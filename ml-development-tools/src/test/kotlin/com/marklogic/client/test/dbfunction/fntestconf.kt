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
package com.marklogic.client.test.dbfunction

import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.io.DocumentMetadataHandle
import com.marklogic.client.io.InputStreamHandle
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

val contentDbName = "DBFUnitTest"
val modulesDbName = "DBFUnitTestModules"
val serverName    = "DBFUnitTest"
val host          = System.getenv("TEST_HOST") ?: "localhost"
val serverPort    = System.getenv("TEST_PORT")?.toInt() ?: 8016

fun main(args: Array<String>) {
  val mapper     = jacksonObjectMapper()
  val serializer = mapper.writerWithDefaultPrettyPrinter()

/* TODO:
skip
    setup operations where result is present
    teardown where result is absent
amp the test inspector to add header
create roles and users
parameterize the port, user name, etc
   */
  val operation = if (args.size > 0) args[0] else "setup"
  when(operation) {
    "setup"    -> dbfTestSetup(serializer)
    "teardown" -> dbfTestTeardown(serializer)
    else       -> throw java.lang.IllegalArgumentException("unknown operation: "+operation)
  }
}
fun dbfTestSetup(serializer: ObjectWriter) {
  setupServer(serializer)
  setupModules()
}
fun dbfTestTeardown(serializer: ObjectWriter) {
  teardownServer(serializer)
}
fun setupServer(serializer: ObjectWriter) {
  val dbClient = DatabaseClientFactory.newClient(
      host,
      8002,
      DatabaseClientFactory.DigestAuthContext("admin", "admin")
  )

  val client = dbClient.getClientImplementation() as OkHttpClient

  for (dbName in listOf(contentDbName, modulesDbName)) {
    createEntity(
      client, serializer, "databases", "database", dbName,
      mapOf<String,String>("database-name" to dbName)
      )

    val forestName = dbName+"-1"
    createEntity(
      client, serializer, "forests", "forest", forestName,
      mapOf<String,String>("forest-name" to forestName, "database" to dbName)
      )
  }

  val userName = "rest-reader"
  createEntity(
      client, serializer, "users", "user", userName,
      mapOf<String,Any>(
        "user-name"        to userName,
        "password"         to "x",
        "role"             to listOf<String>(userName)
        )
  )

  createEntity(
      client, serializer, "servers?group-id=Default&server-type=http", "appserver", serverName,
      mapOf<String,Any>(
        "server-name"      to serverName,
        "root"             to "/",
        "port"             to serverPort,
        "content-database" to contentDbName,
        "modules-database" to modulesDbName
        )
  )

  dbClient.release()
}
fun createEntity(client: OkHttpClient, serializer: ObjectWriter, address: String, name: String,
    instanceName: String, instancedef: Map<String,Any>) {
  val response = client.newCall(
    Request.Builder()
      .url("""http://${host}:8002/manage/v2/${address}""")
      .post(
        RequestBody.create(MediaType.parse("application/json"), serializer.writeValueAsString(instancedef))
        )
      .build()
    ).execute()
  if (response.code() >= 400) {
    throw RuntimeException("""Could not create ${instanceName} ${name}: ${response.code()}""")
  }
}
fun setupModules() {
  val dbClient = DatabaseClientFactory.newClient(
      host,
      8000,
      "DBFUnitTestModules",
      DatabaseClientFactory.DigestAuthContext("admin", "admin")
      )

  val docMgr = dbClient.newJSONDocumentManager()

  val docMeta = DocumentMetadataHandle()

  val docPerm = docMeta.permissions
  docPerm.add("rest-reader", DocumentMetadataHandle.Capability.EXECUTE)

  for (scriptName in listOf("testInspector")) {
    val scriptPath = """./${scriptName}.sjs"""
    val scriptUri  = """/dbf/test/${scriptName}.sjs"""

    val inspectorStream = DBFunctionTestUtil.getResourceAsStream(scriptPath)

    docMgr.write(scriptUri, docMeta, InputStreamHandle(inspectorStream))
  }

  dbClient.release()
}
fun teardownServer(serializer: ObjectWriter) {
  val dbClient = DatabaseClientFactory.newClient(
      host,
      8002,
      DatabaseClientFactory.DigestAuthContext("admin", "admin")
  )

  val client = dbClient.getClientImplementation() as OkHttpClient

  val response = client.newCall(
    Request.Builder()
      .url("""http://${host}:8002/manage/v2/servers/${serverName}/properties?group-id=Default""")
      .put(
        RequestBody.create(MediaType.parse("application/json"), serializer.writeValueAsString(
          mapOf<String, Int>("content-database" to 0, "modules-database" to 0)
          ))
        )
      .build()
    ).execute()
  if (response.code() >= 400) {
    throw RuntimeException("""Could not detach ${serverName} appserver: ${response.code()}""")
  }

  for (dbName in listOf(contentDbName, modulesDbName)) {
    deleteEntity(client, """databases/${dbName}?forest-delete=data""", "database", dbName)
  }

  deleteEntity(client, """servers/${serverName}?group-id=Default""", "appserver", serverName)

  dbClient.release()
}
fun deleteEntity(client: OkHttpClient, address: String, name: String, instanceName: String) {
  val response = client.newCall(
    Request.Builder()
      .url("""http://${host}:8002/manage/v2/${address}""")
      .delete()
      .build()
  ).execute()
  if (response.code() >= 400) {
    throw RuntimeException("""Could not delete ${instanceName} ${name}: ${response.code()}""")
  }
}
