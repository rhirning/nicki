package org.mgnl.nicki.db.dynamic.objects;

import java.util.Date;

import org.mgnl.nicki.core.objects.DynamicObject;

public interface SyncEntry {

	
	void setUniqueId(Long uniqueId);
	void setType(String type);
	void setId(String id);
	void setFrom(Date from);
	void setTo(Date to);
	void setAttribute(String attribute);
	void setContent(String content);
	
	
	Long getUniqueId();
	
	String getType();
	
	String getId();

	Date getFrom();

	Date getTo();
	
	String getAttribute();
	
	String getContent();
}
