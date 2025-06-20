package org.mgnl.nicki.db.dynamic.objects;

/*-
 * #%L
 * nicki-db
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

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * The Class SyncChange.
 */
@Data
@AllArgsConstructor
public class SyncChange {
	
	/**
	 * The Enum ACTION.
	 */
	public enum ACTION {
/** The add. */
ADD, 
 /** The remove. */
 REMOVE, 
 /** The modify. */
 MODIFY}
	
	/** The date. */
	private Date date;
	
	/** The action. */
	private ACTION action;
	
	/** The attribute. */
	private String attribute;
	
	/** The value. */
	private String value;

}
