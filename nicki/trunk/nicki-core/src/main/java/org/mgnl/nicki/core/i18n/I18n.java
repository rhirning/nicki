/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.ThreadContext;

public class I18n {
	private static I18n instance = new I18n();
	
	private List<String> messageBases = new ArrayList<String>();
	private I18n() {
	}
	
	private String getTranslatedText(String key) {
		Config.isInitPerformed();
		for (Iterator<String> iterator = messageBases.iterator(); iterator.hasNext();) {
			String base = iterator.next();
			try {
				ResourceBundle texts = ResourceBundle.getBundle(base, ThreadContext.getLocale());
				if (texts.containsKey(key)) {
					return texts.getString(key);
				}
			} catch (Exception e) {
			}
		}
		return key;
	}
	
	private String getTranslatedText(String key, String ... data) {
		Config.isInitPerformed();
		String text = getText(key);
		for (int i = 0; i < data.length; i++) {
			String pattern = "${" + (i+1) + "}";
			if (StringUtils.contains(text, pattern)) {
				text = StringUtils.replace(text, pattern, data[i]);
			}
		}
		return text;
	}
	
	public static String getText(String key) {
		return instance.getTranslatedText(key);
	}
	
	public static String getText(String key, String ... data) {
		return instance.getTranslatedText(key, data);
	}
	
	public static void addMessageBase(String base) {
		instance.messageBases.add(base);
	}
}
