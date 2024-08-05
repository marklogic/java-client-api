/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.tools.gradle

import com.marklogic.client.tools.proxy.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class ModuleInitTask : DefaultTask() {
  private val generator = Generator()

  @Input
  var endpointDeclarationFile: String = ""
  @Input
  var moduleExtension:         String = ""

  @TaskAction
  fun declarationToModuleStub() {
    if (endpointDeclarationFile == "") {
      if (project.hasProperty("endpointDeclarationFile")) {
        endpointDeclarationFile = project.property("endpointDeclarationFile") as String
      } else {
        throw IllegalArgumentException("endpointDeclarationFile not specified")
      }
    }
    if (moduleExtension== "") {
      if (project.hasProperty("moduleExtension")) {
        moduleExtension = project.property("moduleExtension") as String
      } else {
        throw IllegalArgumentException("moduleExtension not specified")
      }
    }

    generator.endpointDeclToModStubImpl(endpointDeclarationFile, moduleExtension)
  }
}
