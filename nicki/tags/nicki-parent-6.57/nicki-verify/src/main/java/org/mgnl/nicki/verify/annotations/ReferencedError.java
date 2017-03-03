package org.mgnl.nicki.verify.annotations;

public class ReferencedError {
	private String reference;
	private String message;
	
	public ReferencedError(String reference, String message) {
		super();
		this.reference = reference;
		this.message = message;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(reference).append(": ").append(message);
		return sb.toString();
	}
	

}
