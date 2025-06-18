
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


import java.util.List;


/**
 * User information from the user-store.
 * 
 * @author Darwin V. Felix
 *
 */
public interface UserInfo {

    /**
     * Returns a list of info associated with the label.
     * 
     * @param label e.g. name, proxyAddresses, whenCreated
     * @return a list of info associated with the label 
     */
    List<String> getInfo(final String label);
    
    /**
     * Return a list of labels.
     * 
     * @return a list of labels
     */
    List<String> getLabels();
    
    /**
     * Returns true if there is info with the passed-in label. 
     *  
     * @param label e.g. mail, memberOf, displayName
     * @return true true if there is info with the passed-in label
     */
    boolean hasInfo(final String label);
}
