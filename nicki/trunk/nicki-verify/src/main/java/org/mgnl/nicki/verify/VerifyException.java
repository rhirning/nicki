package org.mgnl.nicki.verify;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class VerifyException extends Exception {
	private List<String> errors;

	public VerifyException(List<String> errors) {
		this.errors = errors;
	}

	public VerifyException(String message) {
		errors = new ArrayList<>();
		errors.add(message);
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public String getMessage() {
		return errors == null ? null : errors.toString();
	}
}
