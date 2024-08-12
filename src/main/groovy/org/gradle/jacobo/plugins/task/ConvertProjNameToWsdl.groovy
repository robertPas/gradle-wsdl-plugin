package org.gradle.jacobo.plugins.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory

import org.gradle.jacobo.plugins.converter.NameToFileConverter

/**
 * Converts the project name to a WSDL file.
 */
class ConvertProjNameToWsdl extends DefaultTask { 
  static final Logger log = Logging.getLogger(ConvertProjNameToWsdl.class)

  /**
   * Name of the project
   */
  @Input
  String projectName

  /**
   * Directory where the WSDL file resides.
   */
  @OutputDirectory
  File wsdlDirectory

  /**
   * Name rules (if any) to apply to project name conversion
   */
  @Input
  Map<String, String> nameRules
  
  /**
   * Converts the project name to the WSDL File
   */
  @Internal
  NameToFileConverter converter

  /**
   * Executes this task.
   */
  @TaskAction
  void start() {
    def projectName = getProjectName()
    def wsdlDir = getWsdlDirectory()
    def nameRules = getNameRules()
    log.info("looking for wsdl in directory '{}'", wsdlDir)
    def wsdlFile = getConverter().convert(projectName, wsdlDir, nameRules)
    log.info("converted '{}' to wsdl file '{}'", projectName, wsdlFile)
    project.wsdl.wsdlFile = wsdlFile
  }
}