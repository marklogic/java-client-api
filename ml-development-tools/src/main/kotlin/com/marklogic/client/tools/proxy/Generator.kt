/*
 * Copyright 2018-2019 MarkLogic Corporation
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
package com.marklogic.client.tools.proxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class Generator {
  enum class ValueCardinality { NONE, SINGLE, MULTIPLE }

  fun getJavaConverterName(): Map<String,String> {
    return mapOf(
        "java.io.InputStream"                              to "InputStream",
        "java.io.Reader"                                   to "Reader",
        "com.fasterxml.jackson.databind.JsonNode"          to "JacksonJsonNode",
        "com.fasterxml.jackson.core.JsonParser"            to "JacksonJsonParser",
        "com.fasterxml.jackson.databind.node.ArrayNode"    to "JacksonArrayNode",
        "com.fasterxml.jackson.databind.node.ObjectNode"   to "JacksonObjectNode",
        "org.w3c.dom.Document"                             to "XMLDOMDocument",
        "org.xml.sax.InputSource"                          to "XMLSaxInputSource",
        "javax.xml.transform.Source"                       to "XMLTransformSource",
        "javax.xml.stream.XMLEventReader"                  to "XMLEventReader",
        "javax.xml.stream.XMLStreamReader"                 to "XMLStreamReader",

        "java.math.BigDecimal"                             to "BigDecimal",
        "java.lang.Boolean"                                to "Boolean",
        "Boolean"                                          to "Boolean",
        "boolean"                                          to "Boolean",
        "java.util.Date"                                   to "Date",
        "java.time.Duration"                               to "Duration",
        "java.lang.Double"                                 to "Double",
        "Double"                                           to "Double",
        "double"                                           to "Double",
        "java.lang.Float"                                  to "Float",
        "Float"                                            to "Float",
        "float"                                            to "Float",
        "InputStream"                                      to "InputStream",
        "java.lang.Integer"                                to "Integer",
        "Integer"                                          to "Integer",
        "int"                                              to "Integer",
        "java.time.LocalDate"                              to "LocalDate",
        "java.time.LocalDateTime"                          to "LocalDateTime",
        "java.time.LocalTime"                              to "LocalTime",
        "java.lang.Long"                                   to "Long",
        "Long"                                             to "Long",
        "long"                                             to "Long",
        "java.time.OffsetDateTime"                         to "OffsetDateTime",
        "java.time.OffsetTime"                             to "OffsetTime",
        "Reader"                                           to "Reader",
        "java.lang.String"                                 to "String",
        "String"                                           to "String"
    )
  }
  fun getPrimitiveDataTypes(): Map<String,String> {
    return mapOf(
        "boolean"           to "Boolean",
        "double"            to "Double",
        "float"             to "Float",
        "int"               to "Integer",
        "long"              to "Long",
        "unsignedInt"       to "Integer",
        "unsignedLong"      to "Long"
    )
  }
  fun getAtomicDataTypes(): Map<String,String> {
    return getPrimitiveDataTypes() + mapOf(
        "date"              to "String",
        "dateTime"          to "String",
        "dayTimeDuration"   to "String",
        "decimal"           to "String",
        "string"            to "String",
        "time"              to "String"
    )
  }
  fun getDocumentDataTypes(): Map<String,String> {
    return mapOf(
        "array"             to "Reader",
        "object"            to "Reader",
        "binaryDocument"    to "InputStream",
        "jsonDocument"      to "Reader",
        "textDocument"      to "Reader",
        "xmlDocument"       to "Reader"
    )
  }
  fun getAtomicMappings(): Map<String,Set<String>> {
    return mapOf(
        "boolean"           to setOf(
            "java.lang.Boolean",
            "java.lang.String"),
        "date"              to setOf(
            "java.time.LocalDate",
            "java.lang.String"),
        "dateTime"          to setOf(
            "java.util.Date",
            "java.time.LocalDateTime",
            "java.time.OffsetDateTime",
            "java.lang.String"),
        "dayTimeDuration"   to setOf(
            "java.time.Duration",
            "java.lang.String"),
        "decimal"           to setOf(
            "java.math.BigDecimal",
            "java.lang.String"),
        "double"            to setOf(
            "java.lang.Double",
            "java.lang.String"),
        "float"             to setOf(
            "java.lang.Float",
            "java.lang.String"),
        "int"               to setOf(
            "java.lang.Integer",
            "java.lang.String"),
        "long"              to setOf(
            "java.lang.Long",
            "java.lang.String"),
        "time"              to setOf(
            "java.time.LocalTime",
            "java.time.OffsetTime",
            "java.lang.String"),
        "unsignedInt"       to setOf(
            "java.lang.Integer",
            "java.lang.String"),
        "unsignedLong"      to setOf(
            "java.lang.Long",
            "java.lang.String")
    )
  }
  fun getDocumentMappings(): Map<String,Set<String>> {
    return mapOf(
        "array"             to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.node.ArrayNode",
            "com.fasterxml.jackson.core.JsonParser"),
        "binaryDocument"    to setOf(
            "java.io.InputStream"),
        "jsonDocument"      to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.JsonNode",
            "com.fasterxml.jackson.core.JsonParser"),
        "object"            to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.node.ObjectNode",
            "com.fasterxml.jackson.core.JsonParser"),
        "textDocument"      to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String"),
        "xmlDocument"       to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "org.w3c.dom.Document",
            "org.xml.sax.InputSource",
            "javax.xml.transform.Source",
            "javax.xml.stream.XMLEventReader",
            "javax.xml.stream.XMLStreamReader")
    )
  }
  fun getAllDataTypes(): Map<String,String> {
    return getAtomicDataTypes() + getDocumentDataTypes()
  }
  fun getAllMappings(): Map<String,Set<String>> {
    return getAtomicMappings() + getDocumentMappings()
  }
  fun getJavaDataType(
      dataType: String, mapping: String?, dataKind: String, isMultiple: Boolean
  ): String {
    if (mapping === null) {
      if (dataKind == "system") {
        if (dataType == "session") {
          if (isMultiple) {
            throw IllegalArgumentException("""session data type cannot be  multiple""")
          }
          return "SessionState"
        }
        throw IllegalArgumentException("""unknown system data type: ${dataType}""")
      }
      val datatypeMap = getAllDataTypes()
      val mappedType = datatypeMap[dataType]
      if (mappedType === null) {
        throw IllegalArgumentException("""unknown data type ${dataType}""")
      }
      return mappedType
    }

    val allMappings  = getAllMappings()
    val typeMappings = allMappings[dataType]
    if (typeMappings === null) {
      throw IllegalArgumentException("""no mappings for data type ${dataType}""")
    } else if (!typeMappings.contains(mapping)) {
      throw IllegalArgumentException("""no mapping to ${mapping} for data type ${dataType}""")
    }
    return mapping
  }

  // entry point for EndpointProxiesGenTask
  fun serviceBundleToJava(servDeclFilename: String, javaBaseDir: String) {
    val mapper = jacksonObjectMapper()

    val servDeclFile = File(servDeclFilename)
    val servdef      = mapper.readValue<ObjectNode>(servDeclFile)

    val endpointDirectory = getEndpointDirectory(servDeclFilename, servdef)

    val moduleFiles = mutableMapOf<String, File>()
    val funcdefs    = getFuncdefs(mapper, endpointDirectory, servDeclFile, servdef, moduleFiles)

    val fullClassName = getFullClassName(servDeclFilename, servdef)

    val funcDecl    = mutableListOf<String>()
    val funcDepend  = mutableSetOf<String>()
    val funcSrc     = funcdefs.map{(root, funcdef) ->
      generateFuncSrc(funcDecl, funcDepend, servdef, moduleFiles[root]!!.name, funcdef)
    }.joinToString("\n")
    val funcImports =
        if (funcDepend.isEmpty()) ""
        else "import "+funcDepend.joinToString(";\nimport ")+";\n"
    val funcDecls   =
        if (funcDecl.isEmpty()) ""
        else funcDecl.joinToString("")

    val classSrc = generateServClass(
        servdef, endpointDirectory, fullClassName, funcImports, funcDecls, funcSrc
    )

    writeClass(fullClassName, classSrc, javaBaseDir)
  }
  fun getFuncdefs(
          mapper: ObjectMapper, endpointDirectory: String, servDeclFile: File,
          servdef: ObjectNode, moduleFiles: MutableMap<String, File>
  ): Map<String, ObjectNode> {
    var servExtsn = servdef.get("endpointExtension")?.asText()
    if (servExtsn != null && servExtsn.startsWith("."))
      servExtsn = servExtsn.substring(1)
    val warnings = mutableListOf<String>()
    val endpointDeclFiles = mutableMapOf<String, File>()
    servDeclFile.parentFile.listFiles().forEach{file ->
      val basename = file.nameWithoutExtension
      when(file.extension) {
        "api"               -> endpointDeclFiles[basename] = file
        "mjs", "sjs", "xqy" ->
          if (moduleFiles.containsKey(basename)) {
            throw IllegalArgumentException(
                "can have only one of the ${file.name} and ${moduleFiles[basename]?.name} files"
            )
          } else if (servExtsn != null && file.extension != servExtsn) {
            throw IllegalArgumentException("${file.name} must have ${servExtsn} extension")
          } else {
            moduleFiles[basename] = file
          }
      }
    }
    val moduleRoots   = moduleFiles.keys
    val functionRoots = endpointDeclFiles.keys
    unpairedWarnings(warnings, moduleFiles, functionRoots,
            "endpoint main module without function declaration")

    val funcdefs    = endpointDeclFiles.filter{entry ->
      if (moduleRoots.contains(entry.key)) {
        true
      } else {
        warnings.add("function declaration without endpoint main module: "+entry.value.absolutePath)
        false
      }
    }.mapValues{entry ->
      mapper.readValue<ObjectNode>(entry.value)
    }

    if (warnings.size > 0) {
      System.err.println(warnings.joinToString("""
"""))
    }

    if (funcdefs.size == 0) {
      throw IllegalArgumentException(
              "no proxy declaration with endpoint module found in ${endpointDirectory}"
      )
    }

    return funcdefs
  }
  fun getEndpointDirectory(servDeclFilename: String, servdef: ObjectNode): String {
    var endpointDirectory = servdef.get("endpointDirectory")?.asText()
    if (endpointDirectory === null) {
      throw IllegalArgumentException("no endpointDirectory property in $servDeclFilename")
    } else if (endpointDirectory.length == 0) {
      throw IllegalArgumentException("empty endpointDirectory property in $servDeclFilename")
    } else if (!endpointDirectory.endsWith("/")) {
      endpointDirectory += "/"
    }
    return endpointDirectory
  }
  fun getFullClassName(servDeclFilename: String, servdef: ObjectNode): String {
    val fullClassName = servdef.get("\$javaClass")?.asText()
    if (fullClassName === null) {
      throw IllegalArgumentException("no \$javaClass property in $servDeclFilename")
    }
    return fullClassName
  }
  fun unpairedWarnings(
      warnings: MutableList<String>, files: Map<String, File>, other: Set<String>, msg: String
  ) {
    files.forEach{(root, file) ->
      if (!other.contains(root)) {
        warnings.add(msg+": "+file.absolutePath)
      }
    }
  }
  fun writeClass(fullClassName: String, classSrc: String, javaBaseDir: String) {
    val classFilename = javaBaseDir+if (javaBaseDir.endsWith("/")) {""} else {"/"}+
        fullClassName.replace(".", "/")+".java"
    val classFile     = File(classFilename)
    classFile.parentFile.mkdirs()
    classFile.writeText(classSrc)
  }
  fun generateServClass(
      servdef: ObjectNode, endpointDirectory: String, fullClassName: String,
      funcImports: String, funcDecls: String, funcSrc: String
  ): String {
    val packageName = fullClassName.substringBeforeLast(".")
    val className   = fullClassName.substringAfterLast(".")
    val requestDir  = endpointDirectory+if (endpointDirectory.endsWith("/")) {""} else {"/"}

    val hasSession  = servdef.get("hasSession")?.asBoolean() == true

    val classDesc   = servdef.get("desc")?.asText() ?:
        "Provides a set of operations on the database server"

    val classSrc  = """package ${packageName};

// IMPORTANT: Do not edit. This file is generated.

${funcImports}

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * ${classDesc}
 */
