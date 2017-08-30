/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.db.connection;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    
    public DataSource dataSource;
    public GenericObjectPool pool;
    
    public ConnectionManager() {
    	
    }
    
	public void init(String profileConfigBase) throws InvalidConfigurationException {
		DbcpConfiguration configuration = new DbcpConfiguration(profileConfigBase);
    	connectToDB(configuration);
    }

    /**
    *  connectToDB - Connect to the DB!
    */
    private void connectToDB(DbcpConfiguration config ) {

        try
        {
            Classes.newInstance( config.getDbDriverName() );
        }
        catch(Exception e)
        {
            LOG.error("Error when attempting to obtain DB Driver: "
                    + config.getDbDriverName() + " on "
                    + new Date().toString(), e);
        }

        LOG.debug("Trying to connect to database...");
        try
        {
        	dataSource = setupDataSource(
                    config.getDbURI(),
                    config.getDbUser(),
                    config.getDbPassword(),
                    config.getDbPoolMinSize(),
                    config.getDbPoolMaxSize() );

            LOG.debug("Connection attempt to database succeeded.");
        }
        catch(Exception e)
        {
            LOG.error("Error when attempting to connect to DB ", e);
        }
    }

    /**
     *
     * @param connectURI - JDBC Connection URI
     * @param username - JDBC Connection username
     * @param password - JDBC Connection password
     * @param minIdle - Minimum number of idel connection in the connection pool
     * @param maxActive - Connection Pool Maximum Capacity (Size)
     * @throws Exception
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

    public void printDriverStats() throws Exception {
        LOG.info("NumActive: " + pool.getNumActive());
        LOG.info("NumIdle: " + pool.getNumIdle());
    }

	public synchronized Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}




}
