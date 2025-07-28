/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;

public class EndpointProxiesGenTaskTest {
  @Rule
  public TemporaryFolder testDir = new TemporaryFolder();

  boolean isInitialized = false;

  TestDir testEnv;

  class TestDir {
    File srcDir;
    File serviceDir;
    File javaBaseDir;
    File buildFile;
    File propsFile;
    File outClass;

    TestDir(TemporaryFolder testDir) throws IOException {
      String sourcePath = "ml-modules/root/dbfunctiondef/positive/sessions/";

      srcDir = testDir.newFolder("src");
      serviceDir  = new File(srcDir, sourcePath);
      javaBaseDir = new File(srcDir, "main/java");
      buildFile = testDir.newFile("build.gradle");
      propsFile = testDir.newFile("gradle.properties");
      outClass  = new File(javaBaseDir, "com/marklogic/client/test/dbfunction/positive/SessionsBundle.java");

      serviceDir.mkdirs();

      GradleTestUtil.copyFiles(new File("src/test/" + sourcePath), serviceDir);

      javaBaseDir.mkdirs();
    }
  }

  public void initTestEnv() throws IOException {
    if (!isInitialized) {
      testEnv = new TestDir(testDir);
      isInitialized = true;
    }
  }

  @Test
  public void testTaskInit() throws IOException {
    initTestEnv();

    StringBuilder buildText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n")
      .append("task generateTestProxy(type: com.marklogic.client.tools.gradle.EndpointProxiesGenTask) {\n")
      .append("  serviceDeclarationFile = '"+testEnv.serviceDir.getPath()+"/service.json'\n")
      .append("  javaBaseDirectory      = '"+testEnv.javaBaseDir.getPath()+"'\n")
      .append("}\n");
    writeBuildFile(buildText);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments("generateTestProxy")
        .withDebug(true)
        .build();
    assertTrue("task init did not generate "+testEnv.outClass.getPath(), testEnv.outClass.exists());

    testEnv.buildFile.delete();
    testEnv.outClass.delete();
  }

  @Test
  public void testCommandLineInit() throws IOException {
    initTestEnv();

    StringBuilder buildText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n");
    writeBuildFile(buildText);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments(
            "-PserviceDeclarationFile="+testEnv.serviceDir.getPath()+"/service.json",
            "-PjavaBaseDirectory="+testEnv.javaBaseDir.getPath(),
            "generateEndpointProxies"
           )
        .withDebug(true)
        .build();
    assertTrue("command line did not generate "+testEnv.outClass.getPath(), testEnv.outClass.exists());

    testEnv.buildFile.delete();
    testEnv.outClass.delete();
  }

  @Test
  public void testPropertiesFile() throws IOException {
    initTestEnv();

    StringBuilder fileText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n");
    writeBuildFile(fileText);

    fileText = new StringBuilder()
      .append("serviceDeclarationFile="+testEnv.serviceDir.getPath()+"/service.json\n")
      .append("javaBaseDirectory="+testEnv.javaBaseDir.getPath()+"\n")
      .append("}\n");
    GradleTestUtil.writeTextFile(fileText.toString(), testEnv.propsFile);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments("generateEndpointProxies")
        .withDebug(true)
        .build();
    assertTrue("config did not generate "+testEnv.outClass.getPath(), testEnv.outClass.exists());

    testEnv.buildFile.delete();
    testEnv.propsFile.delete();
    testEnv.outClass.delete();
  }

  @Test
  public void testConfig() throws IOException {
    initTestEnv();

    StringBuilder buildText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n")
      .append("ext {\n")
      .append("    endpointProxiesConfig {\n")
      .append("        serviceDeclarationFile = '"+testEnv.serviceDir.getPath()+"/service.json'\n")
      .append("        javaBaseDirectory      = '"+testEnv.javaBaseDir.getPath()+"'\n")
      .append("    }\n")
      .append("}\n");
    writeBuildFile(buildText);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments("generateEndpointProxies")
        .withDebug(true)
        .build();
    assertTrue("config did not generate "+testEnv.outClass.getPath(), testEnv.outClass.exists());

    testEnv.buildFile.delete();
    testEnv.outClass.delete();
  }

  @Test
  public void testJavaDefault() throws IOException {
    initTestEnv();

    StringBuilder buildText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n")
      .append("task generateTestProxy(type: com.marklogic.client.tools.gradle.EndpointProxiesGenTask) {\n")
      .append("  serviceDeclarationFile = '"+testEnv.serviceDir.getPath()+"/service.json'\n")
      .append("}\n");
    writeBuildFile(buildText);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments("generateTestProxy")
        .withDebug(true)
        .build();
    assertTrue("buildscript did not generate "+testEnv.outClass.getPath(), testEnv.outClass.exists());

    testEnv.buildFile.delete();
    testEnv.outClass.delete();
  }

  public void writeBuildFile(StringBuilder text) throws IOException {
    GradleTestUtil.writeTextFile(text.toString(), testEnv.buildFile);
  }
}
