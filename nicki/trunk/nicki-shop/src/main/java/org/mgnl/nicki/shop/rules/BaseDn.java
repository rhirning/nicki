package org.mgnl.nicki.shop.rules;

public class BaseDn {
	public enum TYPE {SELF,ALL};
	private String path;
	private TYPE type;

	public BaseDn(String path, TYPE type) {
		super();
		this.path = path;
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public TYPE getType() {
		return type;
	}
}

