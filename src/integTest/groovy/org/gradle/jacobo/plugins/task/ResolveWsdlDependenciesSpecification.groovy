package org.gradle.jacobo.plugins.task

import spock.lang.Unroll

import org.gradle.jacobo.plugins.ProjectIntegrationSpec

class ResolveWsdlDependenciesSpecification extends ProjectIntegrationSpec {

  def setup() {
    def rootDir = getFileFromResourcePath("/test-wsdl-project")
    setRootProject(rootDir)
  }

  @Unroll
  def "resolve wsdl dependencies of '#projectName'"() {
    given: "setup sub project and tasks"
    setSubProject(rootProject, projectName, "com.github.jacobono.wsdl")
    setupProjectTasks()

    // simulate what gradle would do here, dependent Tasks need to run first    
    and: "dependent task is executed"
    convertTask.start()

    when: "resolve task is executed"
    resolveTask.start()

    then: "these are the wsdl dependencies"
    project.wsdl.wsdlDependencies.files == project.files(
      ["/test-wsdl-project/wsdl/IntegrationTestService.wsdl",
       "/test-wsdl-project/schema/Messages/Messages.xsd",
       "/test-wsdl-project/schema/PO/PurchaseOrder.xsd"
      ].collect{ getFileFromResourcePath(it) }
    ).files
    
    where:
    projectName = "integration-test-ws"
  }
}