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

  @Test
  fun testProxyGenerator() {
    val srcDir    = testDir.newFolder("src")
    val outDir    = testDir.newFolder("out")
    val buildFile = testDir.newFile("build.gradle")

    File("src/test/resources/dbfunctiondef/positive/sessions")
        .copyRecursively(srcDir, overwrite=true)

    buildFile.writeText("""
plugins {
    id 'com.marklogic.client.tools'
}

task generateTestProxy(type: com.marklogic.client.tools.gradle.GeneratorTask) {
    serviceBundleFilename = '${srcDir.path}/service.json'
    javaBaseDirectory     = '${outDir.path}'
}
""")
    var result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments("generateTestProxy")
        .withDebug(true)
        .build()

    val outClass = outDir.resolve("com/marklogic/client/test/dbfunction/positive/SessionsBundle.java")
// println(outClass.readText())
// println(result.output)
    assertTrue("buildscript did not generate ${outClass.path}", outClass.exists())

    outClass.delete()

    buildFile.writeText("""
plugins {
    id 'com.marklogic.client.tools'
}
""")

    result = GradleRunner
        .create()
        .withProjectDir(testDir.root)
        .withPluginClasspath()
        .withArguments(
            """-PserviceBundleFilename=${srcDir.path}/service.json""",
            """-PjavaBaseDirectory=${outDir.path}""",
            "generateProxy"
           )
        .withDebug(true)
        .build()
    assertTrue("command line did not generate ${outClass.path}", outClass.exists())
  }
}