package org.mgnl.nicki.db.profile;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;


public class DBProfileManager {

	private static final String PROPERTY_PROFILES = "nicki.db.profiles";
	private static final String PROPERTY_PROFILES_BASE = "nicki.db.profile";
	private static final String PROPERTY_PROFILES_DATA_SOURCE = "datasource";
	private static final String PROPERTY_PROFILES_CONNECTION_TYPE = "type";
	private static final String PROPERTY_PROFILES_AUTO_COMMIT = "autocommit";
	private static final String SEPARATOR = ",";
	
	
	private static DBProfileManager instance = null;
	private Map<String, DBProfile> profiles = new HashMap<String, DBProfile>();

	public static DBProfileManager getInstance() {
		if (instance == null) {
			instance = new DBProfileManager();
			instance.init();
		}
		return instance;
	}
	
	public void init() {
		profiles.clear();
		String profileConfigurations = Config.getProperty(PROPERTY_PROFILES);
		if (StringUtils.isNotEmpty(profileConfigurations)) {
			for (String profile : DataHelper.getList(profileConfigurations, SEPARATOR)) {
				addProfile(profile);
				
			}
		}
	}

	private void addProfile(String name) {
		String profileBase = PROPERTY_PROFILES_BASE + "." + name;
		String dataSource = Config.getProperty(profileBase + PROPERTY_PROFILES_DATA_SOURCE);
		String type  = Config.getProperty(profileBase + PROPERTY_PROFILES_CONNECTION_TYPE);
		boolean autoCommit  = DataHelper.booleanOf(Config.getProperty(profileBase + PROPERTY_PROFILES_AUTO_COMMIT, "false")) ;
		
		if (StringUtils.isNotBlank(dataSource)) {
			if (StringUtils.equals("dbcp", type)) {
				profiles.put(name, new DBCPProfile(dataSource, autoCommit));
			} else {
				profiles.put(name, new JndiDBProfile(dataSource, autoCommit));
			}
		}
	}
	
	public static DBProfile getProfile(String name) {
		return getInstance().profiles.get(name);
	}
	
	
}
