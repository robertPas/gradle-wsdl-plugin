package org.gradle.jacobo.plugins.task

import spock.lang.Unroll

import org.gradle.jacobo.plugins.ant.AntWsImport

import org.gradle.jacobo.plugins.ProjectIntegrationSpec


class WsImportSpecification extends ProjectIntegrationSpec {

  def wsimport = Mock(AntWsImport)

  def setup() {
    def rootDir = getFileFromResourcePath("/test-wsdl-project")
    setRootProject(rootDir)
    setSubProject(rootProject, "integration-test-ws", "com.github.jacobono.wsdl")
    setupProjectTasks()
  }

  @Unroll
  def "run task wsimport for project '#projectName'"() {
    given: "setup sub project and tasks"
    wsImportTask.antExecutor = wsimport

    // simulate what gradle would do here, dependent Tasks need to run first    
    and: "dependent tasks are executed"
    [convertTask, resolveTask].each { it.start() }

    when: "ws import is executed"
    wsImportTask.start()

    then: "ant executor should only be called once"
    1 * wsimport.execute(*_)
    
  }
}