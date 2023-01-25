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

public class NullAttributeMapper implements AttributeMapper {

	@Override
	public String toExternal(String internal) {
		return internal;
	}

	@Override
	public String toInternal(String external) {
		return external;
	}

	@Override
	public boolean hasExternal(String external) {
		return true;
	}

	@Override
	public boolean hasInternal(String internal) {
		return true;
	}

	@Override
	public boolean isStrict() {
		return true;
	}

	@Override
	public boolean isHiddenInternal(String internal) {
		return false;
	}

	@Override
	public boolean isHiddenExternal(String external) {
		return false;
	}

	@Override
	public String correctValue(String key, String value) {
		return value;
	}

}
