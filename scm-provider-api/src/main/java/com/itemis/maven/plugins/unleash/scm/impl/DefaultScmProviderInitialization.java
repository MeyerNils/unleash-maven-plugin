package com.itemis.maven.plugins.unleash.scm.impl;

import java.io.File;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.itemis.maven.plugins.unleash.scm.ScmProviderInitialization;

/**
 * A simple bean-style default implementation of the {@link ScmProviderInitialization} interface used to pass
 * initialization data to the SCM provider.<br>
 * More advanced implementations could f.i. realize user prompts as password callbacks.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 2.0.0
 */
public class DefaultScmProviderInitialization implements ScmProviderInitialization {
  private File workingDir;
  private Logger logger;
  private String username;
  private String password;
  private String sshPKPassphrase;
  private String sshPrivateKey;

  /**
   * Creates a simple default initialization object with a pre-defined working directory.
   *
   * @param workingDir the directory on which the provider shall work. This directory doesn't have to exist if the
   *          provider is used to checkout things first.
   * @throws IllegalArgumentException if the workingDir is {@code null}.
   */
  public DefaultScmProviderInitialization(File workingDir) throws IllegalArgumentException {
    Preconditions.checkArgument(workingDir != null, "The working directory for the SCM provider must be specified!");
    this.workingDir = workingDir;
  }

  @Override
  public File getWorkingDirectory() {
    return this.workingDir;
  }

  public DefaultScmProviderInitialization setUsername(String username) {
    this.username = username;
    return this;
  }

  @Override
  public Optional<String> getUsername() {
    return Optional.fromNullable(this.username);
  }

  public DefaultScmProviderInitialization setPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  public Optional<String> getPassword() {
    return Optional.fromNullable(this.password);
  }

  public DefaultScmProviderInitialization setSshPrivateKeyPassphrase(String sshPKPassphrase) {
    this.sshPKPassphrase = sshPKPassphrase;
    return this;
  }

  @Override
  public Optional<String> getSshPrivateKeyPassphrase() {
    return Optional.fromNullable(this.sshPKPassphrase);
  }

  public DefaultScmProviderInitialization setSshPrivateKey(String sshPrivateKey) {
    this.sshPrivateKey = sshPrivateKey;
    return this;
  }

  @Override
  public Optional<String> getSshPrivateKey() {
    return Optional.fromNullable(this.sshPrivateKey);
  }

  public DefaultScmProviderInitialization setLogger(Logger logger) {
    this.logger = logger;
    return this;
  }

  @Override
  public Optional<Logger> getLogger() {
    return Optional.fromNullable(this.logger);
  }
}
