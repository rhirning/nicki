package org.mgnl.nicki.vaadin.base.data;

public interface Deletable {
	boolean isDeletable();
	boolean isDeleted();
	void delete();
	void undelete();
}
