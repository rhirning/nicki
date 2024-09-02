
package org.mgnl.nicki.verify;

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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.mgnl.nicki.core.i18n.I18n;

/**
 * ?berpr?ft, ob in einer geordneten Liste die ben?tigten Teile vorhanden sind.
 * Hierzu werden die g?ltigen Kombinationen festgelegt.
 * <p>
 * Beispiel: 024 bedeutet dass NUR die Elemente 0,2,4 vorhanden sein m?ssen<p>
 * 
 * Die Liste wird als String mit einem Separator (Standard: "|") geliefert.
 * 
 * Die g?ltigen Part-Kombinationen sind durch Komma getrennt.
 * Der Listen-Separator kann auch vor den g?ltigen Kombinationen (getrennt mit ":") definiert werden<p>
 * 
 * Beispiele:<br>
 * Parameter: 024,01<br>
 * <table>
 * <tr><td>J|1|2|3</td><td>falsch</td></tr>
 * <tr><td>J|1</td><td>wahr</td></tr>
 * <tr><td>J||sdf||1</td><td>wahr</td></tr>
 * </table>
 * <p>
 * Parameter: ;:024,01
 * <table>
 * <tr><td>J;1;2;3</td><td>falsch</td></tr>
 * <tr><td>J;1</td><td>wahr</td></tr>
 * <tr><td>J;;sdf;;1</td><td>wahr</td></tr>
 * </table>
 * @author rhirning
 *
 */
public class PartRule extends Rule {
	private String separator = "|";
	private List<List<Integer>> list = new ArrayList<>();
	
	private static final long serialVersionUID = 1L;

	public PartRule(String parameter) {
		setParameter(parameter);
		if (StringUtils.contains(parameter, ":")) {
			separator = StringUtils.substringBefore(parameter, ":");
			parameter = StringUtils.substringAfter(parameter, ":");
		}
		
		for (String entry : StringUtils.split(parameter, ",")) {
			List<Integer> listEntry = new ArrayList<>();
			list.add(listEntry);
			for (int i = 0; i < entry.length(); i++) {
				String pos = StringUtils.substring(entry, i, i+1);
				Integer value = Integer.parseInt(pos);
				listEntry.add(value);
			}
		}
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		StringTokenizer tokenizer = new StringTokenizer(value, separator);
		tokenizer.setIgnoreEmptyTokens(false);
		String parts[] = tokenizer.getTokenArray();
		
		for (List<Integer> listEntry : list) {
			boolean pass = true;
			for (int i = 0; i < parts.length; i++) {
				if (StringUtils.isBlank(parts[i]) && listEntry.contains(i)) {
					pass = false;
					break;
				} else if (StringUtils.isNotBlank(parts[i]) && !listEntry.contains(i)) {
					pass = false;
					break;
				}
			}
			if (pass) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".part");
	}
	public String toString() {
		return "part:" + list;
	}



}
