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
import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
 
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
 
public final class HelloKDC {
 
    private HelloKDC() {
        // default private
    }
 
    public static void main(final String[] args) throws Exception {
 
    	// Domain (pre-authentication) account
        final String username = "rhirning";
        
        // Password for the pre-auth acct.
        final String password = "XXX";
        
        // Name of our krb5 config file
        final String krbfile = "D:/work/svn/nicki/nicki-core/src/test/resources/krb5.conf";
        
        // Name of our login config file
        final String loginfile = "D:/work/svn/nicki/nicki-core/src/test/resources/login.conf";
        
        // Name of our login module
        final String module = "spnego-client";
 
        // set some system properties
        System.setProperty("java.security.krb5.conf", krbfile);
        System.setProperty("java.security.auth.login.config", loginfile);
        //System.setProperty("sun.security.krb5.debug", true);
 
        // assert 
        HelloKDC.validate(username, password, krbfile, loginfile, module);
 
        final CallbackHandler handler = 
            HelloKDC.getUsernamePasswordHandler(username, password);
 
        final LoginContext loginContext = new LoginContext(module, handler);
 
        // attempt to login
        loginContext.login();
 
        // output some info
        System.out.println("Subject=" + loginContext.getSubject());
 
        // logout
        loginContext.logout();
 
        System.out.println("Connection test successful.");
    }
 
    private static void validate(final String username, final String password
        , final String krbfile, final String loginfile, final String moduleName) 
        throws FileNotFoundException, NoSuchAlgorithmException {
 
        // confirm username was provided
        if (null == username || username.isEmpty()) {
            throw new IllegalArgumentException("Must provide a username");
        }
 
        // confirm password was provided
        if (null == password || password.isEmpty()) {
            throw new IllegalArgumentException("Must provide a password");
        }
 
        // confirm krb5.conf file exists
        if (null == krbfile || krbfile.isEmpty()) {
            throw new IllegalArgumentException("Must provide a krb5 file");
        } else {
            final File file = new File(krbfile);
            if (!file.exists()) {
                throw new FileNotFoundException(krbfile);
            }
        }
 
        // confirm loginfile
        if (null == loginfile || loginfile.isEmpty()) {
            throw new IllegalArgumentException("Must provide a login file");
        } else {
            final File file = new File(loginfile);
            if (!file.exists()) {
                throw new FileNotFoundException(loginfile);
            }
        }
 
        // confirm that runtime loaded the login file
        final Configuration config = Configuration.getConfiguration();
 
        // confirm that the module name exists in the file
        if (null == config.getAppConfigurationEntry(moduleName)) {
            throw new IllegalArgumentException("The module name " 
                    + moduleName + " was not found in the login file");
        }        
    }
 
    private static CallbackHandler getUsernamePasswordHandler(
        final String username, final String password) {
 
        final CallbackHandler handler = new CallbackHandler() {
            public void handle(final Callback[] callback) {
                for (int i=0; i<callback.length; i++) {
                    if (callback[i] instanceof NameCallback) {
                        final NameCallback nameCallback = (NameCallback) callback[i];
                        nameCallback.setName(username);
                    } else if (callback[i] instanceof PasswordCallback) {
                        final PasswordCallback passCallback = (PasswordCallback) callback[i];
                        passCallback.setPassword(password.toCharArray());
                    } else {
                        System.err.println("Unsupported Callback: " 
                                + callback[i].getClass().getName());
                    }
                }
            }
        };
 
        return handler;
    }
}