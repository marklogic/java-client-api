/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.tools.gradle

import com.marklogic.client.tools.proxy.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class ServiceCompareTask : DefaultTask() {
  private val generator = Generator()

    @Input
    var customServiceDeclarationFile:  String = ""
    @Input
    var baseServiceDeclarationFile:   String = ""

  @TaskAction
  fun compareCustomServiceToBase() {
        if (customServiceDeclarationFile == "") {
            if (project.hasProperty("customServiceDeclarationFile")) {
                customServiceDeclarationFile = project.property("customServiceDeclarationFile") as String
            } else {
                throw IllegalArgumentException("customServiceDeclarationFile not specified")
            }
        }
        if (baseServiceDeclarationFile == "") {
            if (project.hasProperty("baseServiceDeclarationFile")) {
                baseServiceDeclarationFile = project.property("baseServiceDeclarationFile") as String
            }
        }

        if (baseServiceDeclarationFile == "") {
            generator.compareServices(customServiceDeclarationFile)
        } else {
            generator.compareServices(customServiceDeclarationFile, baseServiceDeclarationFile)
        }
    }
}
