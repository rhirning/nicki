
package org.mgnl.nicki.mq.broker;

/*-
 * #%L
 * nicki-mq
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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerThread extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(BrokerThread.class);
	public final static String DEFAULT_CHARSET = "UTF-8";
	private boolean stop;
	private BrokerService broker;

	public BrokerThread() {
		setDaemon(true);
	}

	public void run() {
		// wait
		int wait = DataHelper.getInteger(Config.getProperty("nicki.mq.broker.wait", "10"), 10);
		LOG.info("Waiting " + wait + " seconds before starting broker");
		try {
			Thread.sleep(1000 * wait);
		} catch (InterruptedException e1) {
			LOG.info("Wait interrupted");
		}
		// configure the broker
		try {
			broker = new BrokerService();
			broker.setPlugins(getPlugins());
			broker.setPersistenceAdapter(getPersistenceAdapter());
			broker.addConnector(Config.getString("nicki.mq.connector"));
			broker.setUseJmx(DataHelper.booleanOf(Config.getProperty("nicki.mq.usejmx", "FALSE")));

			broker.start();
			LOG.info("ActiveMQ loaded succesfully");

			try {
				while (true) {
					try {
						Thread.sleep(1000);
						if (stop) {
							break;
						}
					} catch (InterruptedException e) {
						break;
					}
				}
			} finally {
				if (broker != null) {
					try {
						broker.stop();
					} catch (Exception e) {
						LOG.error("Error stopping broker", e);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to load ActiveMQ!", e);
		}
	}

	public BrokerPlugin[] getPlugins() {
		List<BrokerPlugin> plugins = new ArrayList<>();
		plugins.add(getAuthenticationPlugin());

		return plugins.toArray(new BrokerPlugin[0]);
	}

	public SimpleAuthenticationPlugin getAuthenticationPlugin() {
		SimpleAuthenticationPlugin plugin = new SimpleAuthenticationPlugin();
		List<AuthenticationUser> users = new ArrayList<>();
		users.add(new AuthenticationUser("producer", Config.getString("nicki.mq.user.password.producer"),
				"publishers,consumers"));
		users.add(new AuthenticationUser("consumer", Config.getString("nicki.mq.user.password.consumer"),
				"publishers,consumers"));
		users.add(new AuthenticationUser("admin", Config.getString("nicki.mq.user.password.admin"),
				"admins,publishers,consumers"));
		plugin.setUsers(users);

		return plugin;
	}

	private PersistenceAdapter getKahaDBPersistenceAdapter() {
		KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
		persistenceAdapter.setDirectory(new File(Config.getString("nicki.mq.store")));
		return persistenceAdapter;
	}

	private PersistenceAdapter getJDBCPersistenceAdapter() {
		JDBCPersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter();
		persistenceAdapter
				.setCreateTablesOnStartup(DataHelper.booleanOf(Config.getProperty("nicki.mq.tables.create", "FALSE")));

		DBContext dbContext = DBContextManager.getContext(Config.getString("nicki.mq.context"));
		persistenceAdapter.setDataSource(dbContext.getDataSource());
		return persistenceAdapter;
	}

	private PersistenceAdapter getPersistenceAdapter() {
		String persistenceType = Config.getProperty("nicki.mq.persistenceType", "kaha");
		if (StringUtils.equalsIgnoreCase("JDBC", persistenceType)) {
			return getJDBCPersistenceAdapter();
		} else {
			return getKahaDBPersistenceAdapter();
		}
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
