
package org.mgnl.nicki.jcr.firsthop;

/*-
 * #%L
 * nicki-jcr
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


import javax.jcr.Repository;
import javax.jcr.Session;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * First hop example. Logs in to a content repository and prints a status
 * message.
 */
@Slf4j
public class FirstHop {
	/**
	 * The main entry point of the example application.
	 * 
	 * @param args
	 *            command line arguments (ignored)
	 * @throws Exception
	 *             if an error occurs
	 */
	public static void main(String[] args) throws Exception {
		Repository repository = new TransientRepository();
		Session session = repository.login();
		try {
			String user = session.getUserID();
			String name = repository.getDescriptor(Repository.REP_NAME_DESC);
			log.debug("Logged in as " + user + " to a " + name
					+ " repository.");
		} finally {
			session.logout();
		}
	}

}
