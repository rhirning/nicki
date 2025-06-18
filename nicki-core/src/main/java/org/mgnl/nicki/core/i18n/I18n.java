
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


import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.ThreadContext;


/**
 * The Class I18n.
 */
public final class I18n {
	
	/** The instance. */
	private static I18n instance = new I18n();
	
	/** The message bases. */
	private List<String> messageBases = new ArrayList<String>();
	
	/**
	 * Instantiates a new i 18 n.
	 */
	private I18n() {
	}
	
	/**
	 * Gets the translated text.
	 *
	 * @param key the key
	 * @return the translated text
	 */
	private String getTranslatedText(String key) {
		Config.getInstance();
		for (String base : messageBases) {
			try {
				ResourceBundle texts = ResourceBundle.getBundle(base, ThreadContext.getLocale());
				if (texts.containsKey(key)) {
					return texts.getString(key);
				}
			} catch (Exception e) {
				continue;
			}
		}
		return key;
	}
	
	/**
	 * Gets the translated text.
	 *
	 * @param key the key
	 * @param data the data
	 * @return the translated text
	 */
	private String getTranslatedText(String key, String ... data) {
		Config.getInstance();
		String text = getText(key);
		for (int i = 0; i < data.length; i++) {
			String pattern = "${" + (i+1) + "}";
			if (StringUtils.contains(text, pattern)) {
				text = StringUtils.replace(text, pattern, data[i]);
			}
		}
		return text;
	}
	
	/**
	 * Gets the text.
	 *
	 * @param key the key
	 * @return the text
	 */
	public static String getText(String key) {
		return instance.getTranslatedText(key);
	}
	
	/**
	 * Gets the text.
	 *
	 * @param key the key
	 * @param data the data
	 * @return the text
	 */
	public static String getText(String key, String ... data) {
		return instance.getTranslatedText(key, data);
	}
	
	/**
	 * Adds the message base.
	 *
	 * @param base the base
	 */
	public static void addMessageBase(String base) {
		instance.messageBases.add(base);
	}

	/**
	 * Gets the texts.
	 *
	 * @param base the base
	 * @param keys the keys
	 * @return the texts
	 */
	public static String[] getTexts(String base, String... keys) {
		List<String> texts = new ArrayList<String>();
		for (String key: keys) {
			texts.add(I18n.getText(base + "." + key));
		}
		return texts.toArray(new String[0]);
	}
}
