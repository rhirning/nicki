/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
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
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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
			LOG.error("Error", e);
		}
    }
    
    public static ConnectionManager getInstance() {
    	return instance;
    }
    
	public void configChanged() {
    	this.dataSources.clear();
    	this.pools.clear();
    	ConfigurationManager.getInstance().configChanged();
    	for (String id : ConfigurationManager.getInstance().getConfigurations().keySet()) {
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
    	for (String id : this.pools.keySet()) {
	        ObjectPool connectionPool = this.pools.get(id);
	        LOG.info("NumActive: " + connectionPool.getNumActive());
	        LOG.info("NumIdle: " + connectionPool.getNumIdle());
			
		}
    }

	public Connection getConnection(String name) throws SQLException {
		return this.dataSources.get(name).getConnection();
	}




}
