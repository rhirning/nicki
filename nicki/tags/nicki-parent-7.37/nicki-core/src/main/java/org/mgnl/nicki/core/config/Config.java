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
package org.mgnl.nicki.core.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	private static final String MAIN_CONFIG = "/META-INF/nicki/config.properties";
	private static final String CONFIG_BASE = "nicki.config";
	private static final String I18N_BASE = "nicki.i18n";
	private static final String SEPARATOR = ",";
	private static Config instance = new Config();
	private static boolean initPerformed;
	private List<Properties> properties = new ArrayList<Properties>();
	//private List<String> openProperties = new ArrayList<String>();
	private List<ConfigListener> configListener = new ArrayList<ConfigListener>();
	private Config() {
		
	}
	
	private void init() {
		initConfig();
		initI18n();
	}

	private void initConfig() {
		// read config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			LOG.error("Error reading " + MAIN_CONFIG, e);
		}
		if (mainConfig != null) {
			String configList = mainConfig.getProperty(CONFIG_BASE, null);
			if (configList != null) {
				for(String configName : DataHelper.getList(configList, SEPARATOR)) {
					String configPath = DataHelper.translate(mainConfig.getProperty(CONFIG_BASE + "." + configName, null));
					
					if (StringUtils.isNotEmpty(configPath)) {
						addProperties(configPath);
					}
				}
				
			}
		}
		configChanged();
	}

	private void addProperties(String configPath) {
		try {
			URL resource = getClass().getResource(configPath);
			LOG.info("Using config properties: " + configPath + " (" + resource + ")");
			Properties props = getPropertiesFromClasspath(configPath);
			if (props != null && props.size() > 0) {
				this.properties.add(props);
			}
		} catch (Exception e) {
			LOG.error("Error reading properties at " + configPath);
			//this.openProperties.add(configPath);
		}
	}

	private void initI18n() {
		// read I18n config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		if (mainConfig != null) {
			String messageBaseList = mainConfig.getProperty(I18N_BASE, null);
			if (messageBaseList != null) {
				for (String name : DataHelper.getList(messageBaseList, SEPARATOR)) {
					String messageBase = mainConfig.getProperty(I18N_BASE + "." + name, null);
					if (StringUtils.isNotEmpty(messageBase)) {
						try {
							I18n.addMessageBase(messageBase);
						} catch (Exception e) {
							LOG.error("Error", e);
						}
					}
				}
				
			}
		}
	}

	public static void addConfigListener(ConfigListener listener) throws IOException {
		getInstance().configListener.add(listener);
		listener.configChanged();
	}

	public static String getProperty(String key) {
		LOG.debug("getProperty:" + key);
		/*
		if (getInstance().openProperties.size() > 0) {
			getInstance().addOpenProperties();
		}
		*/
		return getInstance().getAndTranslateProperty(key);
	}
	
	private String getAndTranslateProperty(String key) {
		LOG.debug(key);
		for (Properties props : this.properties) {
			String value = DataHelper.translate(props.getProperty(key));
			if (value != null) {
				return DataHelper.getPassword(value);
			}
		}
		return null;
	}

	/*
	private void addOpenProperties() {
		List<String> toRemove = new ArrayList<String>();
		for (String configPath : this.openProperties) {
			try {
				Properties props = getPropertiesFromClasspath(configPath);
				if (props != null && props.size() > 0) {
					this.properties.add(props);
				}
				toRemove.add(configPath);
				configChanged();
			} catch (Exception e) {
			}
		}
		for (String name : toRemove) {
			this.openProperties.remove(name);
		}
	}
	*/

	public static String getProperty(String key, String defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public Properties getPropertiesFromClasspath(String name) throws IOException {
		Properties properties = new Properties() ;
		properties.load(getClass().getResourceAsStream(name));
		return properties;
	}

	private void configChanged() {
		for (ConfigListener listener :  this.configListener) {
			listener.configChanged();
		}
	}

	@Override
	public String toString() {
		return this.properties.toString();
	}
	
	public static Config getInstance() {
		synchronized (instance) {
			if (!initPerformed) {
				instance.init();
				initPerformed = true;
			}
		}
		return instance;
	}

}
