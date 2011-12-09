/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

public class Config {

	private static String MAIN_CONFIG = "/META-INF/nicki/config.properties";
	private static String CONFIG_BASE = "nicki.config";
	private static String I18N_BASE = "nicki.i18n";
	private static String SEPARATOR = ",";
	private static Config instance = new Config();
	private List<Properties> properties = new ArrayList<Properties>();
	private List<String> openProperties = new ArrayList<String>();
	private List<ConfigListener> configListener = new ArrayList<ConfigListener>();
	private static boolean initPerformed = false;
	private Config() {
		
	}
	
	private static synchronized void init() {
		initPerformed = true;
		initConfig();
		initI18n();
	}

	private static void initConfig() {
		// read config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = instance.getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mainConfig != null) {
			String configList = mainConfig.getProperty(CONFIG_BASE, null);
			if (configList != null) {
				for (Iterator<String> iterator = DataHelper.getList(configList, SEPARATOR).iterator(); iterator
						.hasNext();) {
					String configName = iterator.next();
					String configPath = mainConfig.getProperty(CONFIG_BASE + "." + configName, null);
					if (StringUtils.isNotEmpty(configPath)) {
						instance.addProperties(configPath);
					}
				};
				
			}
		}
		instance.configChanged();
	}

	private void addProperties(String configPath) {
		try {
			Properties props = getPropertiesFromClasspath(configPath);
			if (props != null && props.size() > 0) {
				this.properties.add(props);
			}
		} catch (Exception e) {
			this.openProperties.add(configPath);
		}
	}

	private static void initI18n() {
		// read I18n config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = instance.getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mainConfig != null) {
			String messageBaseList = mainConfig.getProperty(I18N_BASE, null);
			if (messageBaseList != null) {
				for (Iterator<String> iterator = DataHelper.getList(messageBaseList, SEPARATOR).iterator(); iterator
						.hasNext();) {
					String name = iterator.next();
					String messageBase = mainConfig.getProperty(I18N_BASE + "." + name, null);
					if (StringUtils.isNotEmpty(messageBase)) {
						try {
							I18n.addMessageBase(messageBase);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				
			}
		}
	}

	public static void addConfigListener(ConfigListener listener) throws IOException {
		getInstance().configListener.add(listener);
		listener.configChanged();
	}

	public static String getProperty(String key) {
		if (instance.openProperties.size() > 0) {
			instance.addOpenProperties();
		}
		return getInstance()._getProperty(key);
	}
	
	private String _getProperty(String key) {
		for (Iterator<Properties> iterator = this.properties.iterator(); iterator.hasNext();) {
			Properties props = iterator.next();
			String value = props.getProperty(key);
			if (value != null) {
				return DataHelper.getPassword(value);
			}
		}
		return null;
	}

	private void addOpenProperties() {
		List<String> toRemove = new ArrayList<String>();
		for (Iterator<String> iterator = this.openProperties.iterator(); iterator.hasNext();) {
			String configPath = iterator.next();
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
		for (Iterator<String> iterator = toRemove.iterator(); iterator.hasNext();) {
			this.openProperties.remove(iterator.next());
		}
	}

	public static String getProperty(String key, String defaultValue) {
		String value = getInstance()._getProperty(key);
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
		for (Iterator<ConfigListener> iterator = this.configListener.iterator(); iterator.hasNext();) {
			iterator.next().configChanged();
		}
	}

	@Override
	public String toString() {
		return this.properties.toString();
	}
	public static Config getInstance() {
		if (!initPerformed) {
			init();
		}
		return instance;
	}

	public static boolean isInitPerformed() {
		if (!initPerformed) {
			init();
		}
		return initPerformed;
	}

}