public interface ${className} {
    /**
     * Creates a ${className} object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static ${className} on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a ${className} object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * The service declaration uses a custom implementation of the same service instead
     * of the default implementation of the service by specifying an endpoint directory
     * in the modules database with the implementation. A service.json file with the
     * declaration can be read with FileHandle or a string serialization of the JSON
     * declaration with StringHandle.
     *
     * @param db	provides a client for communicating with the database server
     * @param serviceDeclaration	substitutes a custom implementation of the service
     * @return	an object for executing database operations
     */
    static ${className} on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class ${className}Impl implements ${className} {
            private BaseProxy baseProxy;

            private ${className}Impl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                baseProxy = new BaseProxy(dbClient, "${requestDir}", servDecl);
            }${
    if (!hasSession) ""
    else """
            @Override
            public SessionState newSessionState() {
              return baseProxy.newSessionState();
            }"""
    }
${funcSrc}
        }

        return new ${className}Impl(db, serviceDeclaration);
    }${
    if (!hasSession) ""
    else """
    /**
     * Creates an object to track a session for a set of operations
     * that require session state on the database server.
     *
     * @return	an object for session state
     */
    SessionState newSessionState();"""
    }
${funcDecls}
}
"""
    return classSrc
  }
  fun generateFuncSrc(
      funcDecl: MutableList<String>, funcDepend: MutableSet<String>, servdef: ObjectNode,
      moduleFilename: String, funcdef: ObjectNode
  ): String {
    val funcName = funcdef.get("functionName")?.asText()
    if (funcName === null || funcName.length == 0) {
      throw IllegalArgumentException("function without name")
    }
    val funcDesc = funcdef.get("desc")?.asText() ?:
      "Invokes the ${funcName} operation on the database server"

    val funcParams = funcdef.withArray("params")
    val funcReturn = funcdef.get("return")

    val atomicTypes   = getAtomicDataTypes()
    val documentTypes = getDocumentDataTypes()

    var atomicCardinality   = ValueCardinality.NONE
    var documentCardinality = ValueCardinality.NONE

    var sessionParam : ObjectNode? = null

    val payloadParams = mutableListOf<ObjectNode>()
    funcParams?.forEach{param ->
      val funcParam = (param as ObjectNode)
      val paramName = funcParam.get("name").asText()
      val paramType = funcParam.get("datatype")?.asText()

      if (paramType === null) throw IllegalArgumentException(
          "$paramName parameter of $funcName function has no datatype"
        )
      else if (atomicTypes.containsKey(paramType)) {
        funcParam.put("dataKind", "atomic")
        atomicCardinality = paramKindCardinality(atomicCardinality, funcParam)
        payloadParams.add(funcParam)
      } else if (documentTypes.containsKey(paramType)) {
        funcParam.put("dataKind", "document")
        payloadParams.add(funcParam)
        documentCardinality = paramKindCardinality(documentCardinality, funcParam)
      } else if (paramType == "session") {
        if (sessionParam !== null) {
          throw IllegalArgumentException("$funcName function has multiple session parameters")
        }
        funcParam.put("dataKind", "system")
        sessionParam = funcParam
        servdef.put("hasSession", true)
      } else throw IllegalArgumentException(
          "$paramName parameter of $funcName function has invalid datatype: $paramType"
        )
    }

    val returnType     = funcReturn?.get("datatype")?.asText()
    val returnMapping  = funcReturn?.get("\$javaClass")?.asText()
    val returnKind     =
        if (returnType === null)
          if (funcReturn === null)                      null
          else throw IllegalArgumentException("$funcName function has return without datatype")
        else if (atomicTypes.containsKey(returnType))   "atomic"
        else if (documentTypes.containsKey(returnType)) "document"
        else throw IllegalArgumentException("$funcName function has invalid return datatype: $returnType")
    if (funcReturn !== null) {
      (funcReturn as ObjectNode).put("dataKind", returnKind)
    }
    val returnNullable = funcReturn?.get("nullable")?.asBoolean() == true
    val returnMultiple = funcReturn?.get("multiple")?.asBoolean() == true
    val returnMapped   =
        if (returnType === null || returnKind === null) null
        else getJavaDataType(returnType, returnMapping, returnKind, returnMultiple)

    val endpointMethod = "POST"

    val paramsKind     =
        if (atomicCardinality !== ValueCardinality.NONE && documentCardinality !== ValueCardinality.NONE)
          "MULTIPLE_MIXED"
        else if (atomicCardinality === ValueCardinality.MULTIPLE)
          "MULTIPLE_ATOMICS"
        else if (atomicCardinality === ValueCardinality.SINGLE)
          "SINGLE_ATOMIC"
        else if (documentCardinality === ValueCardinality.MULTIPLE)
          "MULTIPLE_NODES"
        else if (documentCardinality === ValueCardinality.SINGLE)
          "SINGLE_NODE"
        else if (atomicCardinality === ValueCardinality.NONE && documentCardinality === ValueCardinality.NONE)
          "NONE"
        else throw InternalError("Unknown combination of atomic and document parameter cardinality")

    val paramDescs     = mutableListOf<String>()
    val sigParams      = funcParams?.map{funcParam ->
      val paramName     = funcParam.get("name").asText()
      val paramType     = funcParam.get("datatype").asText()
      val paramMapping  = funcParam.get("\$javaClass")?.asText()
      val paramKind     = funcParam.get("dataKind").asText()
      val isMultiple    = funcParam.get("multiple")?.asBoolean() == true
      val mappedType    = getJavaDataType(paramType, paramMapping, paramKind, isMultiple)
      val sigType       =
          if (!isMultiple) mappedType
          else             "Stream<"+mappedType+">"
      val paramDesc     = "@param ${paramName}\t" + (
          funcParam.get("desc")?.asText() ?: "provides input"
          )
      when (paramKind) {
        "document" -> {
          funcDepend.add("com.marklogic.client.io.Format")
          if (mappedType.contains(".") != true) {
            funcDepend.add("java.io.$mappedType")
          }
        }
        "system" ->
          funcDepend.add("com.marklogic.client.$mappedType")
      }
      if (isMultiple) {
        funcDepend.add("java.util.stream.Stream")
      }
      paramDescs.add(paramDesc)
      sigType+" "+paramName
    }?.joinToString(", ")

    if (returnKind == "document") {
      funcDepend.add("com.marklogic.client.io.Format")
      if (returnMapped?.contains(".") != true) {
        funcDepend.add("java.io.$returnMapped")
      }
    }
    if (returnMultiple) {
      funcDepend.add("java.util.stream.Stream")
    }
    val returnSig      =
        if (returnType === null)  "void"
        else if (!returnMultiple) returnMapped
        else                      "Stream<"+returnMapped+">"
    val returnDesc     =
        if (funcReturn === null) ""
        else "@return\t" + (
            funcReturn.get("desc")?.asText() ?: "as output"
            )

    val sessionName     =
        if (sessionParam === null) null
        else (sessionParam as ObjectNode).get("name").asText()
    val sessionNullable =
        if (sessionParam === null) null
        else (sessionParam as ObjectNode).get("nullable")?.asBoolean() == true
    val sessionChained  =
        if (sessionParam === null) ""
        else  """"${sessionName}", ${sessionName}, ${sessionNullable}"""

    val paramsChained = payloadParams.map{funcParam ->
      val paramName    = funcParam.get("name").asText()
      val paramType    = funcParam.get("datatype").asText()
      val paramKind    = funcParam.get("dataKind").asText()
      val paramMapping = funcParam.get("\$javaClass")?.asText()
      val isMultiple   = funcParam.get("multiple")?.asBoolean() == true
      val isNullable   = funcParam.get("nullable")?.asBoolean() == true
      val mappedType   = getJavaDataType(paramType, paramMapping, paramKind, isMultiple)
      """BaseProxy.${paramKind}Param("${paramName}", ${isNullable}, BaseProxy.${typeConverter(paramType)}.from${
      if (mappedType.contains("."))
        mappedType.substringAfterLast(".").capitalize()
      else
        mappedType.capitalize()
      }(${paramName}))"""
    }.joinToString(""",
                    """)

    val returnConverter =
        if (returnType === null || returnMapped === null)
          ""
        else
          """return BaseProxy.${typeConverter(returnType)}.to${
          if (returnMapped.contains("."))
            returnMapped.substringAfterLast(".").capitalize()
          else
            returnMapped.capitalize()
          }(
                """
    val returnFormat  =
        if (returnType === null || returnKind != "document") "null"
        else typeFormat(returnType)
    val returnChained =
        if (returnKind === null)         """.responseNone()"""
        else if (returnMultiple == true) """.responseMultiple(${returnNullable}, ${returnFormat})
                )"""
        else                             """.responseSingle(${returnNullable}, ${returnFormat})
                )"""

    val sigSource      = """${returnSig} ${funcName}(${sigParams ?: ""})"""

    val declSource     = """
  /**
   * ${funcDesc}
   *
   * ${ if (paramDescs.size == 0) "" else paramDescs.joinToString("""
   * """)}
   * ${returnDesc}
   */
    ${sigSource};
