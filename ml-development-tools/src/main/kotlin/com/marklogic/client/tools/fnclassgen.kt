/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.tools

import com.marklogic.client.tools.proxy.Generator
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  if (args.size == 2) {
    Generator().serviceBundleToJava(args[0], args[1])
  } else {
    System.err.println("usage: fnclassgen serviceDeclarationFile javaBaseDir")
    exitProcess(-1)
  }
}
