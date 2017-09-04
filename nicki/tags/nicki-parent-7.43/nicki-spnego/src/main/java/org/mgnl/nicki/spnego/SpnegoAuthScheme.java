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
package org.mgnl.nicki.spnego;

import org.apache.commons.codec.binary.Base64;
import org.mgnl.nicki.spnego.SpnegoHttpFilter.Constants;

/**
 * Example schemes are "Negotiate" and "Basic". 
 * 
 * <p>See examples and tutorials at 
 * <a href="http://spnego.sourceforge.net" target="_blank">http://spnego.sourceforge.net</a>
 * </p>
 * 
 * @author Darwin V. Felix
 *
 */
final class SpnegoAuthScheme {
    
    /** Zero length byte array. */
    private static final transient byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /** HTTP (Request) "Authorization" Header scheme. */ 
    private final transient String scheme;

    /** HTTP (Request) scheme token. */
    private final transient String token;
    
    /** true if Basic Auth scheme. */
    private final transient boolean basicScheme;
    
    /** true if Negotiate scheme. */
    private final transient boolean negotiateScheme;
    
    /** true if NTLM token. */
    private final transient boolean ntlm;

    /**
     * 
     * @param authScheme 
     * @param authToken 
     */
    SpnegoAuthScheme(final String authScheme, final String authToken) {
        this.scheme = authScheme;
        this.token = authToken;
        
        if (null == authToken || authToken.isEmpty()) {
            this.ntlm = false;
        } else {
            this.ntlm = authToken.startsWith(SpnegoHttpFilter.Constants.NTLM_PROLOG);
        }
        
        this.negotiateScheme = Constants.NEGOTIATE_HEADER.equalsIgnoreCase(authScheme);
        this.basicScheme = Constants.BASIC_HEADER.equalsIgnoreCase(authScheme);
    }
    
    /**
     * Returns true if this SpnegoAuthScheme is of type "Basic".
     * 
     * @return true if Basic Auth scheme
     */
    boolean isBasicScheme() {
        return this.basicScheme;
    }
    
    /**
     * Returns true if this SpnegoAuthScheme is of type "Negotiate".
     * 
     * @return true if Negotiate scheme
     */
    boolean isNegotiateScheme() {
        return this.negotiateScheme;
    }
    /**
     * Returns true if NTLM.
     * 
     * @return true if Servlet Filter received NTLM token
     */
    boolean isNtlmToken() {
        return this.ntlm;
    }

    /**
     * Returns HTTP Authorization scheme.
     * 
     * @return "Negotiate" or "Basic"
     */
    String getScheme() {
        return this.scheme;
    }

    /**
     * Returns a copy of byte[].
     * 
     * @return copy of token
     */
    byte[] getToken() {
        return (null == this.token) ? EMPTY_BYTE_ARRAY : Base64.decodeBase64(this.token);
    }
}
