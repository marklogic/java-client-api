/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.tools

import com.marklogic.client.tools.proxy.Generator

fun main(args: Array<String>) {
  if (args.size == 2) {
    Generator().endpointDeclToModStubImpl(args[0], args[1])
  } else {
    throw IllegalArgumentException("usage: fnmodinit endpointDeclarationFile moduleExtension")
  }
}
