
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
