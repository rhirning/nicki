package org.mgnl.nicki.db.fs;


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

/**
 * The Class FileStoreException.
 */
@SuppressWarnings("serial")
public class FileStoreException extends Exception {

	/**
	 * Instantiates a new file store exception.
	 *
	 * @param e the e
	 */
	public FileStoreException(Exception e) {
		super(e);
	}

}
