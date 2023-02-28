package org.mgnl.nicki.db.test.base;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.mgnl.nicki.db.test.db.ErrorEntry;


public abstract class TestBase {
	
	
	protected static final boolean DO_EXPIRE_TESTS = true;
	
	@BeforeClass
	public static void startServers() throws Exception {

		System.setProperty("/META-INF/nicki/env.properties", "/META-INF/nicki/env_test.properties");
	
		DataBaseManager.createTables();
		verifyTables();
	}


	private static void verifyTables() {
		try(DBContext dbContext = DBContextManager.getContext("test")) {
			assertNotNull("DbContext", dbContext);
			dbContext.exists(new ErrorEntry());
			
		} catch (SQLException | InitProfileException e) {
			e.printStackTrace();
			assertNotNull("ContextError error", null);
		}
	}

	@AfterClass
	public static void stopServers() throws IOException   {
		DataBaseManager.dropTables();
	}
}
