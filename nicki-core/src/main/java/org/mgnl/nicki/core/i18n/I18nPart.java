
package org.mgnl.nicki.core.i18n;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class I18nPart {
	
	private Map<String, String> map = new HashMap<>();
	
	public I18nPart(String path) {
		super();
        try {
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
			String line = "";
			while ((line = in.readLine()) != null) {
				String key = StringUtils.trimToNull(StringUtils.substringBefore(line, "="));
				String value = StringUtils.trimToEmpty(StringUtils.substringAfter(line, "="));
				if (key != null) {
					map.put(key, value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String simpleTranslate(String input) {
		if (input != null) {
			for (String key : map.keySet()) {
				if (StringUtils.contains(input, key)) {
					return StringUtils.replace(input, key, map.get(key));
				}
			}
		}
		return input;
	}

	public String translate(String input) {
		if (input != null) {
			for (String key : map.keySet()) {
				Pattern pattern = Pattern.compile(key);
				Matcher matcher = pattern.matcher(input);
				if (matcher.matches()) {
					String value = map.get(key);
					for (int i = 1; i < 10; i++) {
						String var = "${" + i + "}";
						if (matcher.groupCount()>= i && StringUtils.contains(value, var)) {
							value = StringUtils.replace(value, var, matcher.group(i));
						}
					}
					return value;
				}
			}
		}
		return input;
	}

}
