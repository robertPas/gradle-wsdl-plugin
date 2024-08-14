package org.gradle.jacobo.plugins.task

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskExecutionException

import spock.lang.Unroll

import org.gradle.jacobo.plugins.ProjectIntegrationSpec

class ConvertProjectToWsdlFileSpecification extends ProjectIntegrationSpec {

  def setup() {
    def rootDir = getFileFromResourcePath("/test-wsdl-project")
    setRootProject(rootDir)    
  }

  @Unroll
  def "convert '#projectName' to its WSDL file, bad project naming structure"() {
    given: "setup sub project and tasks"
    setSubProject(rootProject, projectName, "com.github.jacobono.wsdl")
    setupProjectTasks()

    when: "convert task is executed"
    convertTask.start()

    then:
//    TaskExecutionException e = thrown()
//    e.cause instanceof GradleException
//    e.cause.message.contains(exceptionContains)
    GradleException e = thrown()
    e.message.contains(exceptionContains)

    where:
    projectName << ["bad-project-name", "no-associated-wsdl-ws"]
    exceptionContains << ["does not conform to the convention", "does not exist"]
  }

  @Unroll
  def '''convert '#projectName' to its WSDL file, no associated wsdl for this 
project based on convention'''() {

    given: "setup sub project and tasks"
    setSubProject(rootProject, projectName, "com.github.jacobono.wsdl")
    setupProjectTasks()

    when: "convert task is executed"
    convertTask.start()

    then:
    project.wsdl.wsdlFile == getFileFromResourcePath("/test-wsdl-project/wsdl/IntegrationTestService.wsdl")

    where:
    projectName = "integration-test-ws"
  }
}