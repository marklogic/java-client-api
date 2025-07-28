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

public class ModuleInitTaskTest {
  @Rule
  public TemporaryFolder testDir = new TemporaryFolder();

  boolean isInitialized = false;

  TestDir testEnv;

  class TestDir {
    String baseName = "getSessionField";

    File srcDir;
    File buildFile;
    File propsFile;
    File sjsOutDir;
    File xqyOutDir;
    File sjsAPIFile;
    File xqyAPIFile;

    TestDir(TemporaryFolder testDir) throws IOException {
      String sourcePath = "ml-modules/root/dbfunctiondef/positive/sessions/";
      String apiFilename = baseName + ".api";

      buildFile = testDir.newFile("build.gradle");
      propsFile = testDir.newFile("gradle.properties");
      srcDir = testDir.newFolder("src");

      sjsOutDir = new File(srcDir, "sjs");
      xqyOutDir = new File(srcDir, "xqy");

      sjsOutDir.mkdirs();
      xqyOutDir.mkdirs();

      sjsAPIFile = new File(sjsOutDir, apiFilename);
      xqyAPIFile = new File(xqyOutDir, apiFilename);

      File srcAPIFile = new File("src/test/" + sourcePath + apiFilename);
      GradleTestUtil.copyTextFile(srcAPIFile, sjsAPIFile);
      GradleTestUtil.copyTextFile(srcAPIFile, xqyAPIFile);
    }
  }

  public void initTestEnv() throws IOException {
    if (!isInitialized) {
      testEnv = new TestDir(testDir);
      isInitialized = true;
    }
  }

  @Test
  public void testTaskInitSJS() throws IOException {
    initTestEnv();

    runTaskInitTest(testEnv.sjsAPIFile, "sjs", testEnv.buildFile, testEnv.sjsOutDir);
  }

  @Test
  public void testTaskInitXQY() throws IOException {
    initTestEnv();

    runTaskInitTest(testEnv.xqyAPIFile, "xqy", testEnv.buildFile, testEnv.xqyOutDir);
  }

  @Test
  public void testCommandLineInitSJS() throws IOException {
    initTestEnv();

    runCommandLineInitTest(testEnv.sjsAPIFile, "sjs", testEnv.buildFile, testEnv.sjsOutDir);
  }

  @Test
  public void testCommandLineInitXQY() throws IOException {
    initTestEnv();

    runCommandLineInitTest(testEnv.xqyAPIFile, "xqy", testEnv.buildFile, testEnv.xqyOutDir);
  }

  public void runTaskInitTest(File apiFile, String modExtension, File buildFile, File outDir) throws IOException {
    StringBuilder buildText = new StringBuilder()
      .append("plugins {\n")
      .append("  id 'com.marklogic.ml-development-tools'\n")
      .append("}\n")
      .append("task initModuleTest(type: com.marklogic.client.tools.gradle.ModuleInitTask) {\n")
      .append("  endpointDeclarationFile = '"+apiFile.getPath()+"'\n")
      .append("  moduleExtension         = '"+modExtension+"'\n")
      .append("}\n");
    writeBuildFile(buildText);

    BuildResult result = GradleRunner
        .create()
        .withProjectDir(testDir.getRoot())
        .withPluginClasspath()
        .withArguments("initModuleTest")
        .withDebug(true)
        .build();
    File modFile = new File(outDir, testEnv.baseName + "." + modExtension);
    assertTrue("task init did not generate "+modFile.getPath(), modFile.exists());

    buildFile.delete();
    modFile.delete();
  }

  public void runCommandLineInitTest(File apiFile, String modExtension, File buildFile, File outDir) throws IOException {
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
            "-PendpointDeclarationFile="+apiFile.getPath(),
            "-PmoduleExtension="+modExtension,
            "initializeModule"
        )
        .withDebug(true)
        .build();
    File modFile = new File(outDir, testEnv.baseName + "." + modExtension);
    assertTrue("command line did not generate "+modFile.getPath(), modFile.exists());

    buildFile.delete();
    modFile.delete();
  }

  public void writeBuildFile(StringBuilder text) throws IOException {
    GradleTestUtil.writeTextFile(text.toString(), testEnv.buildFile);
  }
}
