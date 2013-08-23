package org.mgnl.nicki.jcr.firsthop;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Node;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Second hop example. Stores, retrieves, and removes example content.
 */
public class FourthHop {
	private static final Logger LOG = LoggerFactory.getLogger(FourthHop.class);

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
			LOG.debug(node.getPath());
			LOG.debug(node.getProperty("password").getString());

			session.save();
		} finally {
			session.logout();
		}
	}

}