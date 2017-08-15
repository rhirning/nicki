package org.mgnl.nicki.context.db.model;

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
