/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.db.connection;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.config.ConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConnectionManager implements ConfigListener{

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static ConnectionManager instance = new ConnectionManager();
    
    public Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
    public Map<String, GenericObjectPool> pools = new HashMap<String, GenericObjectPool>();
    
    private ConnectionManager() {
    	try {
			Config.addConfigListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static ConnectionManager getInstance() {
    	return instance;
    }
    
	public void configChanged() {
    	this.dataSources.clear();
    	this.pools.clear();
    	ConfigurationManager.getInstance().configChanged();
    	for (Iterator<String> iterator
    			= ConfigurationManager.getInstance().getConfigurations().keySet().iterator(); iterator.hasNext();) {
			String id = iterator.next();
            connectToDB(id,
            		ConfigurationManager.getInstance().getConfiguration(id) );
			
		}
    }

    /**
    *  destructor
    */
    protected void finalize()
    {
        LOG.debug("Finalizing ConnectionManager");
        try
        {
            super.finalize();
        }
        catch(Throwable ex)
        {
            LOG.error( "ConnectionManager finalize failed to disconnect", ex );
        }
    }


    /**
    *  connectToDB - Connect to the DB!
    */
    private void connectToDB(String id, Configuration config ) {

        try
        {
            java.lang.Class.forName( config.getDbDriverName() ).newInstance();
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
        	dataSources.put(id, setupDataSource(id,
                    config.getDbURI(),
                    config.getDbUser(),
                    config.getDbPassword(),
                    config.getDbPoolMinSize(),
                    config.getDbPoolMaxSize() ));

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
    public DataSource setupDataSource(String id,
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
        GenericObjectPool connectionPool = new GenericObjectPool(null);

        connectionPool.setMinIdle( minIdle );
        connectionPool.setMaxActive( maxActive );
        this.pools.put(id, connectionPool);
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
        	connectionFactory,connectionPool,null,null,false,true);

        PoolingDataSource dataSource = 
        	new PoolingDataSource(connectionPool);

        return dataSource;
    }

    public void printDriverStats() throws Exception {
    	for (Iterator<String> iterator = this.pools.keySet().iterator(); iterator.hasNext();) {
			String id = iterator.next();
	        ObjectPool connectionPool = this.pools.get(id);
	        LOG.info("NumActive: " + connectionPool.getNumActive());
	        LOG.info("NumIdle: " + connectionPool.getNumIdle());
			
		}
    }

	public Connection getConnection(String name) throws SQLException {
		return this.dataSources.get(name).getConnection();
	}




}
