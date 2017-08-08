package org.mgnl.nicki.context.db.model;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;

@Table(name = "MODEL_OBJECTS")
public class ModelObjects {
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;
	@Attribute(name = "name")
	private String name;
	@Attribute(name = "description")
	private Long description;
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
	public Long getDescription() {
		return description;
	}
	public void setDescription(Long description) {
		this.description = description;
	}
}
