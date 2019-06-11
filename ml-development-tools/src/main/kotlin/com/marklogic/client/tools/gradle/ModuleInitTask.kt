/*
 * Copyright 2018-2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.tools.gradle

import com.marklogic.client.tools.proxy.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class ModuleInitTask : DefaultTask() {
  val generator = Generator()

  var endpointDeclarationFile: String = ""
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
    if (moduleExtension
            == "") {
      if (project.hasProperty("moduleExtension")) {
        moduleExtension = project.property("moduleExtension") as String
      } else {
        throw IllegalArgumentException("moduleExtension not specified")
      }
    }

    generator.endpointDeclToModStubImpl(endpointDeclarationFile, moduleExtension)
  }
}