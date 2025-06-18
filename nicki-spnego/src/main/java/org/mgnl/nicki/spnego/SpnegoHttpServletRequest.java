
package org.mgnl.nicki.spnego;

/*-
 * #%L
 * nicki-spnego
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


import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.mgnl.nicki.spnego.SpnegoHttpFilter.Constants;

import org.ietf.jgss.GSSCredential;


/**
 * Wrap ServletRequest so we can do our own handling of the 
 * principal and auth types.
 * 
 * <p>Also, see the documentation on the {@link DelegateServletRequest} class.</p>
 * 
 * <p>Finally, a credential delegation example can be found on 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 *
 */
final class SpnegoHttpServletRequest extends HttpServletRequestWrapper 
    implements DelegateServletRequest, SpnegoAccessControl {
    
    /** The Constant MESSAGE_UNSUPPORTED. */
    private static final String MESSAGE_UNSUPPORTED = 
            "User Access Control has NOT been defined or is NOT supported.";
    
    /** Client Principal. */
    private final transient SpnegoPrincipal principal;
    
    /** authZ framework interface. */
    private final transient UserAccessControl accessControl;
    
    /**
     * Creates Servlet Request specifying KerberosPrincipal of user.
     *
     * @param request the request
     * @param spnegoPrincipal the spnego principal
     */
    SpnegoHttpServletRequest(final HttpServletRequest request
        , final SpnegoPrincipal spnegoPrincipal) {
        
        this(request, spnegoPrincipal, null);
    }
    
    /**
     * Creates Servlet Request specifying KerberosPrincipal of user 
     * and a specified User Access Control (authZ).
     *
     * @param request the request
     * @param spnegoPrincipal the spnego principal
     * @param userAccessControl the user access control
     */
    SpnegoHttpServletRequest(final HttpServletRequest request
        , final SpnegoPrincipal spnegoPrincipal
        , final UserAccessControl userAccessControl) {
        
        super(request);
        
        this.principal = spnegoPrincipal;
        this.accessControl = userAccessControl;
    }
    
    /**
     * Returns "Negotiate" or "Basic" else default auth type.
     *
     * @return the auth type
     * @see jakarta.servlet.http.HttpServletRequest#getAuthType()
     */
    @Override
    public String getAuthType() {
        
        final String authType;
        final String header = this.getHeader(Constants.AUTHZ_HEADER);
        
        if (null == header) {
            authType = super.getAuthType();
            
        } else if (header.startsWith(Constants.NEGOTIATE_HEADER)) {
            authType = Constants.NEGOTIATE_HEADER;

        } else if (header.startsWith(Constants.BASIC_HEADER)) {
            authType = Constants.BASIC_HEADER;
            
        } else {
            authType = super.getAuthType();
        }
        
        return authType;
    }
    
    /**
     * Gets the delegated credential.
     *
     * @return the delegated credential
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.DelegateServletRequest#getDelegatedCredential()
     */
    @Override
    public GSSCredential getDelegatedCredential() {
        return this.principal.getDelegatedCredential();
    }
    
    /**
     * Returns authenticated username (sans domain/realm) else default username.
     *
     * @return the remote user
     * @see jakarta.servlet.http.HttpServletRequest#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        
        if (null == this.principal) {
            return super.getRemoteUser();
            
        } else {
            final String[] username = this.principal.getName().split("@", 2);
            return username[0];
        }
    }
    
    /**
     * Returns KerberosPrincipal of user.
     *
     * @return the user principal
     * @see jakarta.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }
    
    /**
     * Any role.
     *
     * @param roles the roles
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#anyRole(java.lang.String[])
     */
    @Override
    public boolean anyRole(final String... roles) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.anyRole(this.getRemoteUser(), roles);
    }
    
    /**
     * Checks for role.
     *
     * @param role the role
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasRole(java.lang.String)
     */
    @Override
    public boolean hasRole(final String role) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), role);        
    }
    
    /**
     * Checks for role.
     *
     * @param featureX the feature X
     * @param featureYs the feature ys
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasRole(java.lang.String, java.lang.String[])
     */
    @Override
    public boolean hasRole(final String featureX, final String... featureYs) {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), featureX, featureYs);
    }
    
    /**
     * Any access.
     *
     * @param resources the resources
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#anyAccess(java.lang.String[])
     */
    @Override
    public boolean anyAccess(final String... resources) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.anyAccess(this.getRemoteUser(), resources);        
    }
    
    /**
     * Checks for access.
     *
     * @param resource the resource
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasAccess(java.lang.String)
     */
    @Override
    public boolean hasAccess(final String resource) {
     // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasAccess(this.getRemoteUser(), resource);
    }
    
    /**
     * Checks for access.
     *
     * @param resourceX the resource X
     * @param resourceYs the resource ys
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#hasAccess(java.lang.String, java.lang.String[])
     */
    @Override
    public boolean hasAccess(final String resourceX, final String... resourceYs) {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasAccess(this.getRemoteUser(), resourceX, resourceYs);        
    }

    /**
     * Gets the user info.
     *
     * @return the user info
     */
    /*
     * (non-Javadoc)
     * @see net.sourceforge.spnego.SpnegoAccessControl#getUserInfo()
     */
    @Override
    public UserInfo getUserInfo() {
        // assert
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        final UserInfo userInfo = this.accessControl.getUserInfo(this.getRemoteUser());
        
        if (null == userInfo) {
            throw new UnsupportedOperationException("UserInfo was NULL and/or not configured");
        } else {
            return userInfo;
        }
    }
    
    /**
     * Checks if is user in role.
     *
     * @param role the role
     * @return true, if is user in role
     */
    /*
     * (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletRequestWrapper#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(final String role) {
        if (null == this.accessControl) {
            throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
        }
        
        return this.accessControl.hasRole(this.getRemoteUser(), role);
    }
}
