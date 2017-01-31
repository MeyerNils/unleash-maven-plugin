package com.itemis.maven.plugins.unleash.steps.checks;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.itemis.maven.aether.ArtifactCoordinates;
import com.itemis.maven.aether.ArtifactResolver;
import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import com.itemis.maven.plugins.cdi.logging.Logger;
import com.itemis.maven.plugins.unleash.ReleaseMetadata;
import com.itemis.maven.plugins.unleash.ReleasePhase;
import com.itemis.maven.plugins.unleash.util.PomUtil;
import com.itemis.maven.plugins.unleash.util.functions.ProjectToString;
import com.itemis.maven.plugins.unleash.util.predicates.IsSnapshotProject;

/**
 * Checks the aether for already released artifacts. This check comprises only modules that are scheduled for release
 * and leaves out others.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 */
@ProcessingStep(id = "checkAether", description = "Checks the aether for already released artifacts. The goal is to ensure that the artifacts produced by this release build can be deployed safely to the aether.", requiresOnline = true)
public class CheckAether implements CDIMojoProcessingStep {
  @Inject
  private Logger log;
  @Inject
  private ReleaseMetadata metadata;
  @Inject
  @Named("reactorProjects")
  private List<MavenProject> reactorProjects;
  @Inject
  private ArtifactResolver artifactResolver;
  @Inject
  @Named("allowLocalReleaseArtifacts")
  private boolean allowLocalReleaseArtifacts;

  @Override
  public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
    this.log.info("Checking aether for already released artifacts of modules that are scheduled for release.");
    this.log.debug(
        "\t=> If any of the modules had already been released with the corresponding release version, the release build will fail fast at this point.");

    Collection<MavenProject> snapshotProjects = Collections2.filter(this.reactorProjects, IsSnapshotProject.INSTANCE);

    List<ArtifactCoordinates> remotelyReleasedProjects = Lists.newArrayList();
    List<ArtifactCoordinates> locallyReleasedProjects = Lists.newArrayList();
    for (MavenProject p : snapshotProjects) {
      this.log.debug("\tChecking module '" + ProjectToString.INSTANCE.apply(p) + "'");
      ArtifactCoordinates calculatedCoordinates = this.metadata
          .getArtifactCoordinatesByPhase(p.getGroupId(), p.getArtifactId()).get(ReleasePhase.RELEASE);
      if (isRemotelyReleased(calculatedCoordinates.getGroupId(), calculatedCoordinates.getArtifactId(),
          calculatedCoordinates.getVersion())) {
        remotelyReleasedProjects.add(calculatedCoordinates);
      } else if (isLocallyReleased(calculatedCoordinates.getGroupId(), calculatedCoordinates.getArtifactId(),
          calculatedCoordinates.getVersion())) {
        locallyReleasedProjects.add(calculatedCoordinates);
      }
    }

    handleRemoteReleases(remotelyReleasedProjects);
    handleLocalReleases(locallyReleasedProjects);
  }

  private void handleRemoteReleases(List<ArtifactCoordinates> remotelyReleasedProjects) throws MojoFailureException {
    if (!remotelyReleasedProjects.isEmpty()) {
      this.log.error("\tThe following artifacts are already present in one of your remote repositories:");
      for (ArtifactCoordinates c : remotelyReleasedProjects) {
        this.log.error("\t\t" + c);
      }
      throw new MojoFailureException(
          "Some of the reactor projects have already been released. Please check your remote repositories!");
    }
  }

  private void handleLocalReleases(List<ArtifactCoordinates> locallyReleasedProjects) throws MojoFailureException {
    if (!locallyReleasedProjects.isEmpty()) {
      if (this.allowLocalReleaseArtifacts) {
        this.log.warn(
            "\tLocal release artifacts are allowed but just for information, the following artifacts are already present in your local repository:");
        for (ArtifactCoordinates c : locallyReleasedProjects) {
          this.log.warn("\t\t" + c);
        }
      } else {
        this.log.error("\tThe following artifacts are already present in your local repository:");
        for (ArtifactCoordinates c : locallyReleasedProjects) {
          this.log.error("\t\t" + c);
        }
        throw new MojoFailureException(
            "Some of the reactor projects have already been released locally. Please check your local repository!");
      }
    }
  }

  private boolean isRemotelyReleased(String groupId, String artifactId, String version) {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(groupId, artifactId, version, PomUtil.ARTIFACT_TYPE_POM);
    Optional<File> pom = this.artifactResolver.resolve(coordinates, true);
    return pom.isPresent();
  }

  private boolean isLocallyReleased(String groupId, String artifactId, String version) {
    ArtifactCoordinates coordinates = new ArtifactCoordinates(groupId, artifactId, version, PomUtil.ARTIFACT_TYPE_POM);
    Optional<File> pom = this.artifactResolver.resolve(coordinates, false);
    return pom.isPresent();
  }
}
