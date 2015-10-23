package org.mgnl.nicki.verify;

import java.io.IOException;
import java.util.HashMap;
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
		if (attributeRules == null) {
			loadAttributeRules();
		}
	}
	
	public static Verify getInstance() {
		if (instance == null) {
			instance = new Verify();
		}
		return instance;
	}
	
	public void verify(String attributeName, String value) throws VerifyException {
		String rule = attributeRules.get(attributeName.toLowerCase());
		
		StringBuffer sb = new StringBuffer();
		
		if (StringUtils.isNotBlank(rule)) {
			String params[] = rule.split("\\|");
			for (int i = 0; i < params.length; i++) {
				Rule checkRule = null;
				if (StringUtils.equals("required", params[i])) {
					checkRule = new RequiredRule();
				} else if (StringUtils.equals("date", params[i])) {
					checkRule = new DateRule();
				} else if (StringUtils.equals("digits", params[i])) {
					checkRule = new DigitsRule();
				} else if (StringUtils.equals("email", params[i])) {
					checkRule = new EmailRule();
				} else if (StringUtils.equals("number", params[i])) {
					checkRule = new NumberRule();
				} else if (StringUtils.startsWith(params[i],"min:")) {
					checkRule = new MinRule(StringUtils.substringAfter(params[i], "min:"));
				} else if (StringUtils.startsWith(params[i],"max:")) {
					checkRule = new MaxRule(StringUtils.substringAfter(params[i], "max:"));
				} else if (StringUtils.startsWith(params[i],"minlength:")) {
					checkRule = new MinLengthRule(StringUtils.substringAfter(params[i], "minlength:"));
				} else if (StringUtils.startsWith(params[i],"maxlength:")) {
					checkRule = new MaxLengthRule(StringUtils.substringAfter(params[i], "maxlength:"));
				} else if (StringUtils.startsWith(params[i],"regex:")) {
					checkRule = new RegExRule(StringUtils.substringAfter(params[i], "regex:"));
				} else if (StringUtils.startsWith(params[i],"password:")) {
					checkRule = new PasswordRule(StringUtils.substringAfter(params[i], "password:"));
				}
				if (checkRule != null) {
					if (!checkRule.evaluate(value)) {
						if (sb.length() == 0) {
							sb.append(attributeName).append(" - ");
						} else {
							sb.append(", ");
						}
						sb.append(checkRule.getMessage());
					}
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
