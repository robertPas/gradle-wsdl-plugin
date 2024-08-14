gradle-wsdl-plugin
==================

Fork of [https://github.com/scubacabra/gradle-wsdl-plugin](https://github.com/scubacabra/gradle-wsdl-plugin), which was adapted to work with Gradle 8.

:boom: :collision:

:exclamation:IMPORTANT PLUGIN ID CHANGES:exclamation:

In compliance with the gradle plugin submission guidelines, this
plugin's id is now *fully* qualified.

It changed from `wsdl` to `com.github.jacobono.wsdl`.  This affects
how you apply the plugin (`apply plugin: 'com.github.jacobono.wsdl`)

:boom: :collision:

Gradle plugin that defines some conventions for web service Projects.
Eases the manual configuration of web service project by: 

* Hooking in ant tasks to parse the wsdl with wsimport
* Defining a convention to generate a correct WSDL name for the project,
  by parsing the project-name itself
* Automatically finding web Service dependencies in the WSDL and
  associated schema it imports/includes
* Populating a WAR file with all dependent Web Service Files (WSDL, XSD).

Using The Plugin
================
```groovy
buildscript {
	repositories { 
		maven {
			url "https://repo.comm-unity.at/nexus/content/groups/public"
			content { includeGroupByRegex "at\\.comm_unity.*" }
		}
		mavenCentral()
	}

	dependencies {
		classpath 'at.comm_unity.com.github.jacobono:gradle-wsdl-plugin:1.7.8.1'
	}
}

apply plugin: 'at.comm_unity.com.github.jacobono.wsdl'
```

Setting Up The jaxws Configurations
===================================

You *need* the jaxws configuration to run the `wsimport` task, but that is the
only task that has an external dependency. Any version of jaxws that you care to
use will work.  I try to stay with the latest releases.

```groovy
    dependencies { 
      jaxws 'com.sun.xml.ws:jaxws-tools:2.2.8-promoted-b131'
      jaxws 'com.sun.xml.ws:jaxws-rt:2.2.8-promoted-b131'
    }
```

Plugin Tasks
============

* `wsimport`
	- runs wsimport ant task on the WSDL file name.
	- needs to be run manually.
	- obviously if you have an updated WSDL, you need to `wsimport` before you
      package up a `war`
* `war`
	- automatically generates the war like a regular WAR would, but also
      populates the war with all the files the wsdl depends on, in the exact
      structure as it is present on the filesystem.
	- can be run manually, but is already hooked into the `build` task
      automatically!

Artifact Output
===============

The war would look like this (see the *hello-world-episode-binding-ws* project
in the examples folder)

      drwxr-xr-x         0  20-Jan-2013  21:19:26  META-INF/
      -rw-r--r--        25  20-Jan-2013  21:19:26  META-INF/MANIFEST.MF
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/classes/
      drwxr-xr-x         0  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/
      drwxr-xr-x         0  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/
      drwxr-xr-x         0  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/ibm/
      drwxr-xr-x         0  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/ibm/com/
      -rw-r--r--       993  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/ibm/com/HelloWorld.class
      -rw-r--r--      2194  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/ibm/com/HelloWorldService.class
      -rw-r--r--      1837  19-Jan-2013  21:00:16  WEB-INF/classes/helloworld/sample/ibm/com/ObjectFactory.class
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/lib/
      -rw-r--r--      2674  19-Jan-2013  21:00:14  WEB-INF/lib/hello-world-schema-0.1.jar
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/wsdl/
      -rw-r--r--      2049  19-Jan-2013  20:56:44  WEB-INF/wsdl/HelloWorldEpisodeBindingService.wsdl
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/schema/
      drwxr-xr-x         0  20-Jan-2013  21:19:26  WEB-INF/schema/HelloWorld/
      -rw-r--r--       581  19-Jan-2013  20:56:44  WEB-INF/schema/HelloWorld/HelloWorld.xsd
      - ----------  --------  -----------  --------  -----------------------------------------------------------------

`wsdl` and `schema` folders are auto populated on the fly :)

Schema Organization
===================

## The Problem of Schema Document Duplication

This is probably the biggest reason for this plugin and what it attempts to do
-- have some sort of  standard for xsd and wsdl projects that use schema to
source generation and episode binding, eliminating document and generated code
duplication.

I have seen a few ways of managing schema documents in projects.  Some
duplicated xsds in the xsd project **AND** in the wsdl project, so that the WSDL
file could see those xsds.  Hard. To. Manage.

I have found that the easiest way to keep things **DRY** is to have two folders
at the root of the repository.  Which is great, but the same code ended up being
replicated for differnet projects, hence the plugin to stop the copypasta.

## Conventions I Like To Use

These are defaulted into the plugin, but you can change the naming convention,
the location convention (under a project, not under the root), or change both.

* rootDir
    * wsdl -> contains all wsdl files, all listed directly under this folder.
    * schema -> contains all schema documents (xsds), in subfolders or direct
        * episodes -> holds episode files generated by jaxb
    	* bindings -> special bindings you might write for jaxb parsing
	

With this folder layout, any subproject can know where the documents are with
`project.rootDir`, and the wsdl and xsd imports/includes can be written and
won't ever have to change.

### Schema Projects

