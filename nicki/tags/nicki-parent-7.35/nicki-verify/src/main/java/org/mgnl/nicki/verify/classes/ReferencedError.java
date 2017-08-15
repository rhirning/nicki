package org.mgnl.nicki.verify.classes;

public class ReferencedError {
	public enum TYPE {ERROR, WARN};
	
	private TYPE type;
	private String reference;
	private String message;
	
	public ReferencedError(TYPE type, String reference, String message) {
		super();
		this.type = type;
		this.reference = reference;
		this.message = message;
	}
	public String getReference() {
		return reference;
	}
	public String getMessage() {
		return message;
	}
	public TYPE getType() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type).append("{");
		sb.append(reference).append(": ").append(message);
		sb.append("}");
		return sb.toString();
	}
}
