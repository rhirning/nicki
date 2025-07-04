package org.mgnl.nicki.core.auth;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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

import java.net.URL;
/**
 * ContextListener to initialize JAAS
 */

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import lombok.extern.slf4j.Slf4j;


/**
 * The listener interface for receiving initJaasContext events.
 * The class that is interested in processing a initJaasContext
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addInitJaasContextListener</code> method. When
 * the initJaasContext event occurs, that object's appropriate
 * method is invoked.
 *
 * @see InitJaasContextEvent
 */
@Slf4j
public class InitJaasContextListener implements ServletContextListener {
	
	/** The Constant PATH_PREFIX. */
	public static final String PATH_PREFIX 			= "/C:";
    
    /** The Constant KRB5_CONF. */
    public static final String KRB5_CONF			= "spnego.krb5.conf";
    
    /** The Constant LOGIN_CONF. */
    public static final String LOGIN_CONF			= "spnego.login.conf";
    
    /** The Constant CONTEXT_CONF. */
    public static final String CONTEXT_CONF			= "nicki.login.context.name";
    
    /** The Constant PROPERTY_KRB5_CONF. */
    public static final String PROPERTY_KRB5_CONF	= "java.security.krb5.conf";
    
    /** The Constant PROPERTY_LOGIN_CONF. */
    public static final String PROPERTY_LOGIN_CONF	= "java.security.auth.login.config";
    

	/**
	 * Context initialized.
	 *
	 * @param sce the sce
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
        
        // specify krb5 conf as a System property
    	String krbSystemProperty = System.getProperty(PROPERTY_KRB5_CONF);
    	if (StringUtils.isBlank(krbSystemProperty)) {
	        if (null == getInitParameter(KRB5_CONF)) {
	            throw new IllegalArgumentException("Missing config: " + KRB5_CONF);
	        } else {
	            String krbConfigFile = null;
	            URL krbConfigURL = this.getClass().getClassLoader().getResource(getInitParameter(KRB5_CONF));
	            if(krbConfigURL != null) {
	                krbConfigFile = krbConfigURL.getFile();
	                if (StringUtils.startsWith(krbConfigFile, PATH_PREFIX)) {
	                	krbConfigFile = StringUtils.substringAfter(krbConfigFile, PATH_PREFIX);
	                }
	                System.setProperty(PROPERTY_KRB5_CONF, krbConfigFile);
	            }
        	}
        }
        log.info(PROPERTY_KRB5_CONF + "=" + System.getProperty(PROPERTY_KRB5_CONF));

        // specify login conf as a System property
    	String jaasConfigSystemProperty = System.getProperty(PROPERTY_LOGIN_CONF);
    	if (StringUtils.isBlank(jaasConfigSystemProperty)) {
		    if (null == getInitParameter(LOGIN_CONF)) {
		        throw new IllegalArgumentException("Missing config: " + LOGIN_CONF);
		    } else {
	            String jaasConfigFile = null;
	            URL jaasConfigURL = this.getClass().getClassLoader().getResource(getInitParameter(LOGIN_CONF));
	            if(jaasConfigURL != null) {
	                jaasConfigFile = jaasConfigURL.getFile();
	                if (StringUtils.startsWith(jaasConfigFile, PATH_PREFIX)) {
	                	jaasConfigFile = StringUtils.substringAfter(jaasConfigFile, PATH_PREFIX);
	                }
	                System.setProperty(PROPERTY_LOGIN_CONF, jaasConfigFile);
	            }
        	}
        }
        log.info(PROPERTY_LOGIN_CONF + "=" + System.getProperty(PROPERTY_LOGIN_CONF));
        
        checkModule(Config.getString(CONTEXT_CONF));
	}

	/**
	 * Check module.
	 *
	 * @param moduleName the module name
	 */
	private void checkModule(final String moduleName) {
        
        // confirm that runtime loaded the login file
        final Configuration config = Configuration.getConfiguration();
 
        // we only expect one entry
        final AppConfigurationEntry entry = config.getAppConfigurationEntry(moduleName)[0];
        if (entry != null) {
        	log.info("LoginContext " + moduleName + " erfolgreich geladen");
        } else {
        	log.info("LoginContext " + moduleName + " konnte nicht geladen werden");
        }
    }

	/**
	 * Gets the inits the parameter.
	 *
	 * @param key the key
	 * @return the inits the parameter
	 */
	private String getInitParameter(String key) {
		return Config.getString(key);
	}

	/**
	 * Context destroyed.
	 *
	 * @param sce the sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}
	

}
