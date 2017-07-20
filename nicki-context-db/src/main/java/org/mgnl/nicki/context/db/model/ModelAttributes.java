package org.mgnl.nicki.context.db.model;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;

@Table(name = "MODEL_ATTRIBUTES")
public class ModelAttributes {
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;
	@Attribute(name = "attributeName")
	private String attributeName;
	@Attribute(name = "dataType")
	private DataType dataType;

	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
