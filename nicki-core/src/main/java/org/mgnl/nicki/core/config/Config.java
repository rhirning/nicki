
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic configuration class. The main config ist /META-INF/nicki/config.properties
 * 
 * <br/>Example:
<pre>
# paths to the module config files
nicki.config				= env,general
nicki.config.env			= ${/META-INF/nicki/env.properties}
nicki.config.general		= /META-INF/nicki/general.properties

nicki.i18n					= templates,vaadin-base,consulting,dynamic-objects,log4j,projects
nicki.i18n.templates		= i18n.templates.messages
nicki.i18n.vaadin-base		= i18n.vaadin-base.messages
nicki.i18n.consulting		= i18n.consulting.messages
nicki.i18n.dynamic-objects	= i18n.dynamic-objects.messages
nicki.i18n.log4j			= i18n.log4j.messages
nicki.i18n.projects			= i18n.projects.messages
 </pre>
 with <code>nicki.config</code> the properties you define files containing the configuration. Properties are searched in the configured order.
 */
@Slf4j
public final class Config {
	
	/** The Constant CONFIG_VALUE_PROVIDER_KEY. */
	private static final String CONFIG_VALUE_PROVIDER_KEY = "nicki.config.valueprovider";
	
	/** The Constant MAIN_CONFIG. */
	private static final String MAIN_CONFIG = "/META-INF/nicki/config.properties";
	
	/** The Constant CONFIG_BASE. */
	private static final String CONFIG_BASE = "nicki.config";
	
	/** The Constant I18N_BASE. */
	private static final String I18N_BASE = "nicki.i18n";
	
	/** The Constant SEPARATOR. */
	private static final String SEPARATOR = ",";
	
	/** The instance. */
	private static Config instance = new Config();
	
	/** The init performed. */
	private static boolean initPerformed;
	
	/** The properties. */
	private List<Properties> properties = new ArrayList<Properties>();
	
	/** The config listener. */
	// private List<String> openProperties = new ArrayList<String>();
	private List<ConfigListener> configListener = new ArrayList<ConfigListener>();
	
	/** The config value provider. */
	private ConfigValueProvider configValueProvider;
	
	/** The is init. */
	private boolean isInit;
	
	/**
	 * Instantiates a new config.
	 */
	private Config() {
		
	}
	
	/**
	 * Inits the.
	 */
	private void init() {
		initConfig();
		initI18n();
	}

	/**
	 * Inits the config.
	 */
	private void initConfig() {
		// read config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			log.error("Error reading " + MAIN_CONFIG, e);
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

	/**
	 * Adds the properties.
	 *
	 * @param configPath the config path
	 */
	private void addProperties(String configPath) {
		try {
			URL resource = getClass().getResource(configPath);
			log.info("Using config properties: " + configPath + " (" + resource + ")");
			Properties props = getPropertiesFromClasspath(configPath);
			if (props != null && props.size() > 0) {
				this.properties.add(props);
			}
		} catch (Exception e) {
			log.error("Error reading properties at " + configPath);
			//this.openProperties.add(configPath);
		}
	}

	/**
	 * Inits the I 18 n.
	 */
	private void initI18n() {
		// read I18n config from MAIN_CONFIG
		Properties mainConfig = null;
		try {
			mainConfig = getPropertiesFromClasspath(MAIN_CONFIG);
		} catch (Exception e) {
			log.error("Error", e);
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
							log.error("Error", e);
						}
					}
				}
				
			}
		}
	}

	/**
	 * Adds the config listener.
	 *
	 * @param listener the listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void addConfigListener(ConfigListener listener) throws IOException {
		getInstance().configListener.add(listener);
		listener.configChanged();
	}

	/**
	 * returns the property value from the configured config.
	 *
	 * @param key Key to the property
	 * @return property value or null if property is not defined
	 * @deprecated use getString(..), getInteger(..), getLong(..), getBoolean(..), getList(..), getClassInstance(..) instead
	 */
	@Deprecated
	public static String getProperty(String key) {
		log.debug("getProperty:" + key);
		return getInstance().getAndTranslateProperty(key);
	}
	
	/**
	 * Gets the class instance.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @return the class instance
	 */
	public static <T> T getClassInstance(String key) {

		String className = getInstance().getAndTranslateProperty(key);
		if (StringUtils.isNotEmpty(className)) {
			try {
				return Classes.newInstance(className);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.error("Could not create class instance for " + className + ":" + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public static String getString(String key, String defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String getString(String key) {
		return StringUtils.stripToNull(getInstance().getAndTranslateProperty(key));
	}

	/**
	 * Gets the integer.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the integer
	 */
	public static int getInteger(String key, int defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.getInteger(value, defaultValue) : defaultValue;
	}

	/**
	 * Gets the integer.
	 *
	 * @param key the key
	 * @return the integer
	 */
	public static int getInteger(String key) {
		return getInteger(key, 0);
	}
	
	/**
	 * Gets the long.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the long
	 */
	public static long getLong(String key, long defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.getLong(value, defaultValue) : defaultValue;
	}
	
	/**
	 * Gets the long.
	 *
	 * @param key the key
	 * @return the long
	 */
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
	
	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the boolean
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = getInstance().getAndTranslateProperty(key);
		return value != null ? DataHelper.booleanOf(value) : defaultValue;
	}
	
	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	/**
	 * Gets the and translate property.
	 *
	 * @param key the key
	 * @return the and translate property
	 */
	private String getAndTranslateProperty(String key) {
		log.debug(key);
		if (!isInit) {
			if (StringUtils.isNotBlank(getValue(CONFIG_VALUE_PROVIDER_KEY))) {
				String className = getValue(CONFIG_VALUE_PROVIDER_KEY);
				if (StringUtils.isNotEmpty(className)) {
					try {
						@SuppressWarnings("unchecked")
						Class<ConfigValueProvider> clazz = (Class<ConfigValueProvider>) Class.forName(className);
						configValueProvider = Classes.newInstance(clazz);
					} catch (Exception e) {
						log.error("Could not create class instance for " + className + ":" + e.getMessage());
					}
				}
			}
			isInit = true;
		}
		if (configValueProvider != null && configValueProvider.exists(key)) {
			String value = DataHelper.translate(configValueProvider.get(key));
			return DataHelper.getPassword(value);
		}

		return getValue(key);
	}
	
	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public static String getValue(String key) {
		for (Properties props : getInstance().properties) {
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
	 * returns the property value from the configured config or the defaultValue if not available.
	 *
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
	
	/**
	 * Gets the properties from classpath.
	 *
	 * @param name the name
	 * @return the properties from classpath
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Properties getPropertiesFromClasspath(String name) throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(name));
		return properties;
	}

	/**
	 * Config changed.
	 */
	private void configChanged() {
		for (ConfigListener listener :  this.configListener) {
			listener.configChanged();
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return this.properties.toString();
	}
	
	/**
	 * Gets the single instance of Config.
	 *
	 * @return single instance of Config
	 */
	public static Config getInstance() {
		synchronized (instance) {
			if (!initPerformed) {
				instance.init();
				initPerformed = true;
			}
		}
		return instance;
	}

	/**
	 * Gets the string values.
	 *
	 * @param configNames the config names
	 * @return the string values
	 */
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

	/**
	 * Exists.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public static boolean exists(String key) {
		return StringUtils.isNotBlank(getString(key));
	}

}
