package org.mgnl.nicki.db.test.base;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.SQLException;

//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.mgnl.nicki.db.test.db.ErrorEntry;


public abstract class TestBase {
	private static DataBaseManager dataBaseManager;	
	
	protected static final boolean DO_EXPIRE_TESTS = true;
	
//	@BeforeAll
	public static void startServers() throws Exception {

		System.setProperty("/META-INF/nicki/env.properties", "/META-INF/nicki/env_test.properties");

		dataBaseManager = new DataBaseManager();
		dataBaseManager.startServer();
		dataBaseManager.createTables();
		dataBaseManager.populateTables();
		verifyTables();
	}


	private static void verifyTables() {
		try(DBContext dbContext = DBContextManager.getContext("test")) {
			assertNotNull(dbContext, "DbContext");
			dbContext.exists(new ErrorEntry());
			
		} catch (SQLException | InitProfileException e) {
			e.printStackTrace();
			assertNull(e, "ContextError error");
		}
	}

//	@AfterAll
	public static void stopServers() throws IOException, InterruptedException   {
		Thread.sleep(3000);
		dataBaseManager.stopServer();
	}
}
