/*
 * Copyright 2016-2017 MarkLogic Corporation
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
package com.marklogic.client.expression;

import com.marklogic.client.type.XsIntegerExpr;
import com.marklogic.client.type.XsStringExpr;
import com.marklogic.client.type.XsStringSeqExpr;



// IMPORTANT: Do not edit. This file is generated. 
public interface SpellExpr {
    /**
    * Given a word returns the two metaphone keys. The primary and secondary metaphone keys which represent the phonetic encoding of two words are returned as a sequence of two strings. Double metaphone is an algorithm based on phonetic sounds useful in providing data to spelling correction suggestions.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/spell:double-metaphone" target="mlserverdoc">spell:double-metaphone</a>
    * @param word  The word for phonetic matching.
    * @return  a XsStringSeqExpr expression sequence
    */
    public XsStringSeqExpr doubleMetaphone(XsStringExpr word);
    /**
    * Given two strings, returns the Levenshtein distance between those strings. The Levenshtein distance is a measure of how many operations it takes to transform a string into another string, and it is useful in determining if a word is spelled correctly, or in simply comparing how "different" two words are.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/spell:levenshtein-distance" target="mlserverdoc">spell:levenshtein-distance</a>
    * @param str1  The first input string.
    * @param str2  The second input string.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr levenshteinDistance(XsStringExpr str1, String str2);
    /**
    * Given two strings, returns the Levenshtein distance between those strings. The Levenshtein distance is a measure of how many operations it takes to transform a string into another string, and it is useful in determining if a word is spelled correctly, or in simply comparing how "different" two words are.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/spell:levenshtein-distance" target="mlserverdoc">spell:levenshtein-distance</a>
    * @param str1  The first input string.
    * @param str2  The second input string.
    * @return  a XsIntegerExpr expression
    */
    public XsIntegerExpr levenshteinDistance(XsStringExpr str1, XsStringExpr str2);
    /**
    * Returns the romanization of the string, substituting basic Latin letters for the letters in the string, according to their sound. Unsupported characters will be mapped to '?' for compatibility with the double metaphone algorithm. We support romanization of the scripts of the languages with advanced support in MarkLogic except for Chinese characters and Hangul.
    * <p>
    * Provides a client interface to a server function. See <a href="http://docs.marklogic.com/spell:romanize" target="mlserverdoc">spell:romanize</a>
    * @param string  The input string.
    * @return  a XsStringExpr expression
    */
    public XsStringExpr romanize(XsStringExpr string);
}
