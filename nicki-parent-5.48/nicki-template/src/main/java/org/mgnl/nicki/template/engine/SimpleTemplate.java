package org.mgnl.nicki.template.engine;

import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SimpleTemplate {
	
	private String name;
	private String data;
	private Map<String, String> part;
	
	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public Map<String, String> getPart() {
		return this.part;
	}

	public void setPart(Map<String, String> part) {
		this.part = part;
	}
	public boolean containsKey(String key) {
		return this.part.containsKey(key);
	}

	public String getPart(String key) {
		return part.get(key);
	}
	public String getName() {
		return name;
	}
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

}
