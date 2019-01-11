package org.mgnl.nicki.db.dynamic.objects;

public class SyncException extends Exception {
	private static final long serialVersionUID = -6320536593750638922L;
	
	public SyncException(Exception e) {
		super(e);
	}
}
