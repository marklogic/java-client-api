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
package com.marklogic.client.config;



public interface ComputedBucket extends BoundQueryOption {

	public String getName();
	public void setName(String name);
	
	public String getGe();
	public void setGe(String ge);
	
	public String getLt();
	public void setLt(String lt);
	
	public AnchorValue getAnchor();
	public void setAnchor(AnchorValue name);
	
	public AnchorValue getLtAnchor();
	public void setLtAnchor(AnchorValue name);
	
	public AnchorValue getGeAnchor();
	public void setGeAnchor(AnchorValue name);
	
	public String getContent();
	public void setContent(String content);
	
}
