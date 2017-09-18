
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


import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;

@Table(name = "MODEL_OBJECTS_ATTRIBUTES")
public class ModelObjectsAttributes {
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;
	@Attribute(name = "model_object_id", foreignKey=true)
	private String modelObjectId;
	@Attribute(name = "model_attribute_id", foreignKey=true)
	private Long modelAttributeId;
	@Attribute(name = "mandatory")
	private boolean mandatory;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getModelObjectId() {
		return modelObjectId;
	}
	public void setModelObjectId(String modelObjectId) {
		this.modelObjectId = modelObjectId;
	}
	public Long getModelAttributeId() {
		return modelAttributeId;
	}
	public void setModelAttributeId(Long modelAttributeId) {
		this.modelAttributeId = modelAttributeId;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
}
