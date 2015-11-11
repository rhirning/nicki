package org.mgnl.nicki.db.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.profile.DBCPProfile;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.JndiDBProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBContextManager {
	private static final Logger LOG = LoggerFactory.getLogger(DBContextManager.class);

	private static final String PROPERTY_PROFILES = "nicki.db.profiles";
	private static final String PROPERTY_PROFILES_BASE = "nicki.db.profile";
	private static final String PROPERTY_PROFILES_DATA_SOURCE = "datasource";
	private static final String PROPERTY_PROFILES_CONNECTION_TYPE = "type";
	private static final String PROPERTY_PROFILES_CONTEXT_CLASS_NAME = "contextClassName";
	private static final String PROPERTY_PROFILES_AUTO_COMMIT = "autocommit";
	private static final String SEPARATOR = ",";
	
	
	private static DBContextManager instance = null;
	private Map<String, DBContext> contexts = new HashMap<String, DBContext>();

	public static DBContextManager getInstance() {
		if (instance == null) {
			instance = new DBContextManager();
			instance.init();
		}
		return instance;
	}
	
	public void init() {
		contexts.clear();
		String profileConfigurations = Config.getProperty(PROPERTY_PROFILES);
		if (StringUtils.isNotEmpty(profileConfigurations)) {
			for (String profileName : DataHelper.getList(profileConfigurations, SEPARATOR)) {
				try {
					DBProfile profile = createProfile(profileName);
					if (profile != null) {
						String contextClassName = Config.getProperty(PROPERTY_PROFILES_BASE
								+ "." + profileName
								+ "." +PROPERTY_PROFILES_CONTEXT_CLASS_NAME);
						DBContext context = (DBContext) getClass().getClassLoader().loadClass(contextClassName).newInstance();
						context.setProfile(profile);
						contexts.put(profileName, context);
					}
				} catch (Exception e) {
					LOG.error("error init DBContexts", e);
				}
			}
		}
	}

	private DBProfile createProfile(String name) {
		String profileBase = PROPERTY_PROFILES_BASE + "." + name + ".";
		String dataSource = Config.getProperty(profileBase + PROPERTY_PROFILES_DATA_SOURCE);
		String type  = Config.getProperty(profileBase + PROPERTY_PROFILES_CONNECTION_TYPE);
		boolean autoCommit  = DataHelper.booleanOf(Config.getProperty(profileBase + PROPERTY_PROFILES_AUTO_COMMIT, "false")) ;
		
		if (StringUtils.isNotBlank(dataSource)) {
			if (StringUtils.equals("dbcp", type)) {
				return new DBCPProfile(dataSource, autoCommit);
			} else {
				return new JndiDBProfile(dataSource, autoCommit);
			}
		}
		return null;
	}
	
	public static DBContext getContext(String name) {
		return getInstance().contexts.get(name);
	}
	
	
}
