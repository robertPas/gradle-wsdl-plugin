package org.gradle.jacobo.plugins.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory

import org.gradle.jacobo.plugins.resolver.DependencyResolver

/**
 * Resolves this projects WSDL dependencies.
 */
class WsdlResolveDependencies extends DefaultTask {
  static final Logger log = Logging.getLogger(WsdlResolveDependencies.class)

  /**
   * Absolute path WSDL file.
   */
  @InputFile
  File wsdlFile

  /**
   * Resolves all the WSDL's dependencies.
   */
  @Internal
  DependencyResolver dependencyResolver

  /**
   * Executes this task.
   */
  @TaskAction
  void start() {
    def wsdlDependencies = getDependencyResolver().resolveDependencies(getWsdlFile())
    project.wsdl.wsdlDependencies = project.files(wsdlDependencies)
  }
}