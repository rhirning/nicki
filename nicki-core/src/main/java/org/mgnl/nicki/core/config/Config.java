
package org.mgnl.nicki.core.config;

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


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
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

	/**
	 * returns the property value from the configured config
	 * @param key Key to the property
	 * @return property value or null if property is not defined
	 * @deprecated use getString(..), getInteger(..), getLong(..), getBoolean(..), getList(..), getClassInstance(..) instead
	 */
	@Deprecated
	public static String getProperty(String key) {
		LOG.debug("getProperty:" + key);
		return getInstance().getAndTranslateProperty(key);
	}
	
	public static <T> T getClassInstance(String key) {

		String className = Config.getProperty(key);
		if (StringUtils.isNotEmpty(className)) {
			try {
				return Classes.newInstance(className);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOG.error("Could not create class instance for " + className + ":" + e.getMessage());
			}
		}
		return null;
	}

	public static String getString(String key, String defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? value : defaultValue;
	}

	public static String getString(String key) {
		return StringUtils.stripToNull(getInstance().getAndTranslateProperty(key));
	}

	public static int getInteger(String key, int defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.getInteger(value, defaultValue) : defaultValue;
	}

	public static int getInteger(String key) {
		return getInteger(key, 0);
	}
	
	public static long getLong(String key, long defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.getLong(value, defaultValue) : defaultValue;
	}
	
	public static long getLong(String key) {
		return getLong(key, 0);
	}
	
	/**
	 * Splits a String into a List<String>. Other than String.split each delimiter is recognized, so that empty values are possible / allowed.
	 * <br/><br/>
	 * Input: "1|2||3|4|5" with delimiter "|" results in {"1", "2", "", "3", "4", "5"}
	 * @param key key in config properties
	 * @param separator delimiter
	 * @return List<String> (not null)
	 */
	public static List<String> getList(String key, String separator) {
		String value = getInstance().getAndTranslateProperty(key);
		return DataHelper.getList(value, separator);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.booleanOf(value) : defaultValue;
	}
	
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
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
	/**
	 * returns the property value from the configured config or the defaultValue if not available
	 * @param key Key to the property
	 * @param defaultValue default value
	 * @return property value or null if property is not defined
	 * @deprecated use getString(..), getInteger(..), getLong(..), getBoolean(..), getList(..), getClassInstance(..) instead
	 */
	@Deprecated
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

	public static String[] getStringValues(String[] configNames) {
		List<String> list = new ArrayList<>();
		if (configNames != null && configNames.length > 0) {
			for (String key : configNames) {
				String value = Config.getString(key, null);
				if (StringUtils.isNotBlank(value)) {
					list.add(value);
				}
			}
		}
		return list.toArray(new String[0]);
	}

}
