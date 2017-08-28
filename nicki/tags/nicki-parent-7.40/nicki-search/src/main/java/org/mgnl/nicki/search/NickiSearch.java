package org.mgnl.nicki.search;

import java.io.IOException;
import java.util.Collection;
import java.util.List;


public interface NickiSearch<T> {

	//String ATTRIBUTE_HASH = "hash";
	String ATTRIBUTE_CATEGORY = "category";
	String ATTRIBUTE_KEY = "key";
	String ATTRIBUTE_TITLE = "title";
	String ATTRIBUTE_DESCRIPTION = "description";
	String ATTRIBUTE_CONTENT = "content";
	void setIndexPath(String property);
	void index(Collection<T> values, Extractor<T> xtractor);
	List<NickiSearchResult> search(String searchString, int maxCount) throws IOException;
}
