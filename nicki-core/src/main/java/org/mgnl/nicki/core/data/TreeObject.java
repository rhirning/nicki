package org.mgnl.nicki.core.data;

public interface TreeObject {
	String getDisplayName();
	default TreeObject getObject(){return this;};
}
