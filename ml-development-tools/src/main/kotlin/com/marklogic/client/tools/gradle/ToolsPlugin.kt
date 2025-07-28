/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.tools.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class ToolsPlugin : Plugin<Project> {
  override fun apply(project: Project) {

    project.extensions.add("endpointProxiesConfig", EndpointProxiesConfig())

    project.tasks.register("generateEndpointProxies", EndpointProxiesGenTask::class.java)
    project.tasks.register("initializeModule",        ModuleInitTask::class.java)
    project.tasks.register("checkCustomService",      ServiceCompareTask::class.java)
  }
}
