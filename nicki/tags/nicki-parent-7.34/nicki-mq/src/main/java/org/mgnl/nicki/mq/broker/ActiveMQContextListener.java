package org.mgnl.nicki.mq.broker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.store.kahadb.plist.PListStoreImpl;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQContextListener implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(ActiveMQContextListener.class);
	private BrokerService broker;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		boolean startBroker = DataHelper.booleanOf(Config.getProperty("nicki.mq.broker.start", "FALSE"));
		if (startBroker) {
			LOG.info("Load ActiveMQ");
			// configure the broker
			try {			
				broker = new BrokerService();
				broker.setPlugins(getPlugins());
				broker.setPersistenceAdapter(getPersistenceAdapter());
				broker.setTempDataStore(getTempDataStore());
				broker.addConnector(Config.getProperty("nicki.mq.connector"));
				
				broker.start();
				LOG.info("ActiveMQ loaded succesfully");
			} catch (Exception e) {
				LOG.error("Unable to load ActiveMQ!", e);
			}
		} else {
			LOG.info("ActiveMQ not started");			
		}
	}

	private PListStore getTempDataStore() {
		PListStoreImpl store = new PListStoreImpl();
		store.setDirectory(new File(Config.getProperty("nicki.mq.tempstore")));
		return store;
	}

	private PersistenceAdapter getPersistenceAdapter() {
		KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
		persistenceAdapter.setDirectory(new File(Config.getProperty("nicki.mq.store")));
		return persistenceAdapter;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (broker != null) {
			try {
				LOG.info("ActiveMQ exiting");
				broker.stop();
				LOG.info("ActiveMQ exit succesfully");
			} catch (Exception e) {
				LOG.error("Unable to exit ActiveMQ!", e);
			}
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
		users.add(new AuthenticationUser("producer", Config.getProperty("nicki.mq.user.password.producer"),
				"publishers,consumers"));
		users.add(new AuthenticationUser("consumer", Config.getProperty("nicki.mq.user.password.consumer"),
				"publishers,consumers"));
		users.add(new AuthenticationUser("admin", Config.getProperty("nicki.mq.user.password.admin"),
				"admins,publishers,consumers"));
		plugin.setUsers(users);
		
		return plugin;
	}

}
