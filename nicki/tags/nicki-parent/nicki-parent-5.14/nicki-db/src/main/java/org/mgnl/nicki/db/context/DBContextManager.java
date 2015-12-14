package org.mgnl.nicki.db.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.connection.InvalidConfigurationException;
import org.mgnl.nicki.db.profile.DBCPProfile;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.JndiDBProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBContextManager {
	private static final Logger LOG = LoggerFactory.getLogger(DBContextManager.class);

	private static final String PROPERTY_CONTEXTS = "nicki.db.contexts";
	private static final String PROPERTY_CONTEXT_BASE = "nicki.db.context";
	private static final String PROPERTY_CONTEXT_CLASS_NAME = "contextClassName";
	private static final String PROPERTY_CONTEXT_DATA_SOURCE = "datasource";
	private static final String PROPERTY_CONTEXT_CONNECTION_TYPE = "type";
	private static final String PROPERTY_CONTEXT_SCHEMA = "schema";
	private static final String PROPERTY_CONTEXT_AUTO_COMMIT = "autocommit";
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
		String contextsConfigurations = Config.getProperty(PROPERTY_CONTEXTS);
		if (StringUtils.isNotEmpty(contextsConfigurations)) {
			for (String contextName : DataHelper.getList(contextsConfigurations, SEPARATOR)) {
				try {
					String contextBase = PROPERTY_CONTEXT_BASE + "." + contextName + ".";
					String contextClassName = Config.getProperty(contextBase
							+ PROPERTY_CONTEXT_CLASS_NAME);
					DBContext context = (DBContext) getClass().getClassLoader().loadClass(contextClassName).newInstance();
					
					String schema = Config.getProperty(contextBase + PROPERTY_CONTEXT_SCHEMA);
					if (StringUtils.isNotBlank(schema)) {
						context.setSchema(schema);
					}
					
					DBProfile profile = createProfile(contextName);
					if (profile != null) {
						context.setProfile(profile);
						contexts.put(contextName, context);
					}
				} catch (Exception e) {
					LOG.error("error init DBContexts", e);
				}
			}
		}
	}

	private DBProfile createProfile(String contextName) throws InvalidConfigurationException {
		String contextBase = PROPERTY_CONTEXT_BASE + "." + contextName + ".";
		String type  = Config.getProperty(contextBase + PROPERTY_CONTEXT_CONNECTION_TYPE);
		boolean autoCommit  = DataHelper.booleanOf(Config.getProperty(contextBase + PROPERTY_CONTEXT_AUTO_COMMIT, "false")) ;
		
		if (StringUtils.equals("dbcp", type)) {
			String profileConfigBase = contextBase + "dbcp.";
			return new DBCPProfile(profileConfigBase, autoCommit);
		} else {
			String dataSource = Config.getProperty(contextBase + PROPERTY_CONTEXT_DATA_SOURCE);
			return new JndiDBProfile(dataSource, autoCommit);
		}
	}
	
	public static DBContext getContext(String name) {
		return getInstance().contexts.get(name);
	}
	
	
}
