/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
