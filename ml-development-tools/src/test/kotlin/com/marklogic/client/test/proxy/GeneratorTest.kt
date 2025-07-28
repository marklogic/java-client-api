/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
                """{"functionName":"missingReturnDataType", "return":{"multiple":true}}""",
                """{"functionName":"incorrectReturnDataType", "return":{"datatype":"notAType"}}""",
                """{"functionName":"stringReturnMultiple", "return":{"datatype":"int", "multiple":"true"}}""",
                """{"functionName":"stringReturnNullable", "return":{"datatype":"int", "nullable":"true"}}"""
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