"""
    funcDecl.add(declSource)

    val defSource      = """
            @Override
            public ${sigSource} {
              ${returnConverter
              }baseProxy
                .request("${moduleFilename}", BaseProxy.ParameterValuesKind.${paramsKind})
                .withSession(${sessionChained})
                .withParams(
                    ${paramsChained})
                .withMethod("${endpointMethod}")
                ${returnChained};
            }
"""
    return defSource
  }
  fun typeConverter(datatype: String) : String {
    val converter =
        if (datatype == "int")                "Integer"
        else if (datatype == "unsignedInt")   "UnsignedInteger"
        else                                   datatype.capitalize()
    return converter+"Type"
  }
  fun typeFormat(documentType: String) : String {
    val format =
        if (documentType == "array" || documentType == "object") "Format.JSON"
        else "Format."+documentType.substringBefore("Document").toUpperCase()
    return format;
  }
  fun paramKindCardinality(currCardinality: ValueCardinality, param: ObjectNode): ValueCardinality {
    val nextCardinality =
        if (currCardinality !== ValueCardinality.NONE)       ValueCardinality.MULTIPLE
        else if (param.get("multiple")?.asBoolean() == true) ValueCardinality.MULTIPLE
        else                                                 ValueCardinality.SINGLE
    return nextCardinality
  }

  // entry point for ModuleInitTask
  fun endpointDeclToModStubImpl(endpointDeclFilename: String, moduleExtension: String) {
    if (endpointDeclFilename.length == 0) {
      throw IllegalArgumentException("null declaration file")
    }

    val endpointDeclFile = File(endpointDeclFilename)
    if (!endpointDeclFile.exists()) {
      throw IllegalArgumentException("declaration file doesn't exist: "+endpointDeclFilename)
    }

    if(moduleExtension != "mjs" && moduleExtension != "sjs" && moduleExtension != "xqy") {
      throw IllegalArgumentException("invalid module extension: "+moduleExtension)
    }

    val moduleFile = endpointDeclFile.parentFile.resolve(
        endpointDeclFile.nameWithoutExtension+"."+moduleExtension
    )
    if (moduleFile.exists()) {
      throw IllegalArgumentException("module file already exists: "+moduleFile.absolutePath)
    }

    val mapper         = jacksonObjectMapper()

    val atomicTypes    = getAtomicDataTypes()
    val documentTypes  = getDocumentDataTypes()

    val funcdef        = mapper.readValue<ObjectNode>(endpointDeclFile)
    val funcParams     = funcdef.withArray("params")

    val funcReturn     = funcdef.get("return")
    val returnType     = funcReturn?.get("datatype")?.asText()
    val returnNullable = funcReturn?.get("nullable")?.asBoolean() == true
    val returnMultiple = funcReturn?.get("multiple")?.asBoolean() == true
    val returnCardinal =
        if (returnType === null) ""
        else getServerCardinality(returnMultiple, returnNullable)
    val returnTypeName =
        if (returnType === null) ""
        else getServerType(returnType, atomicTypes, documentTypes, moduleExtension)

    val prologSource   = getEndpointProlog(moduleExtension)
    val paramsSource   = getEndpointParamSource(atomicTypes, documentTypes, moduleExtension, funcParams)
    val returnSource   = getEndpointReturnSource(moduleExtension, returnTypeName, returnCardinal)
    val moduleSource   = """${prologSource}
