/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.verify;

import org.mgnl.nicki.core.i18n.I18n;

public class RequiredRule extends MinLengthRule {
	private static final long serialVersionUID = 1L;

	public RequiredRule() {
		super("1");
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".required");
	}

	public String toString() {
		return "required";
	}

}
