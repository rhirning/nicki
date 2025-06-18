
package org.mgnl.nicki.core.helper;


/*-
 * #%L
 * nicki-core
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
 * The Interface AttributeMapper.
 */
public interface AttributeMapper {

	/**
	 * To external.
	 *
	 * @param internal the internal
	 * @return the string
	 */
	String toExternal(String internal);

	/**
	 * To internal.
	 *
	 * @param external the external
	 * @return the string
	 */
	String toInternal(String external);

	/**
	 * Checks for external.
	 *
	 * @param external the external
	 * @return true, if successful
	 */
	boolean hasExternal(String external);

	/**
	 * Checks for internal.
	 *
	 * @param internal the internal
	 * @return true, if successful
	 */
	boolean hasInternal(String internal);
	
	/**
	 * Checks if is strict.
	 *
	 * @return true, if is strict
	 */
	boolean isStrict();

	/**
	 * Checks if is hidden internal.
	 *
	 * @param internal the internal
	 * @return true, if is hidden internal
	 */
	boolean isHiddenInternal(String internal);

	/**
	 * Checks if is hidden external.
	 *
	 * @param external the external
	 * @return true, if is hidden external
	 */
	boolean isHiddenExternal(String external);

	/**
	 * Correct value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the string
	 */
	String correctValue(String key, String value);
}
