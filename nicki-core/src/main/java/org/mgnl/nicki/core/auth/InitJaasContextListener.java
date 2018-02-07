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

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitJaasContextListener implements ServletContextListener {
	static final Logger LOG = LoggerFactory.getLogger(InitJaasContextListener.class);
    public static final String KRB5_CONF			= "spnego.krb5.conf";
    public static final String LOGIN_CONF			= "spnego.login.conf";
    public static final String CONTEXT_CONF			= "nicki.login.context.name";
    public static final String PROPERTY_KRB5_CONF	= "java.security.krb5.conf";
    public static final String PROPERTY_LOGIN_CONF	= "java.security.auth.login.config";
    

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
	                System.setProperty(PROPERTY_KRB5_CONF, krbConfigFile);
	            }
        	}
        }
        LOG.info(PROPERTY_KRB5_CONF + "=" + System.getProperty(PROPERTY_KRB5_CONF));

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
	                System.setProperty(PROPERTY_LOGIN_CONF, jaasConfigFile);
	            }
        	}
        }
        LOG.info(PROPERTY_LOGIN_CONF + "=" + System.getProperty(PROPERTY_LOGIN_CONF));
        
        checkModule(Config.getString(CONTEXT_CONF));
	}

	private void checkModule(final String moduleName) {
        
        // confirm that runtime loaded the login file
        final Configuration config = Configuration.getConfiguration();
 
        // we only expect one entry
        final AppConfigurationEntry entry = config.getAppConfigurationEntry(moduleName)[0];
        if (entry != null) {
        	LOG.info("LoginContext " + moduleName + " erfolgreich geladen");
        } else {
        	LOG.info("LoginContext " + moduleName + " konnte nicht geladen werden");
        }
    }

	private String getInitParameter(String key) {
		return Config.getString(key);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}
	

}