${paramsSource}
${returnSource}
"""
    moduleFile.writeText(moduleSource)
  }
  fun getEndpointProlog(moduleExtension: String): String {
    val prolog =
      if (moduleExtension == "mjs" || moduleExtension == "sjs")
        """'use strict';
// declareUpdate(); // Note: uncomment if changing the database state
"""
      else
        """xquery version "1.0-ml";

declare option xdmp:mapping "false";
"""
    return prolog
  }
  fun getEndpointParamSource(atomicTypes: Map<String,String>, documentTypes: Map<String,String>,
                    moduleExtension: String, funcParams: ArrayNode?
  ): String {
    val paramsSource =
            if (funcParams === null) ""
            else funcParams.map{funcParam ->
              val paramName   = funcParam.get("name").asText()
              val paramType   = funcParam.get("datatype").asText()
              val isMultiple  = funcParam.get("multiple")?.asBoolean() == true
              val isNullable  = funcParam.get("nullable")?.asBoolean() == true
              val cardinality = getServerCardinality(isMultiple, isNullable)
              val typeName    = getServerType(paramType, atomicTypes, documentTypes, moduleExtension)
              val paramdef    =
                      if (moduleExtension == "mjs")
                        if (isNullable)
                          "const ${paramName} = (external.${paramName} === void 0) ? null : external.${paramName}; // instanceof ${typeName}${cardinality}"
                        else
                          "const ${paramName} = external.${paramName}; // instanceof ${typeName}${cardinality}"
                      else if (moduleExtension == "sjs")
                        "var ${paramName}; // instanceof ${typeName}${cardinality}"
                      else
                        if (isNullable)
                          "declare variable $${paramName} as ${typeName}${cardinality} external := ();"
                        else
                          "declare variable $${paramName} as ${typeName}${cardinality} external;"
              paramdef
            }.filterNotNull().joinToString("""
