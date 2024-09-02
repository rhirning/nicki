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

//import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.server.Server;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.mgnl.nicki.db.test.db.ErrorEntry;

public class DataBaseManager {
/*	
	private Server server;

	public static void  main(String[] args) throws Exception {
		System.setProperty("/META-INF/nicki/env.properties", "/META-INF/nicki/env_test.properties");
		DataBaseManager dataBaseManager = new DataBaseManager();
		dataBaseManager.startServer();
		dataBaseManager.createTables();
		dataBaseManager.populateTables();
		DBContext dbContext = DBContextManager.getContext("test");
		System.out.println(dbContext.count(new ErrorEntry(), null));
		dataBaseManager.stopServer();
	}

	public void startServer() {

		server = new Server();
		server.setDatabaseName(0, "TEST");
		server.setDatabasePath(0, "mem:TEST");
//		server.setPort(19001);
		server.setSilent(true);
		server.start();

	}

	public void stopServer() {
		if (server != null) {
			server.stop();
		}

	}

	public void populateTables() throws IOException, SQLException, InitProfileException, NotSupportedException {

		DBContext dbContext = DBContextManager.getContext("test");
		assertNotNull(dbContext);
		ErrorEntry errorEntry = new ErrorEntry();
		errorEntry.setCommand("TEST1");
		errorEntry.setUserId("rhirning");
		errorEntry.setCode("10");
		errorEntry.setData("SQL unit testing");
		dbContext.create(errorEntry);
		errorEntry.setCommand("TEST1");
		errorEntry.setData("DB unit testing");
		dbContext.create(errorEntry);
	}

	public void createTables() throws Exception {
		DBContext dbContext = DBContextManager.getContext("test");
		assertNotNull(dbContext);
		executeSQLs(dbContext,
//				"/db/drop.sql",
				"/db/create_ERROR.sql");
	}

	public  void dropTables() throws IOException   {
		DBContext dbContext = DBContextManager.getContext("test");
		assertNotNull(dbContext);
		executeSQLs(dbContext, "/db/drop.sql");
	}


	private void executeSQLs(DBContext dbContext, String... sqls) throws IOException  {
		for (String sql : sqls) {
			InputStream inputStream = DataBaseManager.class.getResourceAsStream(sql);
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					sb.append(line).append("\n");
				} else {
					if (sb.length() > 0) {
						try {
							//System.out.println("SQL: " + sb.toString());
							dbContext.executeUpdate(sb.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						sb.setLength(0);
					}
				}
				
			}
			if (sb.length() > 0) {
				try {
					//System.out.println("SQL: " + sb.toString());
					dbContext.executeUpdate(sb.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
*/
}
