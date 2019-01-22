package org.mgnl.nicki.db.dynamic.objects;

import org.mgnl.nicki.core.objects.DynamicObject;

public interface SyncIdGenerator {
	String getId(DynamicObject dynamicObject);
}
