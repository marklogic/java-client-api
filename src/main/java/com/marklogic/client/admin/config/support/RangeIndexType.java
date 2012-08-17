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
package com.marklogic.client.admin.config.support;

import javax.xml.namespace.QName;


public class RangeIndexType implements Typed {
	public QName type;
	private String collation;
	public RangeIndexType(QName qname) {
		this.type = qname;
	}
	public RangeIndexType(String collation) {
		this.type = new QName("xs:string");
		this.collation=collation;
	}
	public String getCollation() {
		return collation;
	}
	public QName getType() {
		return type;
	}
}