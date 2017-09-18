
package org.mgnl.nicki.context.db.model;

/*-
 * #%L
 * nicki-context-db
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;

@Table(name = "DATA")
public class Data {
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;
	@Attribute(name = "object_id", foreignKey=true)
	private String objectId;
	@Attribute(name = "object_attribute_id", foreignKey=true)
	private String objectAttributeId;
	@Attribute(name = "value")
	private String value;
	@Attribute(name = "createTimestamp")
	private Date createTimestamp;
	@Attribute(name = "modifiedTimestamp")
	private Date modifiedTimestamp;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Date getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}
	public Date getModifiedTimestamp() {
		return modifiedTimestamp;
	}
	public void setModifiedTimestamp(Date modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectAttributeId() {
		return objectAttributeId;
	}
	public void setObjectAttributeId(String objectAttributeId) {
		this.objectAttributeId = objectAttributeId;
	}
	
}
