package org.mgnl.nicki.db.statistics;

public class MissingDataException extends Exception {
	private static final long serialVersionUID = 5308155045734655035L;

	public MissingDataException(String key) {
		super(key);
	}


}
