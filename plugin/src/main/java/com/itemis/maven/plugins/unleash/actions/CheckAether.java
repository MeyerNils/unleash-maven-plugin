// package com.itemis.maven.plugins.unleash.actions;
//
// import java.io.File;
// import java.util.Collection;
// import java.util.List;
//
// import javax.inject.Inject;
// import javax.inject.Named;
//
// import org.apache.maven.project.MavenProject;
//
// import com.google.common.base.Optional;
// import com.google.common.collect.Collections2;
// import com.google.common.collect.Lists;
// import com.itemis.maven.plugins.cdi.InjectableCdiMojo;
// import com.itemis.maven.plugins.cdi.annotations.MojoExecution;
// import com.itemis.maven.plugins.unleash.util.PomUtil;
// import com.itemis.maven.plugins.unleash.util.ReleaseUtil;
// import com.itemis.maven.plugins.unleash.util.predicates.IsSnapshotProjectPredicate;
//
// @MojoExecution(name = "perform", order = 2)
// public class CheckAether extends AbstractProcessingAction implements InjectableCdiMojo {
// @Inject
// @Named("releaseVersion")
// private String defaultReleaseVersion;
//
// @Inject
// @Named("allowLocalReleaseArtifacts")
// private boolean allowLocalReleaseArtifacts;
//
// @Override
// public void execute() {
// System.out.println("*** 3 ***");
//
// List<MavenProject> alreadyReleasedProjects = Lists.newArrayList();
//
// Collection<MavenProject> snapshotProjects = Collections2.filter(getReactorProjects(),
// new IsSnapshotProjectPredicate());
//
// for (MavenProject project : snapshotProjects) {
// if (isReleased(project.getGroupId(), project.getArtifactId(),
// ReleaseUtil.getReleaseVersion(project.getVersion(), this.defaultReleaseVersion))) {
// alreadyReleasedProjects.add(project);
// }
// }
//
// if (!alreadyReleasedProjects.isEmpty()) {
// getLog().error("The following projects have are already present in one of your remote repositories:");
// for (MavenProject p : alreadyReleasedProjects) {
// getLog().error("\t" + PomUtil.getBasicCoordinates(p));
// }
// getLog().error("");
// throw new IllegalStateException(
// "Some of the reactor projects have already been released. Please check your repositories!");
// }
// }
//
// private boolean isReleased(String groupId, String artifactId, String version) {
// Optional<File> pom = getArtifactResolver().resolve(groupId, artifactId, version,
// Optional.of(PomUtil.ARTIFACT_TYPE_POM), Optional.<String> absent(), this.allowLocalReleaseArtifacts);
// return pom.isPresent();
// }
// }
