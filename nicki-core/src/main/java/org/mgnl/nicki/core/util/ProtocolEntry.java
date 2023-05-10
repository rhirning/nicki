package org.mgnl.nicki.core.util;

import lombok.Data;

@Data
public class ProtocolEntry {
	private String action;
	private String modifier;
	private String[] data;
	public ProtocolEntry(String action, String modifier, String... data) {
		super();
		this.action = action;
		this.modifier = modifier;
		this.data = data;
	}
	
	
}