With this convention, you can have any number of schema projects and generate
code through jaxb.  I wrote a plugin 
[gradle-jaxb-plugin](https://github.com/jacobono/gradle-wsdl-plugin) that
handles all of the steps listed below.

* generate source code from schemas
* generate episode files to `schema/episodes`
* jar up the classes as a library to re-use in wsdl projects.

This minimizes the duplicate code regeneration **like crazy**

Look at the [examples folder](examples) for some examples using the jaxb plugin.

Plugin Conventions
==================

## WSDL Location Convention

For a WSDL project, there must be at least one WSDL that the project depends
on.  This file needs to be **directly** under the wsdl folder.  Abstract WSDLs
can be saved in subfolder, but the main WSDL for the project requires this
location convention.

## WSDL Naming Conventions

These conventions are not definable -- the WSDL file **MUST**:

* Begin with a capitalized letter
* Use camel case (every word has their first letter capitalized)
* End with "Service"
* Have a .wsdl extension

i.e.
	`ChuckNorrisRoundhouseKickToTheFaceService.wsdl`

## Project Naming Conventions

The project name **MUST** also follow these conventions to find the WSDL file correctly:

* project name is all lower case
* every word in project name has a '-' (hyphen) to divide the words
* must end in '-ws' (signifying that this is a web-service project)

i.e.
	`chuck-norris-roundhouse-kick-to-the-face-ws`

### What This Convention Really Helps With

Applying this plugin to all projects with the `-ws` suffix.

```groovy
subprojects { project ->
  if(project.name.endsWith("-ws")) { 
    apply plugin: 'com.github.jacobono.wsdl'

    dependencies { 
      jaxws 'com.sun.xml.ws:jaxws-tools:2.2.8-promoted-b131'
      jaxws 'com.sun.xml.ws:jaxws-rt:2.2.8-promoted-b131'
    }
  }
}
```

## Plugin Conventions

Listed in the following sections are the default conventions that are possible
to override if desired.

There is a nested configuration, with the `wsdl` extension being the parent to the
`wsimport` extension.  You can change these defaults with a closure in your build
script.

```groovy
    wsdl {
	  ...
      wsimport {
	    ...
	  }
    }
```
	
### WSDL Plugin Conventions

There are 4 overridable defaults that declare the location defaults above.
These defaults are changed via the `wsdl` closure.

* wsdlFolder
  * **ALWAYS** relative to `project.rootDir
	* i.e. "wsdl" or "WSDL", or "web-services" etc.
* schemaFolder
  * **ALWAYS** relative to `project.rootDir
 	* i.e "schema", "XMLSchema", "xsd"
* episodeFolder
  * **ALWAYS** relative to `project.rootDir
	* All episode files go directly under here, no subfolders.
	* i.e. "episodes", "schema/episodes", "xsd/episodes", "XMLSchema/epiosdes"

### ws-import Conventions

These defaults are changed via the nested `wsimport` closure.
Several boolean sensible defaults are defined to be passed into the wsimport task:

* `verbose`
* `keep`
* `xnocompile`
* `fork`
* `xdebug`
* `xadditionalHeaders`
 
And a few other String defaults
    
* `sourceDestionationDirectory`
* `target`
* `wsdlLocation`

`sourceDestionationDirectory` is relative to `project.projectDir`.  It defaults to `src/main/java`, but can be set to anywhere in the `project.projectDir`.

Optional Parameters

* `encoding`: Set the encoding name for generated sources, such as `UTF-8`.
   Default value is the platform default (which you really really really don't want if you have developers with different OSs, trust me).
* `package`: The target package name for the generated classes. If left empty, the package name will be derived from the WSDL file.


For more information on the jaxws wsimport ant task options, visit [here](http://jax-ws.java.net/2.2.3/docs/wsimportant.html)

### Default Conventions

These are the current default conventions:

```groovy
wsdl {
  wsdlFolder	= "wsdl"
  schemaFolder	= "schema"
  episodeFolder = "schema/episodes"
  nameRules     = [:]
  episodes      = []
  wsimport {
    sourceDestinationDirectory = "src/main/java"
    verbose		= true
    keep		= true	
    xnocompile	= true
    fork		= false
    xadditionalHeaders  = false
    xdebug		= false
    target		= "2.1"
    wsdlLocation = "FILL_IN_BY_SERVER"
  }
}
```

Other Features
==============

## Got a really REALLY long WSDL name??

I have seen WSDL names that can be really long, like *REALLY* long. This becomes
a problem with the project name and wsdl naming conventions.  Before you know
it, you could have a WSDL file

  `ProjectNameIsSoLongDataManagementService.wsdl`

With a corresponding project name of

   `project-name-is-so-long-data-management-ws`

This is just too long of a project name for me (the WSDL can be that long if
that is how it is named, but not the project name)!

A tidy feature I added is something called a "nameRule". This allows you to
specify a map of strings to transform in your project name to get the correct
wsdl name.

Basically, you can configure the extension closure with something like:

```groovy
wsdl {
     nameRules = ["-dm" : "DataManagement", "-isl" : "IsSoLong"]
}
```
Now, your project name becomes 

`project-name-isl-dm-ws`

Which is pretty cryptic, yes, but it really reduces the length of your project
name.  And usually, a project will dictate a Service naming convention like
"DataManagement" or "TransactionProcessing" that you can put into a name rule.

I happen to _quite_ like this.

## Binding Previously Genrated Episode Files in JAX-WS

A user can define the episodes wished to be bound (to prevent re-generated
duplicates that have already been generated by the jaxb task).  This is the
`episodes` property, which is just a list of defined episodes, with their extension.

Configure the wsimport task to bind with episode files located under `episodeDirectory` with

```groovy
wsdl {
  episodes = ["name-of-episode-file.episode"]
}
```

or if there are many episode files,

```groovy
wsdl {
  episodes = ["file1", "file2", "file3"].collect { it + ".episode" }
}
```

Examples
========

You can find some examples in the [examples folder](examples)

Improvements
============

If you think this plugin could be better, please fork it! If you have an idea
that would make something a little easier, I'd love to hear about it.

In my head, I see a few [possible improvements](docs/possible-improvements.md)
