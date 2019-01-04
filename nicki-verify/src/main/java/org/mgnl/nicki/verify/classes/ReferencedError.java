
package org.mgnl.nicki.verify.classes;

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


public class ReferencedError {
	public enum TYPE {ERROR, WARN};
	
	private TYPE type;
	private String reference;
	private String message;
	private Object command;
	private boolean revalidate;
	
	public ReferencedError(TYPE type, String reference, String message) {
		super();
		this.type = type;
		this.reference = reference;
		this.message = message;
	}
	
	public ReferencedError(TYPE type, String reference, String message, Object command) {
		this(type, reference, message);
		this.command = command;
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
		if (command != null) {
			sb.append(", command=").append(command.toString());
		}
		sb.append("}");
		return sb.toString();
	}
	public Object getCommand() {
		return command;
	}

	public boolean isRevalidate() {
		return revalidate;
	}

	public void setRevalidate(boolean revalidate) {
		this.revalidate = revalidate;
	}
}