""")
    return paramsSource
  }
  fun getEndpointReturnSource(moduleExtension: String, returnTypeName: String?, returnCardinal: String): String {
    val returnSource   =
            if (moduleExtension == "mjs" || moduleExtension == "sjs") """
// TODO:  produce the ${returnTypeName}${returnCardinal} output from the input variables
"""
            else """
(: TODO:  produce the ${returnTypeName}${returnCardinal} output from the input variables :)
"""
    return returnSource
  }
  fun getServerType(paramType: String, atomicTypes: Map<String,String>,
                    documentTypes: Map<String,String>, moduleExtension: String
  ): String? {
    val typeName   =
        if (paramType == "session")
          null
        else if (atomicTypes.containsKey(paramType))
          if (moduleExtension == "mjs" || moduleExtension == "sjs") "xs."+paramType
          else                                                      "xs:"+paramType
        else if (documentTypes.containsKey(paramType))
          when (paramType) {
            "array","object" ->
              if (moduleExtension == "mjs" || moduleExtension == "sjs")
                paramType.capitalize()+"Node"
              else
                paramType+"-node()"
            else ->
              if (moduleExtension == "mjs" || moduleExtension == "sjs")
                "DocumentNode"
              else
                "document-node()"
          }
        else throw IllegalArgumentException("invalid datatype: $paramType")
    return typeName
  }
  fun getServerCardinality(isMultiple: Boolean, isNullable: Boolean): String {
    val cardinality =
        if      (isMultiple  && isNullable)  "*"
        else if (isMultiple  && !isNullable) "+"
        else if (!isMultiple && isNullable)  "?"
        else                                 ""
    return cardinality
  }

  // entry point for ServiceCompareTask
  fun compareServices(customServDeclFilename: String, baseServDeclFilename: String? = null) {
    val mapper = jacksonObjectMapper()

    val customServDeclFile = File(customServDeclFilename)
    val customServdef = mapper.readValue<ObjectNode>(customServDeclFile)

    val customEndpointDirectory = getEndpointDirectory(customServDeclFilename, customServdef)

    var baseServDeclFilename =
        if (baseServDeclFilename != null)
            baseServDeclFilename
        else resolveBaseEndpointDirectory(
            customServDeclFilename, customServDeclFile, customServdef, customEndpointDirectory
            )

    val baseServDeclFile = File(baseServDeclFilename)
    val baseServdef = mapper.readValue<ObjectNode>(baseServDeclFile)

    val customModuleFiles = mutableMapOf<String, File>()
    val customFuncdefs    = getFuncdefs(
        mapper, customEndpointDirectory, customServDeclFile, customServdef, customModuleFiles
    )

    val baseEndpointDirectory = getEndpointDirectory(baseServDeclFilename, baseServdef)

    val baseModuleFiles = mutableMapOf<String, File>()
    val baseFuncdefs    = getFuncdefs(
        mapper, baseEndpointDirectory, baseServDeclFile, baseServdef, baseModuleFiles
    )

    val errors = mutableListOf<String>()

    val keepBaseExtsn = !customServdef.has("endpointExtension")
    customFuncdefs.forEach{(root, customFuncdef) ->
      if (!baseFuncdefs.containsKey(root)) {
        errors.add("function ${root} exists in custom service but not base service")
      } else {
        val baseFuncdef = baseFuncdefs[root]
        if (baseFuncdef == null) {
          errors.add("function ${root} is null in base service but not custom service")
        } else {
          if (keepBaseExtsn) {
            val baseExtsn = baseModuleFiles[root]?.extension
            val customExtsn = customModuleFiles[root]?.extension
            if (baseExtsn != customExtsn) {
              errors.add(
                  "function ${root} has $baseExtsn extension in base service but $customExtsn in custom service"
              )
            }
          }
          checkObjectsEqual(errors, root, customFuncdef, baseFuncdef)
        }
      }
    }
    baseFuncdefs.keys.forEach{root ->
      if (!customFuncdefs.containsKey(root)) {
        errors.add("function ${root} exists in base service but not custom service")
      }
    }

    if (errors.size > 0) {
      val errorReport = errors.joinToString("""
