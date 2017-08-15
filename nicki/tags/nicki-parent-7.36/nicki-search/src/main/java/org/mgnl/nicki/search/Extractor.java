package org.mgnl.nicki.search;

public interface Extractor<T extends Object> {
	String getCategory(T object);
	String getKey(T object);
	String getTitle(T object);
	
	String getDescription(T object);
	
	boolean accept(T object);
	
	
}
