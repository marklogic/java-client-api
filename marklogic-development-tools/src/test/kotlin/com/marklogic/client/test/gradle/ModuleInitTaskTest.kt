/*
 * Copyright 2018 MarkLogic Corporation
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
package com.marklogic.client.test.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.Test

import org.junit.Assert.assertTrue
import java.io.File

class ModuleInitTaskTest {
  @get:Rule
  val testDir = TemporaryFolder()

  var isInitialized = false

  lateinit var testEnv: TestDir

  class TestDir {
    val baseName = "getSessionField"

    val srcDir: File
    val buildFile: File
    val propsFile: File
    val sjsOutDir: File
    val xqyOutDir: File
    val sjsAPIFile: File
    val xqyAPIFile: File

    constructor(testDir: TemporaryFolder) {
      val sourcePath = "ml-modules/root/dbfunctiondef/positive/sessions/"
      val apiFilename = baseName + ".api"

      buildFile = testDir.newFile("build.gradle")
      propsFile = testDir.newFile("gradle.properties")
      srcDir = testDir.newFolder("src")

      sjsOutDir = srcDir.resolve("sjs")
      xqyOutDir = srcDir.resolve("xqy")

      sjsOutDir.mkdirs()
      xqyOutDir.mkdirs()

      sjsAPIFile = sjsOutDir.resolve("apiFilename")
      xqyAPIFile = xqyOutDir.resolve("apiFilename")

      val srcAPIFile = File("src/test/" + sourcePath + apiFilename)
      srcAPIFile.copyTo(sjsAPIFile)
      srcAPIFile.copyTo(xqyAPIFile)
    }
  }

  fun initTestEnv() {
    if (!isInitialized) {
      testEnv = TestDir(testDir)
      isInitialized = true
    }
  }

  @Test
  fun testTaskInitSJS() {
    initTestEnv()

    runTest(testEnv.sjsAPIFile, "sjs", testEnv.buildFile, testEnv.sjsOutDir)
  }

  @Test
  fun testTaskInitXQY() {
    initTestEnv()

    runTest(testEnv.xqyAPIFile, "xqy", testEnv.buildFile, testEnv.xqyOutDir)
  }

  fun runTest(apiFile: File, modExtension: String, buildFile: File, outDir: File) {
    buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}

task initModule(type: com.marklogic.client.tools.gradle.ModuleInitTask) {
    functionFile    = '${apiFile.path}'
    moduleExtension = '${modExtension}'
}
""")
    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("initModule")
        .withDebug(true)
        .build()
    val modFile = outDir.resolve(testEnv.baseName + modExtension)
    assertTrue("buildscript did not generate ${modFile.path}", modFile.exists())

    buildFile.delete()
    modFile.delete()
  }
}