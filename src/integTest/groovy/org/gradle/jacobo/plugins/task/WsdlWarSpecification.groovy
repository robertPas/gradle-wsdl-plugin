package org.gradle.jacobo.plugins.task

import org.gradle.jacobo.plugins.ProjectIntegrationSpec
import org.gradle.testkit.runner.GradleRunner

import spock.lang.Unroll

class WsdlWarSpecification extends ProjectIntegrationSpec {

  File rootDir
  File subRootDir
  File buildFile

  def setup() {
    rootDir = getFileFromResourcePath("/test-wsdl-project")
    setRootProject(rootDir)
  }

  @Unroll
  def "run task wsimport for project '#projectName'"() {
    given: "setup sub project and tasks"
    setSubProject(rootProject, projectName, "at.comm_unity.com.github.jacobono.wsdl")
    subRootDir = new File(rootDir, projectName)
    subRootDir.mkdir() 
    buildFile = new File(subRootDir, 'build.gradle')
    buildFile.delete()  // just in case it already exists
    buildFile.createNewFile()
    buildFile << """
        plugins {
            id 'war'
            id 'at.comm_unity.com.github.jacobono.wsdl'
        }
		wsdl {
			wsdlFolder = '../wsdl'
			schemaFolder = '../schema'
		}
    """

    and: "resolved dependencies returns its FileCollection"
    def resolvedDependencies = project.files(
      "../wsdl/IntegrationTestService.wsdl",
      "../schema/Messages/Messages.xsd",
      "../schema/PO/PurchaseOrder.xsd"
    )

    when: "war task is executed"
	def result = GradleRunner.create()
			.forwardStdOutput(new PrintWriter(System.out))
			.forwardStdError(new PrintWriter(System.err))
			.withProjectDir(subRootDir)
			.withArguments('wsdlWar', '--stacktrace')
			.withPluginClasspath()
			.build()

    and: "get a  ziptree of the war"
    def war = project.zipTree("build/libs/" + projectName + ".war")

    then: '''zip tree file names should contain all resolvedDependencies file
names (file names used because the unzipping of a war is in a tmp folder not 
in place)'''
    resolvedDependencies.files.each { file -> war.files.name.contains(file.name) }

    where:
    projectName = "integration-test-ws"
  }
}