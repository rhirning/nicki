package org.mgnl.nicki.core.util;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import lombok.Data;


/**
 * The Class ProtocolEntry.
 */
@Data
public class ProtocolEntry {
	
	/** The action. */
	private String action;
	
	/** The modifier. */
	private String modifier;
	
	/** The data. */
	private String[] data;
	
	/**
	 * Instantiates a new protocol entry.
	 *
	 * @param action the action
	 * @param modifier the modifier
	 * @param data the data
	 */
	public ProtocolEntry(String action, String modifier, String... data) {
		super();
		this.action = action;
		this.modifier = modifier;
		this.data = data;
	}
	
	
}
