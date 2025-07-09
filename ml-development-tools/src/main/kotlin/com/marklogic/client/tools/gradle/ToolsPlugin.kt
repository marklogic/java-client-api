/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.tools.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class ToolsPlugin : Plugin<Project> {
  override fun apply(project: Project) {

    project.extensions.add("endpointProxiesConfig", EndpointProxiesConfig())

    project.tasks.create("generateEndpointProxies", EndpointProxiesGenTask::class.java)
    project.tasks.create("initializeModule",        ModuleInitTask::class.java)
    project.tasks.create("checkCustomService",      ServiceCompareTask::class.java)
  }
}
