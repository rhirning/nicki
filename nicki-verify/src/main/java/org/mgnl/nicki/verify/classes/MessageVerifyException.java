
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

@SuppressWarnings("serial")
public class MessageVerifyException extends Exception {
	private List<String> messages;
	private boolean revalidate;

	public MessageVerifyException(List<String> messages) {
		this.messages = messages;
	}

	public MessageVerifyException(String message) {
		messages = new ArrayList<>();
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	@Override
	public String getMessage() {
		return messages == null ? null : messages.toString();
	}

	public boolean isRevalidate() {
		return revalidate;
	}

	public void setRevalidate(boolean revalidate) {
		this.revalidate = revalidate;
	}
}
