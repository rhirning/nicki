package org.mgnl.nicki.context.db.model;

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
