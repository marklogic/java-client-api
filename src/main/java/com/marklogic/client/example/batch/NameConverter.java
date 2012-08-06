/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.example.batch;

/**
 * NameConverter mangles and unmangles strings as local names
 * for XML elements.
 * Warning: This example provides only crude, partial detection
 * of Unicode characters that require escaping in an NCName.
 * A more robust implementation could use 
 * org.apache.xerces.util.XML11Char for character and value testing.
 */
public class NameConverter {
	final static public int HYPHEN_CP     = "-".codePointAt(0);
	final static public int PERIOD_CP     = ".".codePointAt(0);
	final static public int UNDERSCORE_CP = "_".codePointAt(0);

	static public String unmangleFromNCName(String name) {
		if (name == null)
			return null;
		if ("_".equals(name))
			return "";

		StringBuilder buf = new StringBuilder();

		int namelen = name.length();
		for (int i=0; i < namelen; i++) {
			char c = name.charAt(i);
			if (c != '_') {
				buf.append(c);
				continue;
			}

			StringBuilder hex = null;
			for (; i < namelen; i++) {
				c = name.charAt(i);
				if (c == '_')
					break;

				if (hex == null) {
					hex = new StringBuilder();
				}
				hex.append(c);
			}
			if (c != '_') {
				throw new IllegalArgumentException("not a mangled name: "+name);
			}

			if (hex == null) {
				buf.append('_');
			} else {
				buf.append(
					Character.toChars(
						Integer.parseInt(hex.toString(), 16))
					);
			}
		}

		return buf.toString();
	}

	static public String mangleToNCName(String string) {
		if (string == null)
			return null;
		if (string.length() == 0)
			return "_";

		StringBuilder buf = new StringBuilder();

        for (int i=0; i < string.length(); i++) {
            appendToNCName(i==0, string.codePointAt(i), buf);
		}

		return buf.toString();
	}
	static public void appendToNCName(boolean isFirstChar, int cp, StringBuilder buf) {
		if (isValidForNCName(isFirstChar, cp)) {
			buf.appendCodePoint(cp);
		} else if (cp == UNDERSCORE_CP) {
			buf.append("__");
		} else {
			buf.appendCodePoint(UNDERSCORE_CP);
			buf.append(Integer.toHexString(cp));
			buf.appendCodePoint(UNDERSCORE_CP);
		}
	}
	static public boolean isValidForNCName(boolean isFirstChar, int cp) {
        int type = Character.getType(cp);
        switch (type) {
        case Character.LETTER_NUMBER: 
        case Character.LOWERCASE_LETTER:
        case Character.OTHER_LETTER:
        case Character.TITLECASE_LETTER:
        case Character.UPPERCASE_LETTER:
        	return true;
        case Character.COMBINING_SPACING_MARK:
        case Character.DECIMAL_DIGIT_NUMBER:
        case Character.ENCLOSING_MARK:
        case Character.MODIFIER_LETTER:
        case Character.NON_SPACING_MARK:
        	return !isFirstChar;
        }

        if (cp == PERIOD_CP || cp == HYPHEN_CP) {
			return !isFirstChar;
		}

		return false;
	}
}
