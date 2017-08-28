package org.mgnl.nicki.context.db.model;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;

@Table(name = "OBJECTS")
public class Objects {
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;
	@Attribute(name = "model_object_id", foreignKey=true)
	private Long modelObjectId;
	@Attribute(name = "name")
	private String name;
	@Attribute(name = "path")
	private String path;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getModelObjectId() {
		return modelObjectId;
	}
	public void setModelObjectId(Long modelObjectId) {
		this.modelObjectId = modelObjectId;
	}
}
