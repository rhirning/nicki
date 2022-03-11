package org.mgnl.nicki.core.config;

public interface ConfigValueProvider {

	/**
	 * Checks if the provider has a config value.
	 * @param key key to the config value
	 * @return true if the config value ist not blank
	 */
	boolean exists(String key);

	/**
	 * returns the config value to the key
	 * @param key key to the config value
	 * @return the config value
	 */
	String get(String key);

}
