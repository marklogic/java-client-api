/*
 * Copyright (c) 2019 MarkLogic Corporation
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