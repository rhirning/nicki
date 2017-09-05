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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
/**
 * Creates rules for jquery validation plugin
 * 
 * Parameters must be separated by pipe character "|"
 * 
 * Supported Parameters:
 * 
 * required
 * date
 * digits
 * email
 * number
 * min:10
 * max:10000
 * maxlength:12
 * minlength:3
 * regex: 
 *   	/aus/ 	findet "aus", und zwar in "aus", "Haus", "auserlesen" und "Banause".
 * ^ 	/^aus/ 	findet "aus" am Anfang des zu durchsuchenden Wertes, also in "aus" und "auserlesen", sofern das die ersten Wörter im Wert sind.
 * $ 	/aus$/ 	findet "aus" am Ende des zu durchsuchenden Wertes, also in "aus" und "Haus", sofern das die letzten Wörter im Wert sind.
 * * 	/aus* /	findet "au", "aus", "auss" und "aussssss", also das letzte Zeichen vor dem Stern 0 oder beliebig oft hintereinander wiederholt.
 * + 	/aus+/ 	findet "auss" und "aussssss", also das letzte Zeichen vor dem Plus-Zeichen mindestens einmal oder beliebig oft hintereinander wiederholt.
 * . 	/.aus/ 	findet "Haus" und "Maus", also ein beliebiges Zeichen an einer bestimmten Stelle.
 * .+ 	/.+aus/ 	findet "Haus" und "Kehraus", also eine beliebige Zeichenfolge an einer bestimmten Stelle. Zusammensetzung aus beliebiges Zeichen und beliebig viele davon, jedoch mindestens eines.
 * \b 	/\baus\b/ 	findet "aus" als einzelnes Wort. \b bedeutet eine Wortgrenze.
 * \B 	/\Baus\B/ 	findet "aus" nur innerhalb von Wörtern, z.B. in "hausen" oder "Totalausfall". \B bedeutet keine Wortgrenze.
 * \d 	/\d+/ 	findet eine beliebige ganze Zahl. \d bedeutet eine Ziffer (0 bis 9)
 * \D 	/\D+/ 	findet "-fach" in "3-fach", also keine Ziffer.
 * \f 	/\f/ 	findet ein Seitenvorschubzeichen.
 * \n 	/\n/ 	findet ein Zeilenvorschub-Zeichen.
 * \r 	/\r/ 	findet ein Wagenrücklaufzeichen.
 * \t 	/\t/ 	findet ein Tabulator-Zeichen.
 * \v 	/\v/ 	findet ein vertikales Tabulator-Zeichen.
 * \s 	/\s/ 	findet jede Art von Weißraum-Zeichen ("Whitespace"), also \f, \n, \t, \v und das Leerzeichen.
 * \S 	/\S+/ 	findet ein beliebiges einzelnes Zeichen, das kein "Whitespace" ist, also kein \f\n\t\v und kein Leerzeichen.
 * \w 	/\w+/ 	findet alle alphanumerischen Zeichen und den Unterstrich (typische Bedingung etwa für programmiersprachengerechte selbstvergebene Namen).
 * \W 	/\W/ 	findet ein Zeichen, das nicht alphanumerisch und auch kein Unterstrich ist (typisch zum Suchen nach illegalen Zeichen bei programmiersprachengerechten selbstvergebenen Namen).
 * () 	/(aus)/ 	findet "aus" und merkt es sich intern. Bis zu 9 solcher Klammern (Merkplätze) sind in einem regulären Ausdruck erlaubt.
 * /.../g 	/aus/g 	findet "aus" so oft wie es in dem gesamten zu durchsuchenden Bereich vorkommt. Die Fundstellen werden intern in einem Array gespeichert.
 * /.../i 	/aus/i 	findet "aus", "Aus" und "AUS", also unabhängig von Groß-/Kleinschreibung.
 * /.../gi 	/aus/gi 	findet "aus", so oft wie es in dem gesamten zu durchsuchenden Bereich vorkommt (g) und unabhängig von Groß-/Kleinschreibung (i).
 * 
 * 
 * @author rhi
 *
 */
public class Verify {
	
	private static Map<String, String> attributeRules;
	private static Verify instance;
	
	public Verify() {
	}
	
	public static Verify getInstance() {
		if (instance == null) {
			instance = new Verify();
		}
		return instance;
	}
	
