/*
 * Copyright (c) 2020 MarkLogic Corporation
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
package com.marklogic.client.test.proxy

import com.marklogic.client.tools.proxy.Generator
import com.networknt.schema.JsonSchemaException
import org.junit.Assert.fail
import org.junit.Test
import java.lang.Exception

class GeneratorTest {
    private val generator = Generator()

    @Test
    fun negInvalidAPI() {
        val badApis = listOf(
                """{"functionName":1}""",
                """{"functionName":"numericParams", "params":1}""",
                """{"functionName":"numericParamsItem", "params":[1]}""",
                """{"functionName":"missingParamsName", "params":[{"datatype":"int"}]}""",
                """{"functionName":"numericParamsName", "params":[{"name":1, "datatype":"int"}]}""",
                """{"functionName":"missingParamsDataType", "params":[{"name":"p1"}]}""",
                """{"functionName":"incorrectParamsDataType", "params":[{"name":"p1", "datatype":"notAType"}]}""",
                """{"functionName":"stringParamsMultiple", "params":[{"name":"p1", "datatype":"int", "multiple":"true"}]}""",
                """{"functionName":"stringParamsNullable", "params":[{"name":"p1", "datatype":"int", "nullable":"true"}]}""",
                """{"functionName":"numericReturn", "return":1}""",
                """{"functionName":"missingReturnDataType", "return":{"multiple":true}}}""",
                """{"functionName":"incorrectReturnDataType", "return":{"datatype":"notAType"}}}""",
                """{"functionName":"stringReturnMultiple", "return":{"datatype":"int", "multiple":"true"}}}""",
                """{"functionName":"stringReturnNullable", "return":{"datatype":"int", "nullable":"true"}}}"""
        )
        for (i in badApis.indices) {
            val badApi = badApis[i]
            var expectedFailure = false
            try {
                generator.validateFunction("test$i", badApi)
                fail("test $i didn't fail: $badApi")
            } catch (e: JsonSchemaException) {
                // println(e.message)
                expectedFailure = true
            } catch (e: Throwable) {
                fail("test $i with unexpected failure: $badApi\n$e")
            }
            if (!expectedFailure) {
                fail("test $i with uncaught failure: $badApi")
            }
        }
    }
}