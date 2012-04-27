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
package com.marklogic.client.configpojos;

/**
 * Defines values for use in computed buckets anchored to time.
 * @see com.marklogic.client.configpojos.ComputedBucket
 *
 */
public enum AnchorValue {
	
	NOW, START_OF_DAY, START_OF_MONTH, START_OF_YEAR;
	String toXmlString() {
		return this.toString().toLowerCase().replace("_", "-");
	}
	static AnchorValue fromXmlString(String xmlString) {
		return AnchorValue.valueOf(xmlString.toUpperCase().replace("-", "_"));
	}		
				
	
	
}
