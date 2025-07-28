/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.expression;

import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprCol;
import com.marklogic.client.type.ServerExpression;

import java.util.Map;

/**
 * The RdtExpr instance provides functions that build expressions
 * for redacting the values of a column.
 * <p>In addition to using the provided functions,
 * you can redact any column by using {@link PlanBuilder#as(PlanColumn, ServerExpression)}
 * to rebind the column to an expression that replaces the existing value
 * with an altered or randomly generated value.
 * You can also hide a column by binding the column to the null value
 * or by projecting other columns.
 * </p>
 */
public interface RdtExpr {
    /**
     * Redacts a column with string values by replacing each value with deterministic masking text.
     * That is, a specific value generates the same masked value every time the value is redacted.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:mask-deterministic" target="mlserverdoc">ordt:mask-deterministic</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object
     */
    PlanExprCol maskDeterministic(PlanColumn column);
    /**
     * Redacts a column with string values by replacing each value with deterministic masking text.
     * That is, a specific value generates the same masked value every time the value is redacted.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:mask-deterministic" target="mlserverdoc">ordt:mask-deterministic</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object
     */
    PlanExprCol maskDeterministic(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with string values by replacing each value with random masking text.
     * The same value may produce a different masked value every time the value is redacted.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:mask-random" target="mlserverdoc">ordt:mask-random</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol maskRandom(PlanColumn column);
    /**
     * Redacts a column with string values by replacing each value with random masking text.
     * The same value may produce a different masked value every time the value is redacted.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:mask-random" target="mlserverdoc">ordt:mask-random</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol maskRandom(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with date or datetime values either by masking part
     * of the existing value or by generating a random value.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-datetime" target="mlserverdoc">ordt:redact-datetime</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactDatetime(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with email address string
     * that conforms to the pattern <code>name@domain</code>.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-email" target="mlserverdoc">ordt:redact-email</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactEmail(PlanColumn column);
    /**
     * Redacts a column with email address string
     * that conforms to the pattern <code>name@domain</code>.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-email" target="mlserverdoc">ordt:redact-email</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactEmail(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with IPv4 address string that conforms to a pattern with
     * four blocks of 1-3 decimal digits separated by period (.) where the value of each block
     * of digits is less than or equal to 255 as in <code>123.201.098.112</code> and
     * <code>123.45.678.0</code>.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-ipv4" target="mlserverdoc">ordt:redact-ipv4</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactIpv4(PlanColumn column);
    /**
     * Redacts a column with IPv4 address string that conforms to a pattern with
     * four blocks of 1-3 decimal digits separated by period (.) where the value of each block
     * of digits is less than or equal to 255 as in <code>123.201.098.112</code> and
     * <code>123.45.678.0</code>.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-ipv4" target="mlserverdoc">ordt:redact-ipv4</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactIpv4(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column by generating a random number within a configurable range
     * either as a numeric data type or as a formatted string.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-number" target="mlserverdoc">ordt:redact-number</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactNumber(PlanColumn column);
    /**
     * Redacts a column by generating a random number within a configurable range
     * either as a numeric data type or as a formatted string.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-number" target="mlserverdoc">ordt:redact-number</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactNumber(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a string column by applying a regular expression.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-regex" target="mlserverdoc">ordt:redact-regex</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactRegex(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with a 10-digit US phone number string
     * by generating random numbers or replacing numbers with a masking character.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-us-phone" target="mlserverdoc">ordt:redact-us-phone</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactUsPhone(PlanColumn column);
    /**
     * Redacts a column with a 10-digit US phone number string
     * by generating random numbers or replacing numbers with a masking character.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-us-phone" target="mlserverdoc">ordt:redact-us-phone</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactUsPhone(PlanColumn column, Map<String,?> options);
    /**
     * Redacts a column with a 9-digit US SSN (Social Security Number) string
     * by generating random numbers or replacing numbers with a masking character.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-us-ssn" target="mlserverdoc">ordt:redact-us-ssn</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactUsSsn(PlanColumn column);
    /**
     * Redacts a column with a 9-digit US SSN (Social Security Number) string
     * by generating random numbers or replacing numbers with a masking character.
     * <p>Provides a client interface to the
     * <a href="http://docs.marklogic.com/ordt:redact-us-ssn" target="mlserverdoc">ordt:redact-us-ssn</a>
     * server function.</p>
     * @param column  the column to be redacted
     * @param options  the options for redacting the column
     * @return  a PlanExprCol object for the redacted column
     */
    PlanExprCol redactUsSsn(PlanColumn column, Map<String,?> options);
}
