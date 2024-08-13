package org.gradle.jacobo.plugins.task

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.UnknownTaskException

import org.gradle.jacobo.plugins.ProjectTaskSpecification

class WsdlWarSpec extends ProjectTaskSpecification {
  
  def someWsdl = new File("some-wsdl.wsdl")
  
  def setup() {
  }

  def "no wsdlWar without war"() {
    buildProject("com.github.jacobono.wsdl")
    
    when:
    task = project.tasks[WsdlWar.TASK_NAME] as WsdlWar

    then:
    thrown(UnknownTaskException)
  }

  // not a whole lot to test besides the inputFiles
  def "war up this wsdl project"() {
    project = ProjectBuilder.builder().build()
    project.apply(plugin: "war")
    project.apply(plugin: "com.github.jacobono.wsdl")

    task = project.tasks[WsdlWar.TASK_NAME] as WsdlWar
    task.with {
      wsdlDependencies = project.files([new File("some-wsdl.wsdl"),
					new File("some-xsd.xsd")])
    }

    expect:
    task.wsdlDependencies != null
    task.wsdlDependencies instanceof FileCollection
  }
}