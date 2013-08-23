package org.mgnl.nicki.jcr.firsthop;

import javax.jcr.Repository;
import javax.jcr.Session;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * First hop example. Logs in to a content repository and prints a status
 * message.
 */
public class FirstHop {
	private static final Logger LOG = LoggerFactory.getLogger(FirstHop.class);

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
			LOG.debug("Logged in as " + user + " to a " + name
					+ " repository.");
		} finally {
			session.logout();
		}
	}

}