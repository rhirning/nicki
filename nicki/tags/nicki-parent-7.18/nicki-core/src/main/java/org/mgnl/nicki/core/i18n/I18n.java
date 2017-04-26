/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.ThreadContext;

public final class I18n {
	private static I18n instance = new I18n();
	
	private List<String> messageBases = new ArrayList<String>();
	private I18n() {
	}
	
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
	
	public static String getText(String key) {
		return instance.getTranslatedText(key);
	}
	
	public static String getText(String key, String ... data) {
		return instance.getTranslatedText(key, data);
	}
	
	public static void addMessageBase(String base) {
		instance.messageBases.add(base);
	}

	public static String[] getTexts(String base, String... keys) {
		List<String> texts = new ArrayList<String>();
		for (String key: keys) {
			texts.add(I18n.getText(base + "." + key));
		}
		return texts.toArray(new String[]{});
	}
}
