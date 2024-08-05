/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.tools.gradle

import com.marklogic.client.tools.proxy.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class EndpointProxiesGenTask : DefaultTask() {
  private val generator = Generator()

  @Input
  var serviceDeclarationFile: String = ""
  @Input
  var javaBaseDirectory:      String = ""

  @TaskAction
  fun serviceBundleToJava() {
    val proxiesConfig = project.property("endpointProxiesConfig") as EndpointProxiesConfig
    if (serviceDeclarationFile == "") {
      serviceDeclarationFile = when {
        proxiesConfig.serviceDeclarationFile != ""    -> proxiesConfig.serviceDeclarationFile
        project.hasProperty("serviceDeclarationFile") -> project.property("serviceDeclarationFile") as String
        else -> throw IllegalArgumentException("serviceDeclarationFile not specified")
      }
    }
    if (javaBaseDirectory == "") {
      javaBaseDirectory = when {
        proxiesConfig.javaBaseDirectory != "" -> proxiesConfig.javaBaseDirectory
        project.hasProperty("javaBaseDirectory") -> project.property("javaBaseDirectory") as String
        else -> project.projectDir.resolve("src/main/java").path
      }
    }

    generator.serviceBundleToJava(serviceDeclarationFile, javaBaseDirectory)
  }
}
