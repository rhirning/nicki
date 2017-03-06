package org.mgnl.nicki.verify.classes;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ReferenceVerifyException extends Exception {
	private List<ReferencedError> errors;

	public ReferenceVerifyException(List<ReferencedError> errors) {
		this.errors = errors;
	}

	public ReferenceVerifyException(ReferencedError error) {
		errors = new ArrayList<>();
		errors.add(error);
	}

	public List<ReferencedError> getErrors() {
		return errors;
	}

	@Override
	public String getMessage() {
		return errors == null ? null : errors.toString();
	}
}
