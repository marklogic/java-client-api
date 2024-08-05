/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dbfunction.positive;

import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertTrue;

public class DescribedBundleTest {
   DescribedBundle testObj = DescribedBundle.on(DBFunctionTestUtil.db);

   @Test
   public void javaDocTest() throws IOException {
      String className = "/com/marklogic/client/test/dbfunction/positive/DescribedBundle";

      File sourceFile = new File("src/test/java"+className+".java");

      File outputDir  = new File("out/test/javadoc");
      outputDir.mkdirs();

      DocumentationTool       docTool     = ToolProvider.getSystemDocumentationTool();
      StandardJavaFileManager fileManager = docTool.getStandardFileManager(null, null, null);

      Iterable<? extends JavaFileObject> javaFile = fileManager.getJavaFileObjects(sourceFile);
      fileManager.setLocation(DocumentationTool.Location.DOCUMENTATION_OUTPUT, Arrays.asList(outputDir));

      String javaVersion = System.getProperty("java.version");
      int javaMajorVersion = Integer.parseInt(javaVersion.substring(0, javaVersion.indexOf(".")));

      // the default doclet path doesn't seem to have the standard doclet from tools.jar in Java 8
      if (javaMajorVersion < 9) {
         List<File> toolClassPath = getClassPath(ToolProvider.getSystemToolClassLoader());
         List<File> docletClassPath = toList(fileManager.getLocation(DocumentationTool.Location.DOCLET_PATH));
         if (docletClassPath != null) {
            if (toolClassPath != null) {
               docletClassPath.addAll(toolClassPath);
            }
            fileManager.setLocation(DocumentationTool.Location.DOCLET_PATH, docletClassPath);
         } else if (toolClassPath != null) {
            fileManager.setLocation(DocumentationTool.Location.DOCLET_PATH, toolClassPath);
         } else {
            throw new IllegalStateException("cannot establish class path");
         }
      }

      DocumentationTool.DocumentationTask docTask = docTool.getTask(
          null, fileManager, null, null, null, javaFile
          );

      boolean result = docTask.call();
      assertTrue("Failed to generate valid JavaDoc from API file", result);

      File outputFile = new File(outputDir, className+".html");
      assertTrue("Could not find HTML file for JavaDoc generated from API File", outputFile.exists());
   }
   private List<File> getClassPath(ClassLoader loader) {
      if (loader == null) {
         return null;
      }
      return Arrays.stream(((URLClassLoader) loader).getURLs())
                  .map(url -> {
                     try {
                        return Paths.get(url.toURI()).toFile();
                     } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                     }
                  })
                  .collect(Collectors.toList());
   }
   private List<File> toList(Iterable<? extends File> iterable) {
      if (iterable == null) {
         return null;
      }
      ArrayList<File> list = new ArrayList<>();
      iterable.forEach(file -> list.add(file));
      return list;
   }
}
