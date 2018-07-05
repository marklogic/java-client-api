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
package com.marklogic.client.tools.proxy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class Generator {
  enum class ValueCardinality { NONE, SINGLE, MULTIPLE }

  fun getJavaConverterName(): Map<String,String> {
    return mapOf(
        "com.marklogic.client.io.marker.BinaryReadHandle"  to "BinaryHandle",
        "com.marklogic.client.io.marker.BinaryWriteHandle" to "BinaryHandle",
        "java.io.InputStream"                              to "InputStream",
        "java.io.Reader"                                   to "Reader",
        "com.fasterxml.jackson.databind.JsonNode"          to "JacksonJsonNode",
        "com.fasterxml.jackson.core.JsonParser"            to "JacksonJsonParser",
        "com.fasterxml.jackson.databind.node.ArrayNode"    to "JacksonArrayNode",
        "com.fasterxml.jackson.databind.node.ObjectNode"   to "JacksonObjectNode",
        "com.marklogic.client.io.marker.JSONReadHandle"    to "JSONHandle",
        "com.marklogic.client.io.marker.JSONWriteHandle"   to "JSONHandle",
        "com.marklogic.client.io.marker.TextReadHandle"    to "TextHandle",
        "com.marklogic.client.io.marker.TextWriteHandle"   to "TextHandle",
        "org.w3c.dom.Document"                             to "XMLDOMDocument",
        "org.xml.sax.InputSource"                          to "XMLSaxInputSource",
        "javax.xml.transform.Source"                       to "XMLTransformSource",
        "javax.xml.stream.XMLEventReader"                  to "XMLEventReader",
        "javax.xml.stream.XMLStreamReader"                 to "XMLStreamReader",
        "com.marklogic.client.io.marker.XMLReadHandle"     to "XMLHandle",
        "com.marklogic.client.io.marker.XMLWriteHandle"    to "XMLHandle",

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
  // TODO: distinguish write handles from read handles
  fun getDocumentMappings(): Map<String,Set<String>> {
    return mapOf(
        "array"             to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.node.ArrayNode",
            "com.fasterxml.jackson.core.JsonParser",
            "com.marklogic.client.io.marker.JSONReadHandle",
            "com.marklogic.client.io.marker.JSONWriteHandle"),
        "binaryDocument"    to setOf(
            "java.io.InputStream",
            "com.marklogic.client.io.marker.BinaryReadHandle",
            "com.marklogic.client.io.marker.BinaryWriteHandle"),
        "jsonDocument"      to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.JsonNode",
            "com.fasterxml.jackson.core.JsonParser",
            "com.marklogic.client.io.marker.JSONReadHandle",
            "com.marklogic.client.io.marker.JSONWriteHandle"),
        "object"            to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.fasterxml.jackson.databind.node.ObjectNode",
            "com.fasterxml.jackson.core.JsonParser",
            "com.marklogic.client.io.marker.JSONReadHandle",
            "com.marklogic.client.io.marker.JSONWriteHandle"),
        "textDocument"      to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "com.marklogic.client.io.marker.TextReadHandle",
            "com.marklogic.client.io.marker.TextWriteHandle"),
        "xmlDocument"       to setOf(
            "java.io.InputStream",
            "java.io.Reader",
            "java.lang.String",
            "org.w3c.dom.Document",
            "org.xml.sax.InputSource",
            "javax.xml.transform.Source",
            "javax.xml.stream.XMLEventReader",
            "javax.xml.stream.XMLStreamReader",
            "com.marklogic.client.io.marker.XMLReadHandle",
            "com.marklogic.client.io.marker.XMLWriteHandle")
    )
  }
  fun getAllDataTypes(): Map<String,String> {
    return getAtomicDataTypes() + getDocumentDataTypes()
  }
  fun getAllMappings(): Map<String,Set<String>> {
    return getAtomicMappings() + getDocumentMappings()
  }
  fun getJavaDataType(
      dataType: String, mapping: String?, dataKind: String, isNullable: Boolean, isMultiple: Boolean
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
  fun serviceBundleToJava(servFilename: String, javaBaseDir: String) {
    val warnings = mutableListOf<String>()
    val mapper   = jacksonObjectMapper()

    val servFile = File(servFilename)
    val servdef  = mapper.readValue<ObjectNode>(servFile)

    var endpointDirectory = servdef.get("endpointDirectory")?.asText()
    if (endpointDirectory === null) {
      throw IllegalArgumentException("no endpointDirectory property in $servFilename")
    } else if (endpointDirectory.length === 0) {
      throw IllegalArgumentException("empty endpointDirectory property in $servFilename")
    } else if (!endpointDirectory.endsWith("/")) {
      endpointDirectory = endpointDirectory+"/"
    }

    val fullClassName = servdef.get("\$javaClass")?.asText()
    if (fullClassName === null) {
      throw IllegalArgumentException("no \$javaClass property in $servFilename")
    }

    val moduleFiles   = mutableMapOf<String, File>()
    val functionFiles = mutableMapOf<String, File>()
    servFile.parentFile.listFiles().forEach{file ->
      // TODO: overriding default extensions
      when(file.extension) {
        "api"        -> functionFiles[ file.nameWithoutExtension ] = file
// TODO: error if already exists
        "sjs", "xqy" -> moduleFiles[   file.nameWithoutExtension ] = file
      }
    }
    val moduleRoots   = moduleFiles.keys
    val functionRoots = functionFiles.keys
    unpairedWarnings(warnings, moduleFiles, functionRoots,
        "endpoint main module without function declaration")

    val funcdefs    = functionFiles.filter{entry ->
      if (moduleRoots.contains(entry.key)) {
        true
      } else {
        warnings.add("function declaration without endpoint main module: "+entry.value.absolutePath)
        false
      }
    }.mapValues{(root, file) ->
      mapper.readValue<ObjectNode>(file)
    }

// TODO: emit warnings

    val funcSrc     = funcdefs.map{(root, funcdef) ->
      generateFuncSrc(servdef, moduleFiles[root]!!.name, funcdef)
    }.joinToString("\n")
    val funcImports = funcdefs.map{(root, funcdef) ->
      generateFuncImports(funcdef)
    }.distinct().joinToString("\n")

    val classSrc = generateServClass(
        servdef, endpointDirectory, servFilename, fullClassName, funcImports, funcSrc
    )

    writeClass(servdef, fullClassName, classSrc, javaBaseDir)
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
  fun writeClass(servdef: ObjectNode, fullClassName: String, classSrc: String, javaBaseDir: String) {
    val classFilename = javaBaseDir+if (javaBaseDir.endsWith("/")) {""} else {"/"}+
        fullClassName.replace(".", "/")+".java"
    val classFile     = File(classFilename)
//    println(javaFilename)
//    println(javaSource)
//    println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(functiondef))
    classFile.parentFile.mkdirs()
    classFile.writeText(classSrc)
  }
// TODO: generate interface IClassName for mocking
  fun generateServClass(
      servdef: ObjectNode, endpointDirectory: String, servFilename: String, fullClassName: String, funcImports: String, funcSrc: String
  ): String {
    val packageName = fullClassName.substringBeforeLast(".")
    val className   = fullClassName.substringAfterLast(".")
    val requestDir  = endpointDirectory+if (endpointDirectory.endsWith("/")) {""} else {"/"}

    val hasSession  = servdef.get("hasSession")?.asBoolean() === true

    val classDesc   = servdef.get("desc")?.asText() ?:
        "Provides a set of operations on the database server"

    val classSrc  = """package ${packageName};

// IMPORTANT: Do not edit. This file is generated.

${funcImports}

import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.BaseProxy;

/**
 * ${classDesc}
 */
public class ${className} {
    private BaseProxy baseProxy;

    public static ${className} on(DatabaseClient db) {
        return new ${className}(db);
    }

    public ${className}(DatabaseClient db) {
        baseProxy = new BaseProxy(db, "${requestDir}");
    }${
// TODO: JavaDoc for the session state factory
    if (!hasSession) ""
    else """
    public SessionState newSessionState() {
      return baseProxy.newSessionStateImpl();
    }"""
    }
${funcSrc}
}
"""
    return classSrc
  }
  fun generateFuncImports(funcdef: ObjectNode): String {
    val funcParams     = funcdef.get("params")
    val funcReturn     = funcdef.get("return")

    val documentTypes  = getDocumentDataTypes()
    val atomicTypes    = getAtomicDataTypes()

    val returnType     = funcReturn?.get("datatype")?.asText()
    val returnMapping  = funcReturn?.get("\$javaClass")?.asText()
    val returnKind     =
        if (returnType === null)                        null
        else if (atomicTypes.containsKey(returnType))   "atomic"
        else if (documentTypes.containsKey(returnType)) "document"
        else throw IllegalArgumentException("invalid return datatype: $returnType")
    val returnNullable = funcReturn?.get("nullable")?.asBoolean() === true
    val returnMultiple = funcReturn?.get("multiple")?.asBoolean() === true
    val returnMapped   =
        if (returnType === null || returnKind === null) null
        else getJavaDataType(returnType, returnMapping, returnKind, returnNullable, returnMultiple)

    val paramImports   =
        funcParams?.map{funcParam ->
          val paramType    = funcParam.get("datatype").asText()
          val paramMapping = funcParam.get("\$javaClass")?.asText()
          val paramKind    = funcParam.get("dataKind").asText()
          val isMultiple   = funcParam.get("multiple")?.asBoolean() === true
          val isNullable   = funcParam.get("nullable")?.asBoolean() === true
          val mappedType   = getJavaDataType(paramType, paramMapping, paramKind, isNullable, isMultiple)
          val paramImport  =
              if (!documentTypes.containsKey(paramType)) {
                if (paramKind == "system") "import com.marklogic.client.$mappedType;"
                else                       null
              } else if (!mappedType.contains(".")) {
                """import java.io.${mappedType};
"""
              } else                       null
          paramImport
        }?.filterNotNull()?.sorted()?.distinct()?.joinToString("")

// TODO: imports based on return datatype only if not imported for parameter
    val returnImports  =
        if (returnMapped === null || returnKind != "document")
          null
        else if (!returnMapped.contains("."))
          """import java.io.${returnMapped};
"""
        else null
    return """
import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;

${paramImports ?: ""}
${returnImports ?: ""}
"""
  }
  fun generateFuncSrc(servdef: ObjectNode, moduleFilename: String, funcdef: ObjectNode): String {
    val funcName = funcdef.get("functionName")?.asText()
    if (funcName === null || funcName.length === 0) {
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
      }
      else if (documentTypes.containsKey(paramType)) {
        funcParam.put("dataKind", "document")
        payloadParams.add(funcParam)
        documentCardinality = paramKindCardinality(documentCardinality, funcParam)
      }
      else if (paramType == "session") {
        if (sessionParam !== null) {
          throw IllegalArgumentException("$funcName function has multiple session parameters")
        }
        funcParam.put("dataKind", "system")
        sessionParam = funcParam
        servdef.put("hasSession", true)
      }
      else throw IllegalArgumentException(
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
    val returnNullable = funcReturn?.get("nullable")?.asBoolean() === true
    val returnMultiple = funcReturn?.get("multiple")?.asBoolean() === true
    val returnMapped   =
        if (returnType === null || returnKind === null) null
        else getJavaDataType(returnType, returnMapping, returnKind, returnNullable, returnMultiple)

    val endpointMethod = "post"

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
      val isMultiple    = funcParam.get("multiple")?.asBoolean() === true
      val isNullable    = funcParam.get("nullable")?.asBoolean() === true
      val mappedType    = getJavaDataType(paramType, paramMapping, paramKind, isNullable, isMultiple)
      val sigType       =
          if (!isMultiple) mappedType
          else             "Stream<"+mappedType+">"
      val paramDesc     = "@param ${paramName}\t" + (
          funcParam.get("desc")?.asText() ?: "provides input"
          )
      paramDescs.add(paramDesc)
      sigType+" "+paramName
    }?.joinToString(", ")

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
        else (sessionParam as ObjectNode).get("nullable")?.asBoolean() === true
    val sessionChained  =
        if (sessionParam === null) ""
        else  """"${sessionName}", ${sessionName}, ${sessionNullable}"""

    val paramsChained = payloadParams?.map{funcParam ->
      val paramName    = funcParam.get("name").asText()
      val paramType    = funcParam.get("datatype").asText()
      val paramKind    = funcParam.get("dataKind").asText()
      val paramMapping = funcParam.get("\$javaClass")?.asText()
      val isMultiple   = funcParam.get("multiple")?.asBoolean() === true
      val isNullable   = funcParam.get("nullable")?.asBoolean() === true
      val mappedType   = getJavaDataType(paramType, paramMapping, paramKind, isNullable, isMultiple)
      """BaseProxy.${paramKind}Param("${paramName}", ${isNullable}, BaseProxy.${typeConverter(paramType)}.from${
      if (mappedType.contains("."))
        mappedType.substringAfterLast(".").capitalize()
      else
        mappedType.capitalize()
      }(${paramName}))"""
    }?.joinToString(""",
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
        if (returnType == null || returnKind != "document") "null"
        else typeFormat(returnType)
    val returnChained =
        if (returnKind === null)          """.responseNone()"""
        else if (returnMultiple === true) """.responseMultiple(${returnNullable}, ${returnFormat})
        )"""
        else                              """.responseSingle(${returnNullable}, ${returnFormat})
        )"""

    val javaSource     = """
  /**
   * ${funcDesc}
   *
   * ${ if (paramDescs.size == 0) "" else paramDescs.joinToString("""
   * """)}
   * ${returnDesc}
   */
    public ${returnSig} ${funcName}(${sigParams ?: ""}) {
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
    return javaSource
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
        if (currCardinality !== ValueCardinality.NONE)        ValueCardinality.MULTIPLE
        else if (param.get("multiple")?.asBoolean() === true) ValueCardinality.MULTIPLE
        else                                                  ValueCardinality.SINGLE
    return nextCardinality
  }

  fun declarationToModuleStubImpl(functionFilename: String, moduleExtension: String) {
    if (functionFilename === null || functionFilename.length == 0) {
      throw IllegalArgumentException("null declaration file")
    }

    val functionFile = File(functionFilename)
    if (!functionFile.exists()) {
      throw IllegalArgumentException("declaration file doesn't exist: "+functionFilename)
    }

    if(moduleExtension != "sjs" && moduleExtension != "xqy") {
      throw IllegalArgumentException("invalid module extension: "+moduleExtension)
    }

    val moduleFile = functionFile.parentFile.resolve(
        functionFile.nameWithoutExtension+"."+moduleExtension
    )
    if (moduleFile.exists()) {
      throw IllegalArgumentException("module file already exists: "+moduleFile.absolutePath)
    }

    val mapper         = jacksonObjectMapper()

    val atomicTypes    = getAtomicDataTypes()
    val documentTypes  = getDocumentDataTypes()

    val funcdef        = mapper.readValue<ObjectNode>(functionFile)
    val funcParams     = funcdef.withArray("params")

    val funcReturn     = funcdef.get("return")
    val returnType     = funcReturn?.get("datatype")?.asText()
    val returnNullable = funcReturn?.get("nullable")?.asBoolean() === true
    val returnMultiple = funcReturn?.get("multiple")?.asBoolean() === true
    val returnCardinal =
        if (returnType === null) ""
        else getServerCardinality(returnMultiple, returnNullable)
    val returnTypeName =
        if (returnType === null) ""
        else getServerType(returnType, atomicTypes, documentTypes, moduleExtension)

    val prologSource   =
        if (moduleExtension == "sjs")
          """'use strict';
// declareUpdate(); // Note: uncomment if changing the database state
"""
        else """xquery version "1.0-ml";

declare option xdmp:mapping "false";
"""
    val paramsSource   =
        if (funcParams === null) ""
        else funcParams.map{funcParam ->
          val paramName   = funcParam.get("name").asText()
          val paramType   = funcParam.get("datatype").asText()
          val isMultiple  = funcParam.get("multiple")?.asBoolean() === true
          val isNullable  = funcParam.get("nullable")?.asBoolean() === true
          val cardinality = getServerCardinality(isMultiple, isNullable)
          val typeName    = getServerType(paramType, atomicTypes, documentTypes, moduleExtension)
          val paramdef    =
              if (moduleExtension == "sjs")
                "var ${paramName}; // instance of ${typeName}${cardinality}"
              else "declare variable $${paramName} as ${typeName}${cardinality} external;"
          paramdef
        }.filterNotNull().joinToString("""
""")
    val returnSource   =
        if (moduleExtension == "sjs") """
// TODO:  produce the ${returnTypeName}${returnCardinal} output from the input variables
"""
        else """
(: TODO:  produce the ${returnTypeName}${returnCardinal} output from the input variables :)
"""
    val moduleSource   = """${prologSource}
${paramsSource}
${returnSource}
"""
    moduleFile.writeText(moduleSource)
  }
  fun getServerType(paramType: String, atomicTypes: Map<String,String>,
                    documentTypes: Map<String,String>, moduleExtension: String
  ): String? {
    val typeName   =
        if (paramType == "session")
          null
        else if (atomicTypes.containsKey(paramType))
          if (moduleExtension == "sjs") "xs."+paramType
          else                          "xs:"+paramType
        else if (documentTypes.containsKey(paramType))
          when (paramType) {
            "array","object" ->
              if (moduleExtension == "sjs") paramType.capitalize()+"Node"
              else                          paramType+"-node()"
            else ->
              if (moduleExtension == "sjs") "DocumentNode"
              else                          "document-node()"
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
}