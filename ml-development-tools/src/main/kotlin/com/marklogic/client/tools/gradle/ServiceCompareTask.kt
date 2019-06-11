/*
 * Copyright 2019 MarkLogic Corporation
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

open class ServiceCompareTask : DefaultTask() {
  val generator = Generator()

  var customSeviceDeclarationFile: String = ""
  var baseSeviceDeclarationFile:   String = ""

  @TaskAction
  fun compareCustomServiceToBase() {
    if (customSeviceDeclarationFile == "") {
      if (project.hasProperty("customSeviceDeclarationFile")) {
                customSeviceDeclarationFile = project.property("customSeviceDeclarationFile") as String
            } else {
                throw IllegalArgumentException("customSeviceDeclarationFile not specified")
            }
        }
        if (baseSeviceDeclarationFile == "") {
            if (project.hasProperty("baseSeviceDeclarationFile")) {
                baseSeviceDeclarationFile = project.property("baseSeviceDeclarationFile") as String
            }
        }

        if (baseSeviceDeclarationFile == "") {
            generator.compareServices(customSeviceDeclarationFile)
        } else {
            generator.compareServices(customSeviceDeclarationFile, baseSeviceDeclarationFile)
        }
    }
}