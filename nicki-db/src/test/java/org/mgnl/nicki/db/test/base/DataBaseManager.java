package org.mgnl.nicki.db.test.base;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.derby.drda.NetworkServerControl;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.mgnl.nicki.db.test.db.ErrorEntry;

public class DataBaseManager {

	public static void  main(String[] args) throws Exception {
		System.setProperty("/META-INF/nicki/env.properties", "/META-INF/nicki/env_test.properties");
		createTables();
		populateTables();
		DBContext dbContext = DBContextManager.getContext("test");
		System.out.println(dbContext.count(new ErrorEntry(), null));
		DataBaseManager.dropTables();
	}
	
	public static void populateTables() throws IOException, SQLException, InitProfileException, NotSupportedException {

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

	public static void createTables() throws Exception {
		NetworkServerControl server = new NetworkServerControl
				(InetAddress.getByName("localhost"),1527);
			server.start(null);
		DBContext dbContext = DBContextManager.getContext("test");
		assertNotNull(dbContext);
		executeSQLs(dbContext,
//				"/db/drop.sql",
				"/db/create_ERROR.sql");
	}

	public static void dropTables() throws IOException   {
		DBContext dbContext = DBContextManager.getContext("test");
		assertNotNull(dbContext);
		executeSQLs(dbContext, "/db/drop.sql");
	}


	private static void executeSQLs(DBContext dbContext, String... sqls) throws IOException  {
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

}
