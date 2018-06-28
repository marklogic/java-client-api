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

class GeneratorTaskTest {
  @get:Rule
  val testDir = TemporaryFolder()

  var isInitialized = false

  lateinit var testEnv : TestDir
  class TestDir {
    val srcDir      : File
    val serviceDir  : File
    val javaBaseDir : File
    val buildFile   : File
    val propsFile   : File
    val outClass    : File

    constructor(testDir: TemporaryFolder) {
      val sourcePath = "ml-modules/root/dbfunctiondef/positive/sessions"

      srcDir      = testDir.newFolder("src")
      serviceDir  = srcDir.resolve(sourcePath)
      javaBaseDir = srcDir.resolve("main/java")
      buildFile   = testDir.newFile("build.gradle")
      propsFile   = testDir.newFile("gradle.properties")
      outClass    = javaBaseDir.resolve("com/marklogic/client/test/dbfunction/positive/SessionsBundle.java")

      serviceDir.mkdirs()
      File("src/test/" + sourcePath).copyRecursively(serviceDir, overwrite=true)

      javaBaseDir.mkdirs()
    }
  }

  fun initTestEnv() {
    if (!isInitialized) {
      testEnv       = TestDir(testDir)
      isInitialized = true
    }
  }

  @Test
  fun testTaskInit() {
    initTestEnv()

    testEnv.buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}

task generateTestProxy(type: com.marklogic.client.tools.gradle.GeneratorTask) {
    serviceBundleFilename = '${testEnv.serviceDir.path}/service.json'
    javaBaseDirectory     = '${testEnv.javaBaseDir.path}'
}
""")
    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("generateTestProxy")
        .withDebug(true)
        .build()
    assertTrue("buildscript did not generate ${testEnv.outClass.path}", testEnv.outClass.exists())

    testEnv.buildFile.delete()
    testEnv.outClass.delete()
  }

  @Test
  fun testCommandLineInit() {
    initTestEnv()

    testEnv.buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}
""")

    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments(
            """-PserviceBundleFilename=${testEnv.serviceDir.path}/service.json""",
            """-PjavaBaseDirectory=${testEnv.javaBaseDir.path}""",
            "generateProxy"
           )
        .withDebug(true)
        .build()
    assertTrue("command line did not generate ${testEnv.outClass.path}", testEnv.outClass.exists())

    testEnv.buildFile.delete()
    testEnv.outClass.delete()
  }

  @Test
  fun testPropertiesFile() {
    initTestEnv()

    testEnv.buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}
""")

    testEnv.propsFile.writeText("""
serviceBundleFilename=${testEnv.serviceDir.path}/service.json
javaBaseDirectory=${testEnv.javaBaseDir.path}
""")

    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("generateProxy")
        .withDebug(true)
        .build()
    assertTrue("config did not generate ${testEnv.outClass.path}", testEnv.outClass.exists())

    testEnv.buildFile.delete()
    testEnv.propsFile.delete()
    testEnv.outClass.delete()
  }

  @Test
  fun testConfig() {
    initTestEnv()

    testEnv.buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}

ext {
    proxyConfig {
        serviceBundleFilename = '${testEnv.serviceDir.path}/service.json'
        javaBaseDirectory     = '${testEnv.javaBaseDir.path}'
    }
}
""")

    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("generateProxy")
        .withDebug(true)
        .build()
    assertTrue("config did not generate ${testEnv.outClass.path}", testEnv.outClass.exists())

    testEnv.buildFile.delete()
    testEnv.outClass.delete()
  }

  @Test
  fun testJavaDefault() {
    initTestEnv()

    testEnv.buildFile.writeText("""
plugins {
    id 'com.marklogic.ml-gradle'
    id 'com.marklogic.client.tools'
}

task generateTestProxy(type: com.marklogic.client.tools.gradle.GeneratorTask) {
    serviceBundleFilename = '${testEnv.serviceDir.path}/service.json'
}
""")
    val result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("generateTestProxy")
        .withDebug(true)
        .build()
    assertTrue("buildscript did not generate ${testEnv.outClass.path}", testEnv.outClass.exists())

    testEnv.buildFile.delete()
    testEnv.outClass.delete()
  }
}