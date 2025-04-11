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

import org.mgnl.nicki.core.helper.AttributeMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class NullAttributeMapper.
 */
public class NullAttributeMapper implements AttributeMapper {

	/**
	 * To external.
	 *
	 * @param internal the internal
	 * @return the string
	 */
	@Override
	public String toExternal(String internal) {
		return internal;
	}

	/**
	 * To internal.
	 *
	 * @param external the external
	 * @return the string
	 */
	@Override
	public String toInternal(String external) {
		return external;
	}

	/**
	 * Checks for external.
	 *
	 * @param external the external
	 * @return true, if successful
	 */
	@Override
	public boolean hasExternal(String external) {
		return true;
	}

	/**
	 * Checks for internal.
	 *
	 * @param internal the internal
	 * @return true, if successful
	 */
	@Override
	public boolean hasInternal(String internal) {
		return true;
	}

	/**
	 * Checks if is strict.
	 *
	 * @return true, if is strict
	 */
	@Override
	public boolean isStrict() {
		return true;
	}

	/**
	 * Checks if is hidden internal.
	 *
	 * @param internal the internal
	 * @return true, if is hidden internal
	 */
	@Override
	public boolean isHiddenInternal(String internal) {
		return false;
	}

	/**
	 * Checks if is hidden external.
	 *
	 * @param external the external
	 * @return true, if is hidden external
	 */
	@Override
	public boolean isHiddenExternal(String external) {
		return false;
	}

	/**
	 * Correct value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the string
	 */
	@Override
	public String correctValue(String key, String value) {
		return value;
	}

}
