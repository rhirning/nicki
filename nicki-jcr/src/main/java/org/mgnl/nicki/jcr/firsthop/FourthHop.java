
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
import javax.jcr.SimpleCredentials;
import javax.jcr.Node;
import org.apache.jackrabbit.core.TransientRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Second hop example. Stores, retrieves, and removes example content.
 */
@Slf4j
public class FourthHop {

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
		Session session = repository.login(new SimpleCredentials("username",
				"password".toCharArray()));
		try {
			Node root = session.getRootNode();

			// Store content
			Node users = root.addNode("users");
			Node admin = users.addNode("admin");
			admin.setProperty("password", "keines");
			session.save();

			// Retrieve content
			Node node = root.getNode("users/admin");
			log.debug(node.getPath());
			log.debug(node.getProperty("password").getString());

			session.save();
		} finally {
			session.logout();
		}
	}

}
