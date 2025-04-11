package org.mgnl.nicki.core.config;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertiesConfigValueProvider.
 */
@Slf4j
public class PropertiesConfigValueProvider implements ConfigValueProvider {

	/** The Constant CONFIG_SOURCE_KEY. */
	private static final String CONFIG_SOURCE_KEY = "nicki.config.valueprovider.source";
	
	/** The path. */
	private String path;
	
	/** The properties. */
	private Properties properties = new Properties();
	
	/** The is init. */
	private boolean isInit;
	
	/**
	 * Instantiates a new properties config value provider.
	 */
	public PropertiesConfigValueProvider() {
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		path = Config.getValue(CONFIG_SOURCE_KEY);
		if (StringUtils.isNotBlank(path)) {
			
			try (FileInputStream fis = new FileInputStream(path)) {
				properties.load(fis);
			} catch (IOException e) {
				log.error("Could not load properties file: " + path);
			}
		}
		isInit = true;
	}
	
	/**
	 * Exists.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	@Override
	public boolean exists(String key) {
		return StringUtils.isNotBlank(get(key));
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	@Override
	public String get(String key) {
		if (!isInit) {
			init();
		}
		return properties.getProperty(key);
	}

}
