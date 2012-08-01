package org.mgnl.nicki.dynamic.objects.shop;

import java.util.List;

public interface CatalogValueProvider {
	
	boolean hasEntries();
	
	boolean isChecking();
	
	<T extends Object> List<T> getEntries();
	
	<T extends Object> boolean checkEntry(T entry);

}
