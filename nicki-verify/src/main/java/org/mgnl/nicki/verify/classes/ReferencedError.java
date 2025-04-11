
package org.mgnl.nicki.verify.classes;

// TODO: Auto-generated Javadoc
/*-
 * #%L
 * nicki-verify
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


/**
 * The Class ReferencedError.
 */
public class ReferencedError {
	
	/**
	 * The Enum TYPE.
	 */
	public enum TYPE {
/** The error. */
ERROR, 
 /** The warn. */
 WARN};
	
	/** The type. */
	private TYPE type;
	
	/** The reference. */
	private String reference;
	
	/** The message. */
	private String message;
	
	/** The command. */
	private Object command;
	
	/** The revalidate. */
	private boolean revalidate;
	
	/**
	 * Instantiates a new referenced error.
	 *
	 * @param type the type
	 * @param reference the reference
	 * @param message the message
	 */
	public ReferencedError(TYPE type, String reference, String message) {
		super();
		this.type = type;
		this.reference = reference;
		this.message = message;
	}
	
	/**
	 * Instantiates a new referenced error.
	 *
	 * @param type the type
	 * @param reference the reference
	 * @param message the message
	 * @param command the command
	 */
	public ReferencedError(TYPE type, String reference, String message, Object command) {
		this(type, reference, message);
		this.command = command;
	}
	
	/**
	 * Gets the reference.
	 *
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type).append("{");
		sb.append(reference).append(": ").append(message);
		if (command != null) {
			sb.append(", command=").append(command.toString());
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public Object getCommand() {
		return command;
	}

	/**
	 * Checks if is revalidate.
	 *
	 * @return true, if is revalidate
	 */
	public boolean isRevalidate() {
		return revalidate;
	}

	/**
	 * Sets the revalidate.
	 *
	 * @param revalidate the new revalidate
	 */
	public void setRevalidate(boolean revalidate) {
		this.revalidate = revalidate;
	}
}
