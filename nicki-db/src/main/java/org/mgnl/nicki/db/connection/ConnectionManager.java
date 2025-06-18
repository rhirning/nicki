
package org.mgnl.nicki.db.connection;

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


import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;


/**
 * The Class ConnectionManager.
 */
@Slf4j
public class ConnectionManager {

    
    /** The data source. */
    public DataSource dataSource;
    
    /** The pool. */
    public GenericObjectPool pool;
    
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
     *  connectToDB - Connect to the DB!.
     *
     * @param config the config
     */
    private void connectToDB(DbcpConfiguration config ) {

        try
        {
            Classes.newInstance( config.getDbDriverName() );
        }
        catch(Exception e)
        {
            log.error("Error when attempting to obtain DB Driver: "
                    + config.getDbDriverName() + " on "
                    + new Date().toString(), e);
        }

        log.debug("Trying to connect to database...");
        try
        {
        	dataSource = setupDataSource(
                    config.getDbURI(),
                    config.getDbUser(),
                    config.getDbPassword(),
                    config.getDbPoolMinSize(),
                    config.getDbPoolMaxSize() );

            log.debug("Connection attempt to database succeeded.");
        }
        catch(Exception e)
        {
            log.error("Error when attempting to connect to DB ", e);
        }
    }

    /**
     * Setup data source.
     *
     * @param connectURI - JDBC Connection URI
     * @param username - JDBC Connection username
     * @param password - JDBC Connection password
     * @param minIdle - Minimum number of idel connection in the connection pool
     * @param maxActive - Connection Pool Maximum Capacity (Size)
     * @return the data source
     * @throws Exception the exception
     */
    public DataSource setupDataSource(
    		String connectURI, 
    		String username,
    		String password,
    		int minIdle, int maxActive
	) throws Exception {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        pool = new GenericObjectPool(null);

        pool.setMinIdle( minIdle );
        pool.setMaxActive( maxActive );
        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string from configuration
        //
        ConnectionFactory connectionFactory = 
        	new DriverManagerConnectionFactory(connectURI,username, password);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
         new PoolableConnectionFactory(
        	connectionFactory,pool,null,null,false,true);

        PoolingDataSource dataSource = 
        	new PoolingDataSource(pool);

        return dataSource;
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
	public synchronized Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}




}
