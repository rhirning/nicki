
package org.mgnl.nicki.core.auth;

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

import javax.security.auth.kerberos.KerberosPrincipal;

import org.ietf.jgss.GSSCredential;

// TODO: Auto-generated Javadoc
/**
 * This class encapsulates a KerberosPrincipal.
 * 
 * <p>This class also has a reference to the client's/requester's 
 * delegated credential (if any). See the {@link DelegateServletRequest} 
 * documentation for more details.</p>
 * 
 * <p>Also, see the delegation examples at 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 *
 */
public final class SpnegoPrincipal implements Principal {

    /** The kerberos principal. */
    private final transient KerberosPrincipal kerberosPrincipal;
    
    /** The credentials. */
    private final transient byte[] credentials;
    
    /** The delegated cred. */
    private final transient GSSCredential delegatedCred;
    
    /**
     * Constructs a SpnegoPrincipal from the provided String input.
     * 
     * @param name the principal name
     */
    public SpnegoPrincipal(final String name) {
        this.kerberosPrincipal = new KerberosPrincipal(name);
        this.credentials = null;
        this.delegatedCred = null;
    }
    
    /**
     * Constructs a SpnegoPrincipal from the provided String input 
     * and name type input.
     * 
     * @param name the principal name
     * @param nameType the name type of the principal
     * @param credentials this principal's credential (if any)
     */
    public SpnegoPrincipal(final String name, final int nameType, final byte[] credentials) {
        this.kerberosPrincipal = new KerberosPrincipal(name, nameType);
        this.credentials = credentials;
        this.delegatedCred = null;
    }

    /**
     * Constructs a SpnegoPrincipal from the provided String input 
     * and name type input.
     *
     * @param name the principal name
     * @param nameType the name type of the principal
     * @param credentials the credentials
     * @param delegCred this principal's delegated credential (if any)
     */
    public SpnegoPrincipal(final String name, final int nameType, final byte[] credentials
        , final GSSCredential delegCred) {
        
        this.kerberosPrincipal = new KerberosPrincipal(name, nameType);
        this.credentials = credentials;
        this.delegatedCred = delegCred;
    }
    
    /**
     * Returns this Principal's credential or null.
     * 
     * @return Principal's credential or null.
     */
    public byte[] getCredential() {
        return this.credentials;
    }
    
    /**
     * Returns this Principal's delegated credential or null.
     * 
     * @return Principal's delegated credential or null.
     */
    public GSSCredential getDelegatedCredential() {
        return this.delegatedCred;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.kerberosPrincipal.getName();
    }
    
    /**
     * Returns the name type of the KerberosPrincipal.
     * 
     * @return name type of the KerberosPrincipal
     */
    public int getNameType() {
        return this.kerberosPrincipal.getNameType();
    }
    
    /**
     * Returns the realm component of this Kerberos principal.
     * 
     * @return realm component of this Kerberos principal
     */
    public String getRealm() {
        return this.kerberosPrincipal.getRealm();
    }
    
    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + this.kerberosPrincipal.hashCode();
        result = 31 * result + this.delegatedCred.hashCode();
        
        return result;
    }
    
    /**
     * Equals.
     *
     * @param object the object
     * @return true, if successful
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        if (!(object instanceof SpnegoPrincipal)) {
            return false;
        }
        
        final SpnegoPrincipal obj = (SpnegoPrincipal) object;
        if (!this.kerberosPrincipal.equals(obj.kerberosPrincipal)
                || !this.delegatedCred.equals(obj.delegatedCred)) {
            
            return false;
        }
        
        return this.hashCode() == obj.hashCode();
    }
    
    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.kerberosPrincipal.toString();
    }
}
