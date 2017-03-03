package org.mgnl.nicki.verify.annotations;

public class MissingAttribute extends Exception {
	private static final long serialVersionUID = 7024676626467083763L;

	public MissingAttribute(String name) {
		super(name);
	}


}
