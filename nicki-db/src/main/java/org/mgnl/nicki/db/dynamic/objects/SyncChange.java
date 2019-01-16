package org.mgnl.nicki.db.dynamic.objects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SyncChange {
	public enum ACTION {ADD, REMOVE, MODIFY}
	
	private Date date;
	private ACTION action;
	private String attribute;
	private String value;

}
