
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
