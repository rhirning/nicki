
package org.mgnl.nicki.mq.broker;

/*-
 * #%L
 * nicki-mq-broker
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
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class BrokerThread.
 */
@Slf4j
public class BrokerThread extends Thread implements Runnable {
	
	/** The Constant DEFAULT_CHARSET. */
	public final static String DEFAULT_CHARSET = "UTF-8";
	
	/** The stop. */
	private boolean stop;
	
	/** The broker. */
	private BrokerService broker;

	/**
	 * Instantiates a new broker thread.
	 */
	public BrokerThread() {
		setDaemon(true);
	}

	/**
	 * Run.
	 */
	public void run() {
		// wait
		int wait = Config.getInteger("nicki.mq.broker.wait", 10);
		log.info("Waiting " + wait + " seconds before starting broker");
		try {
			Thread.sleep(1000 * wait);
		} catch (InterruptedException e1) {
			log.info("Wait interrupted");
		}
		// configure the broker
		try {
			broker = new BrokerService();
			broker.setPlugins(getPlugins());
			broker.setPersistenceAdapter(getPersistenceAdapter());
			broker.addConnector(Config.getString("nicki.mq.connector"));
			if (Config.getBoolean("nicki.mq.broker.jmx.use", false)) {
				broker.setUseJmx(true);
				ManagementContext managementContext = new ManagementContext();
				boolean setContext = false;
				if (StringUtils.isNotBlank(Config.getString("nicki.mq.broker.jmx.port"))) {
					managementContext.setConnectorPort(Config.getInteger("nicki.mq.broker.jmx.port"));
					log.info("ConnectorPort: " + Config.getInteger("nicki.mq.broker.jmx.port"));
					setContext = true;
				}
				if (StringUtils.isNotBlank(Config.getString("nicki.mq.broker.jmx.domain"))) {
					managementContext.setJmxDomainName(Config.getString("nicki.mq.broker.jmx.domain"));
					log.info("JmxDomainName: " + Config.getString("nicki.mq.broker.jmx.domain"));
					setContext = true;
				}
				if (setContext) {
					broker.setManagementContext(managementContext);
				}
			} else {
				broker.setUseJmx(false);
			}
			broker.start();
			log.info("ActiveMQ loaded succesfully");

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
						log.error("Error stopping broker", e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Unable to load ActiveMQ!", e);
		}
	}

	/**
	 * Gets the plugins.
	 *
	 * @return the plugins
	 */
	public BrokerPlugin[] getPlugins() {
		List<BrokerPlugin> plugins = new ArrayList<>();
		plugins.add(getAuthenticationPlugin());

		return plugins.toArray(new BrokerPlugin[0]);
	}

	/**
	 * Gets the authentication plugin.
	 *
	 * @return the authentication plugin
	 */
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

	/**
	 * Gets the kaha DB persistence adapter.
	 *
	 * @return the kaha DB persistence adapter
	 */
	private PersistenceAdapter getKahaDBPersistenceAdapter() {
		KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
		persistenceAdapter.setDirectory(new File(Config.getString("nicki.mq.store")));
		return persistenceAdapter;
	}

	/**
	 * Gets the JDBC persistence adapter.
	 *
	 * @return the JDBC persistence adapter
	 */
	private PersistenceAdapter getJDBCPersistenceAdapter() {
		JDBCPersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter();
		persistenceAdapter
				.setCreateTablesOnStartup(Config.getBoolean("nicki.mq.tables.create", false));

		DBContext dbContext = DBContextManager.getContext(Config.getString("nicki.mq.context"));
		persistenceAdapter.setDataSource(dbContext.getDataSource());
		return persistenceAdapter;
	}

	/**
	 * Gets the persistence adapter.
	 *
	 * @return the persistence adapter
	 */
	private PersistenceAdapter getPersistenceAdapter() {
		String persistenceType = Config.getString("nicki.mq.persistenceType", "kaha");
		if (StringUtils.equalsIgnoreCase("JDBC", persistenceType)) {
			return getJDBCPersistenceAdapter();
		} else {
			return getKahaDBPersistenceAdapter();
		}
	}

	/**
	 * Sets the stop.
	 *
	 * @param stop the new stop
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
