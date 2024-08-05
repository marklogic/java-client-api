/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.tools

import com.marklogic.client.tools.proxy.Generator
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  if (args.size == 2) {
    Generator().endpointDeclToModStubImpl(args[0], args[1])
  } else {
    System.err.println("usage: fnmodinit endpointDeclarationFile moduleExtension")
    exitProcess(-1)
  }
}
