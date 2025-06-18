
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


import java.util.ArrayList;
import java.util.List;


/**
 * The Class MessageVerifyException.
 */
@SuppressWarnings("serial")
public class MessageVerifyException extends Exception {
	
	/** The messages. */
	private List<String> messages;
	
	/** The revalidate. */
	private boolean revalidate;

	/**
	 * Instantiates a new message verify exception.
	 *
	 * @param messages the messages
	 */
	public MessageVerifyException(List<String> messages) {
		this.messages = messages;
	}

	/**
	 * Instantiates a new message verify exception.
	 *
	 * @param message the message
	 */
	public MessageVerifyException(String message) {
		messages = new ArrayList<>();
		messages.add(message);
	}

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return messages == null ? null : messages.toString();
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
