
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


import org.apache.commons.lang3.StringUtils;

public class PathHelper {

	public static boolean isPathEqual(String refPath, String comparePath) {
		if (StringUtils.equalsIgnoreCase(refPath, comparePath)) {
			return true;
		}

		return false;
	}

	public static String getSlashPath(String parentPath, String childPath) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(parentPath)) {
			if (StringUtils.equals(parentPath, childPath)) {
				return "/";
			}
			childPath = StringUtils.substringBeforeLast(childPath, "," + parentPath);
		}
		
		String parts[] = StringUtils.split(childPath, ",");
		if (parts != null) {
			for (int i = parts.length - 1; i >=0; i--) {
				String part = StringUtils.substringAfter(parts[i], "=");
				sb.append("/");
				sb.append(part);
			}
		}
		return sb.toString();
	}

}