""")
      throw IllegalArgumentException(
          """custom declaration inconsistent with base declaration:
${errorReport}"""
      )
    }
  }
  fun resolveBaseEndpointDirectory(
      customServDeclFilename: String, customServDeclFile: File, customServdef: ObjectNode, customEndpointDirectory: String
  ): String {
    var baseEndpointDirectory = customServdef.get("baseEndpointDirectory")?.asText()
    if (baseEndpointDirectory === null) {
      throw IllegalArgumentException(
          "baseEndpointDirectory argument empty and no baseEndpointDirectory property in $customServDeclFilename"
      )
    } else if (baseEndpointDirectory.length == 0) {
      throw IllegalArgumentException(
          "baseEndpointDirectory argument empty and empty baseEndpointDirectory property in $customServDeclFilename"
      )
    } else if (!baseEndpointDirectory.endsWith("/")) {
      baseEndpointDirectory += "/"
    }
    if (customEndpointDirectory == baseEndpointDirectory) {
      throw IllegalArgumentException(
          "custom directory $customEndpointDirectory specifies itself as the base directory"
      )
    }

    /* Example
      file:             /ml-modules/root/dbfunctiondef/positive/decoratorCustom/service.json
      custom directory:                               /dbf/test/decoratorCustom/
      base directory:                                 /dbf/test/decoratorBase/
     */
    // the leading database directories (if any) that the custom and base declaration files have in common
    val commonPrefix = customEndpointDirectory
                .commonPrefixWith(baseEndpointDirectory)
                .replaceFirst(Regex("""([/\\])[^\/\\]+$"""), "$1")

    // the trailing database path that's unique to the custom declaration file
    var trimSuffix   =
      if (commonPrefix.length == 0)
        customEndpointDirectory
      else
        customEndpointDirectory.substring(commonPrefix.length)
    trimSuffix = trimSuffix.replace(Regex("""[/\\]+"""), File.separator) + "service.json"

    // the filesystem path of the custom declaration file
    val customServDeclPath = customServDeclFile.absolutePath
    if (!customServDeclPath.endsWith(trimSuffix)) {
      throw IllegalArgumentException(
          "cannot determine relative path for $customEndpointDirectory within $customServDeclPath"
      )
    }

    // the leading directories from the filesystem path of the custom declaration file
    val rootPath = customServDeclPath.substring(0, customServDeclPath.length - trimSuffix.length)

    // the trailing database path that's unique to the base declaration file
    var appendSuffix =
        if (commonPrefix.length == 0)
          baseEndpointDirectory
        else
          baseEndpointDirectory.substring(commonPrefix.length)
    appendSuffix = appendSuffix.replace(Regex("""[/\\]+"""), File.separator)+"service.json"

    // concatenate the leading filesystem directories and trailing database path for the base declaration file
    return rootPath+appendSuffix
  }
  fun checkObjectsEqual(errors: MutableList<String>, parentKey: String, customObj: ObjectNode, baseObj: ObjectNode) {
    customObj.fields().forEach{(key, customVal) ->
      if (key.startsWith("$")) {
      } else if (!baseObj.has(key)) {
        errors.add("${key} property of ${parentKey} exists in custom service but not base service")
      } else {
        checkValuesEqual(errors, key, customVal, baseObj.get(key))
      }
    }
    baseObj.fields().forEach{(key, value) ->
      if (!customObj.has(key)) {
        errors.add("${key} property of ${parentKey} exists in base service but not custom service")
      }
    }
  }
  fun checkArraysEqual(errors: MutableList<String>, key: String, customArr: ArrayNode, baseArr: ArrayNode) {
    if (customArr.size() != baseArr.size()) {
      errors.add(
          "${key} property has ${baseArr.size()} items in base service but ${customArr.size()} in custom service"
      )
      return
    }
    for (i in 0..customArr.size()) {
      checkValuesEqual(errors, key, customArr[i], baseArr[i])
    }
  }
  fun checkValuesEqual(errors: MutableList<String>, key: String, customVal: JsonNode?, baseVal: JsonNode?) {
    if (customVal == null) {
      if (baseVal != null) {
        errors.add("${key} property is null in custom service but not base service")
      }
      return
    }
    if (baseVal == null) {
      errors.add("${key} property is null in base service but not custom service")
      return
    }
    if (customVal.isObject) {
      if (baseVal.isObject) {
        checkObjectsEqual(errors, key, customVal as ObjectNode, baseVal as ObjectNode)
      } else {
        errors.add("${key} property is object in custom service but not base service")
      }
      return
    }
    if (baseVal.isObject) {
      errors.add("${key} property is object in base service but not custom service")
      return
    }
    if (customVal.isArray) {
      val customArr = customVal as ArrayNode
      if (baseVal.isArray) {
        checkArraysEqual(errors, key, customArr, baseVal as ArrayNode)
      } else if (customArr.size() == 1) {
        checkValuesEqual(errors, key, customArr[0], baseVal)
      } else {
        errors.add("${key} property is array in custom service but not base service")
      }
      return
    }
    if (baseVal.isArray) {
      errors.add("${key} property is array in base service but not custom service")
      return
    }
    if (customVal != baseVal) {
      errors.add(
          "${key} property of ${baseVal} in base service is not equal to ${customVal} in custom service"
      )
      return
    }
  }
}