	public void verify(String attributeName, String value, Map<String, String> values) throws VerifyException {
		if (attributeRules == null) {
			loadAttributeRules();
		}
		String rule = attributeRules.get(attributeName.toLowerCase());
		
		try {
			verifyRule(rule, value, values);
		} catch (VerifyException e) {
			StringBuilder sb = new StringBuilder();
			sb.append(attributeName).append(" - ").append(e.getMessage());
			throw new VerifyException(sb.toString());
		}
	}
	
	public static List<Rule> getRules(String orgRules) {
		String separator = "|";
		String rules = orgRules;
		if (StringUtils.startsWith(orgRules, "separator:")) {
			String rest = StringUtils.substringAfter(orgRules, "separator:");
			separator = StringUtils.substring(rest, 0, 1);
			rules = StringUtils.substring(rest, 1);
		}
		List<Rule> rulesList = new ArrayList<>();
		
		if (StringUtils.isNotBlank(rules)) {
			for (String rule : StringUtils.split(rules, separator)) {
				Rule checkRule = null;
				if (StringUtils.equals("required", rule)) {
					checkRule = new RequiredRule();
				} else if (StringUtils.startsWith(rule,"boolean:")) {
					checkRule = new BooleanRule(StringUtils.substringAfter(rule, "boolean:"));
				} else if (StringUtils.equals("date", rule)) {
					checkRule = new DateRule();
				} else if (StringUtils.equals("digits", rule)) {
					checkRule = new DigitsRule();
				} else if (StringUtils.equals("email", rule)) {
					checkRule = new EmailRule();
				} else if (StringUtils.equals("number", rule)) {
					checkRule = new NumberRule();
				} else if (StringUtils.startsWith(rule,"min:")) {
					checkRule = new MinRule(StringUtils.substringAfter(rule, "min:"));
				} else if (StringUtils.startsWith(rule,"max:")) {
					checkRule = new MaxRule(StringUtils.substringAfter(rule, "max:"));
				} else if (StringUtils.startsWith(rule,"minlength:")) {
					checkRule = new MinLengthRule(StringUtils.substringAfter(rule, "minlength:"));
				} else if (StringUtils.startsWith(rule,"maxlength:")) {
					checkRule = new MaxLengthRule(StringUtils.substringAfter(rule, "maxlength:"));
				} else if (StringUtils.startsWith(rule,"regex:")) {
					checkRule = new RegExRule(StringUtils.substringAfter(rule, "regex:"));
				} else if (StringUtils.startsWith(rule,"password:")) {
					checkRule = new PasswordRule(StringUtils.substringAfter(rule, "password:"));
				} else if (StringUtils.startsWith(rule,"dependend:")) {
					checkRule = new DependendRule(StringUtils.substringAfter(rule, "dependend:"));
				} else if (StringUtils.startsWith(rule,"values:")) {
					checkRule = new ValuesRule(StringUtils.substringAfter(rule, "values:"));
				} else if (StringUtils.startsWith(rule,"allowedMapKeys:")) {
					checkRule = new AllowedMapKeysRule(StringUtils.substringAfter(rule, "allowedMapKeys:"));
				} else if (StringUtils.startsWith(rule,"allowedMapValues:")) {
					checkRule = new AllowedMapValuesRule(StringUtils.substringAfter(rule, "allowedMapValues:"));
				} else if (StringUtils.startsWith(rule,"part:")) {
					checkRule = new PartRule(StringUtils.substringAfter(rule, "part:"));
				}
				if (checkRule != null) {
					rulesList.add(checkRule);
				}
			}
		}
		return rulesList;
	}

	public static void verifyRule(String rule, String value, Map<String, String> values) throws VerifyException {
		
		StringBuilder sb = new StringBuilder();
		
		if (StringUtils.isNotBlank(rule)) {
			List<Rule> rules = getRules(rule);
			for (Rule checkRule : rules) {
				if (!checkRule.evaluate(value, values)) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(checkRule.getMessage());
				}
			}
			if (sb.length() > 0) {
				throw new VerifyException(sb.toString());
			}
		}
	}

	private void loadAttributeRules() {
		String path = Config.getProperty("nicki.verify.rules.path");
		attributeRules = new HashMap<String, String>();
		try {
			Properties props = getPropertiesFromClasspath(path);
			for (Object oKey : props.keySet()) {
				String key = (String) oKey;
				attributeRules.put(key.toLowerCase(), props.getProperty(key));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private Properties getPropertiesFromClasspath(String name) throws IOException {
		Properties properties = new Properties() ;
		properties.load(getClass().getResourceAsStream(name));
		return properties;
	}
}
