package com.marklogic.client.test.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

public class ServiceCompareTaskTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    boolean isInitialized = false;

    TestDir testEnv;

    class TestDir {
        File srcDir;
        File baseServiceDir;
        File customServiceDir;
        File otherServiceDir;
        File buildFile;
        File propsFile;

        TestDir(TemporaryFolder testDir) throws IOException {
            String baseSourcePath   = "ml-modules/root/dbfunctiondef/positive/decoratorBase/";
            String customSourcePath = "ml-modules/root/dbfunctiondef/positive/decoratorCustom/";
            String otherSourcePath  = "ml-modules/root/dbfunctiondef/positive/mimetype/";

            srcDir = testDir.newFolder("src");

            baseServiceDir   = new File(srcDir, baseSourcePath);
            customServiceDir = new File(srcDir, customSourcePath);
            otherServiceDir  = new File(srcDir, otherSourcePath);

            buildFile = testDir.newFile("build.gradle");
            propsFile = testDir.newFile("gradle.properties");

            baseServiceDir.mkdirs();
            customServiceDir.mkdirs();
            otherServiceDir.mkdirs();

            GradleTestUtil.copyFiles(new File("src/test/" + baseSourcePath),   baseServiceDir);
            GradleTestUtil.copyFiles(new File("src/test/" + customSourcePath), customServiceDir);
            GradleTestUtil.copyFiles(new File("src/test/" + otherSourcePath),  otherServiceDir);
        }
    }

    public void initTestEnv() throws IOException {
        if (!isInitialized) {
            testEnv = new TestDir(testDir);
            isInitialized = true;
        }
    }

    // positive tests //////////////////////////////////////
    @Test
    public void testTaskCheckTwoArg() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n")
                .append("task serviceCompareTest(type: com.marklogic.client.tools.gradle.ServiceCompareTask) {\n")
                .append("  customSeviceDeclarationFile = '"+testEnv.customServiceDir.getPath()+"/service.json'\n")
                .append("  baseSeviceDeclarationFile   = '"+testEnv.baseServiceDir.getPath()+"/service.json'\n")
                .append("}\n");
        writeBuildFile(buildText);

        BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments("serviceCompareTest")
                .withDebug(true)
                .build();

        assertTrue("two argument task failed", result.getOutput().contains("BUILD SUCCESSFUL"));

        testEnv.buildFile.delete();
    }
    @Test
    public void testTaskCheckOneArg() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n")
                .append("task serviceCompareTest(type: com.marklogic.client.tools.gradle.ServiceCompareTask) {\n")
                .append("  customSeviceDeclarationFile = '"+testEnv.customServiceDir.getPath()+"/service.json'\n")
                .append("}\n");
        writeBuildFile(buildText);

        BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments("serviceCompareTest")
                .withDebug(true)
                .build();

        assertTrue("one argument task failed", result.getOutput().contains("BUILD SUCCESSFUL"));

        testEnv.buildFile.delete();
    }
    @Test
    public void testCommandLineCheckTwoArg() throws IOException {
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
                    "-PcustomSeviceDeclarationFile="+testEnv.customServiceDir.getPath()+"/service.json",
                    "-PbaseSeviceDeclarationFile="+testEnv.baseServiceDir.getPath()+"/service.json",
                    "checkCustomService"
                    )
                .withDebug(true)
                .build();

        assertTrue("two argument command failed", result.getOutput().contains("BUILD SUCCESSFUL"));

        testEnv.buildFile.delete();
    }
    @Test
    public void testCommandLineCheckOneArg() throws IOException {
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
                        "-PcustomSeviceDeclarationFile="+testEnv.customServiceDir.getPath()+"/service.json",
                        "checkCustomService"
                )
                .withDebug(true)
                .build();

        assertTrue("one argument command failed", result.getOutput().contains("BUILD SUCCESSFUL"));

        testEnv.buildFile.delete();
    }

    // negative tests //////////////////////////////////////
    @Test
    public void testTaskCheckNonExistentBase() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n")
                .append("task serviceCompareTest(type: com.marklogic.client.tools.gradle.ServiceCompareTask) {\n")
                .append("  customSeviceDeclarationFile = '"+testEnv.customServiceDir.getPath()+"/service.json'\n")
                .append("  baseSeviceDeclarationFile   = '/a/non-existent/service.json'\n")
                .append("}\n");
        writeBuildFile(buildText);

        try {
            BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments("serviceCompareTest")
                .withDebug(true)
                .build();
            fail("non-existent task succeeded: "+result.getOutput());
        } catch (Exception e) {
            String msg = e.getMessage();
            assertTrue("no non-existent task failure",
                    msg.contains(":serviceCompareTest FAILED"));
            assertTrue("no file missing for non-existent task",
                    msg.contains("No such file or directory"));
        }

        testEnv.buildFile.delete();
    }
    @Test
    public void testCommandLineCheckNonExistentBase() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n");
        writeBuildFile(buildText);

        try {
            BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments(
                        "-PcustomSeviceDeclarationFile="+testEnv.customServiceDir.getPath()+"/service.json",
                        "-PbaseSeviceDeclarationFile=/a/non-existent/service.json",
                        "checkCustomService"
                )
                .withDebug(true)
                .build();
            fail("non-existent command succeeded: "+result.getOutput());
        } catch (Exception e) {
            String msg = e.getMessage();
            assertTrue("no non-existent command failure",
                    msg.contains(":checkCustomService FAILED"));
            assertTrue("no file missing for non-existent command",
                    msg.contains("No such file or directory"));
        }

        testEnv.buildFile.delete();
    }
    @Test
    public void testTaskCheckInvalidBase() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n")
                .append("task serviceCompareTest(type: com.marklogic.client.tools.gradle.ServiceCompareTask) {\n")
                .append("  customSeviceDeclarationFile = '"+testEnv.customServiceDir.getPath()+"/service.json'\n")
                .append("  baseSeviceDeclarationFile   = '"+testEnv.otherServiceDir.getPath()+"/service.json'\n")
                .append("}\n");
        writeBuildFile(buildText);

        try {
            BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments("serviceCompareTest")
                .withDebug(true)
                .build();
            fail("invalid task succeeded: "+result.getOutput());
        } catch (Exception e) {
            String msg = e.getMessage();
            assertTrue("no invalid task failure",
                    msg.contains(":serviceCompareTest FAILED"));
            assertTrue("no custom-only function for invalid task",
                    msg.contains("exists in custom service but not base service"));
            assertTrue("no base-only function for invalid task",
                    msg.contains("exists in base service but not custom service"));
        }

        testEnv.buildFile.delete();
    }
    @Test
    public void testCommandLineCheckInvalidBase() throws IOException {
        initTestEnv();

        StringBuilder buildText = new StringBuilder()
                .append("plugins {\n")
                .append("  id 'com.marklogic.ml-development-tools'\n")
                .append("}\n");
        writeBuildFile(buildText);

        try {
            BuildResult result = GradleRunner
                .create()
                .withProjectDir(testDir.getRoot())
                .withPluginClasspath()
                .withArguments(
                        "-PcustomSeviceDeclarationFile="+testEnv.customServiceDir.getPath()+"/service.json",
                        "-PbaseSeviceDeclarationFile="+testEnv.otherServiceDir.getPath()+"/service.json",
                        "checkCustomService"
                )
                .withDebug(true)
                .build();
            fail("invalid command succeeded: "+result.getOutput());
        } catch (Exception e) {
            String msg = e.getMessage();
            assertTrue("no invalid command failure",
                    msg.contains(":checkCustomService FAILED"));
            assertTrue("no custom-only function for invalid command",
                    msg.contains("exists in custom service but not base service"));
            assertTrue("no base-only function for invalid command",
                    msg.contains("exists in base service but not custom service"));
        }

        testEnv.buildFile.delete();
    }

/* TODO:
  negative
    custom function declaration differs from base function declaration
    custom module extension differs from declared endpoint extension
    custom module extension differs from base endpoint extension with no endpointExtension declaration
 */

    public void writeBuildFile(StringBuilder text) throws IOException {
        GradleTestUtil.writeTextFile(text.toString(), testEnv.buildFile);
    }
}
