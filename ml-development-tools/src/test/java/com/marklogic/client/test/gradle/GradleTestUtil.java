/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.stream.Collectors;

public class GradleTestUtil {
   static public void copyFiles(File in, File out) throws IOException {
      Path outDir = out.toPath();
      try (DirectoryStream<Path> inStream = Files.newDirectoryStream(in.toPath())) {
         for (Path inFile: inStream) {
            Files.copy(inFile, outDir.resolve(inFile.getFileName()),
                  StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES
            );
         }
      }
   }

   static public void copyTextFile(File in, File out) throws IOException {
     Files.copy(in.toPath(), out.toPath(),
           StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES
     );
   }

   static public String readTextFile(File file) throws IOException {
     return Files.lines(file.toPath()).collect(Collectors.joining("\n"));
   }

   static public void writeTextFile(String text, File file) throws IOException {
     Files.write(file.toPath(), text.getBytes(Charset.forName("UTF-8")),
           StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
     );
   }
}
