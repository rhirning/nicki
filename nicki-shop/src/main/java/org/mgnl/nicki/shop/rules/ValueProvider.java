package org.mgnl.nicki.shop.rules;

import org.mgnl.nicki.shop.catalog.Selector;

public interface ValueProvider {

	void init(Selector selector);

	String getValue();

	String getQuery(String value);

	boolean isHierarchical();

	BaseDn getBaseDn(String value);

}
