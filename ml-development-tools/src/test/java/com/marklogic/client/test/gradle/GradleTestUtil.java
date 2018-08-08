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
