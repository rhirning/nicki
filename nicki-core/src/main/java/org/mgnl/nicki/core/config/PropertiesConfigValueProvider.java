package org.mgnl.nicki.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesConfigValueProvider implements ConfigValueProvider {

	private static final String CONFIG_SOURCE_KEY = "nicki.config.valueprovider.source";
	
	private String path;
	private Properties properties = new Properties();
	private boolean isInit;
	
	public PropertiesConfigValueProvider() {
	}

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
	@Override
	public boolean exists(String key) {
		return StringUtils.isNotBlank(get(key));
	}

	@Override
	public String get(String key) {
		if (!isInit) {
			init();
		}
		return properties.getProperty(key);
	}

}
