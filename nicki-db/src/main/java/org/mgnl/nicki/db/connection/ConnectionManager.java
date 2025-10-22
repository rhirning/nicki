
package org.mgnl.nicki.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mgnl.nicki.core.util.Classes;

/*-
 * #%L
 * nicki-db
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

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ConnectionManager.
 */
@Slf4j
public class ConnectionManager {

	/** The data source. */
	public DataSource dataSource;

	/** The pool. */
	private GenericObjectPool<PoolableConnection> pool;

	/**
	 * Instantiates a new connection manager.
	 */
	public ConnectionManager() {

	}

	/**
	 * Inits the.
	 *
	 * @param profileConfigBase the profile config base
	 * @throws InvalidConfigurationException the invalid configuration exception
	 */
	public void init(String profileConfigBase) throws InvalidConfigurationException {
		DbcpConfiguration configuration = new DbcpConfiguration(profileConfigBase);
		connectToDB(configuration);
	}

	/**
	 * connectToDB - Connect to the DB!.
	 *
	 * @param config the config
	 */
	private void connectToDB(DbcpConfiguration config) {

		try {
			Classes.newInstance(config.getDbDriverName());
		} catch (Exception e) {
			log.error("Error when attempting to obtain DB Driver: " + config.getDbDriverName() + " on "
					+ new Date().toString(), e);
		}

		log.debug("Trying to connect to database...");
		try {
			dataSource = setupDataSource(config.getDbURI(), config.getDbUser(), config.getDbPassword(),
					config.getDbPoolMinIdleSize(), config.getDbPoolMaxIdleSize(), config.getJmxName());

			log.debug("Connection attempt to database succeeded.");
		} catch (Exception e) {
			log.error("Error when attempting to connect to DB ", e);
		}
	}

	private DataSource setupDataSource(String connectURI, String user, String password) {
		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

		//
		// Now we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);

		// Set the factory's pool property to the owning pool
		poolableConnectionFactory.setPool(connectionPool);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(connectionPool);

		return dataSource;
	}

	/**
	 * Setup data source.
	 *
	 * @param connectURI - JDBC Connection URI
	 * @param username   - JDBC Connection username
	 * @param password   - JDBC Connection password
	 * @param minIdle    - Minimum number of idle connection in the connection pool
	 * @param maxIdle    - Maximum number of idle connection in the connection pool
	 * @return the data source
	 * @throws Exception the exception
	 */
	public DataSource setupPoolingDataSource(String connectURI, String username, String password, int minIdle,
			int maxIdle) throws Exception {
		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, username, password);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

		//
		// Now we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		pool = new GenericObjectPool<>(poolableConnectionFactory);

		pool.setMinIdle(minIdle);
		pool.setMaxIdle(maxIdle);

		// Set the factory's pool property to the owning pool
		poolableConnectionFactory.setPool(pool);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		return new PoolingDataSource<>(pool);
	}

	/**
	 * Setup data source.
	 *
	 * @param connectURI - JDBC Connection URI
	 * @param username   - JDBC Connection username
	 * @param password   - JDBC Connection password
	 * @param minIdle    - Minimum number of idle connection in the connection pool
	 * @param maxIdle    - Maximum number of idle connection in the connection pool
	 * @return the data source
	 * @throws Exception the exception
	 */
	public DataSource setupDataSource(String connectURI, String username, String password, int minIdle, int maxIdle,
			String jmxDataSourceName) throws Exception {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("oracle.jdbc.OracleDriver");
		ds.setUrl(connectURI);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setMinIdle(minIdle);
		ds.setMaxIdle(maxIdle);
		if (StringUtils.isNotBlank(jmxDataSourceName)) {
			String jmxName = BasicDataSource.class.getPackage().getName() + ":type=DataSource,name="
					+ jmxDataSourceName;
			ds.setJmxName(jmxName); // damit wird JMX aktiviert
		}
		return ds;
	}

	/**
	 * Prints the driver stats.
	 *
	 * @throws Exception the exception
	 */
	public void printDriverStats() throws Exception {
		log.info("NumActive: " + pool.getNumActive());
		log.info("NumIdle: " + pool.getNumIdle());
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
