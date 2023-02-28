package org.mgnl.nicki.db.test.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.junit.Test;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.helper.Type;
import org.mgnl.nicki.db.helper.TypedValue;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.mgnl.nicki.db.test.base.DataBaseManager;
import org.mgnl.nicki.db.test.base.TestBase;
import org.mgnl.nicki.db.test.db.ErrorEntry;

public class DatabaseTest extends TestBase {
	
	@Test
	public void runDatabaseTest() throws InvalidPrincipalException, IOException {

		try(DBContext dbContext = DBContextManager.getContext("test")) {
			assertNotNull("DbContext", dbContext);
			DataBaseManager.populateTables();
			dbContext.exists(new ErrorEntry());
			
		} catch (SQLException | InitProfileException | NotSupportedException e) {
			e.printStackTrace();
			assertNotNull("ContextError error", null);
		}
	}
	
	private long countCodeEntries(DBContext dbContext, String code) throws SQLException, InitProfileException {
		ErrorEntry errorEntry = new ErrorEntry();
		errorEntry.setCode(code);
		return dbContext.count(errorEntry, null);
	}
	
	private long countCommandEntries(DBContext dbContext, String command) throws SQLException, InitProfileException {
		ErrorEntry errorEntry = new ErrorEntry();
		errorEntry.setCommand(command);
		return dbContext.count(errorEntry, null);
	}
	
	@Test
	public void runSecondDatabaseTest() throws InvalidPrincipalException, IOException {

		try(DBContext dbContext = DBContextManager.getContext("test")) {
			assertNotNull("DbContext", dbContext);
			dbContext.exists(new ErrorEntry());
			
		} catch (SQLException | InitProfileException e) {
			e.printStackTrace();
			assertNotNull("ContextError error", null);
		}
	}
	
	/**
	 * Insert mit code = "ABC"
	 * update where code = 'ABC'
	 * search mit where clause
	 * 
	 * @throws InvalidPrincipalException
	 * @throws IOException
	 */
	@Test
	public void testUpdateWhereChar() throws InvalidPrincipalException, IOException {

		try(DBContext dbContext = DBContextManager.getContext("test")) {
			
			// count
			assertNotNull("DbContext", dbContext);
			assertEquals("Anzahl Datenbankeinträge mit XXX", 0, countCodeEntries(dbContext, "XXX"));
			create(dbContext, "ABC");
			assertEquals("Anzahl Datenbankeinträge mit ABC", 1, countCodeEntries(dbContext, "ABC"));

			// search with bean
			ErrorEntry errorEntry = new ErrorEntry();
			errorEntry.setCode("ABC");
			List<ErrorEntry> entries = dbContext.loadObjects(errorEntry, false);
			assertNotNull(entries);
			assertEquals("Anzahl gefundener Einträge mit ABC", 1, entries.size());
			
			// search with where clause
			errorEntry = new ErrorEntry();
			entries = dbContext.loadObjects(errorEntry, false, "code='ABC'", null);
			assertNotNull(entries);
			assertEquals("Anzahl gefundener Einträge ABC in where clause", 1, entries.size());
			
			// search with prepared where clause
			errorEntry = new ErrorEntry();
			entries = dbContext.loadObjects(errorEntry, false, "code=?", null, new TypedValue(Type.STRING, 1, "ABC").correctValue(ErrorEntry.class, "code"));
			assertNotNull(entries);
			assertEquals("Anzahl gefundener Einträge ABC als TypedValue", 1, entries.size());

			// search with bean and prepared where clause
			errorEntry = new ErrorEntry();
			errorEntry.setCommand("TEST");
			entries = dbContext.loadObjects(errorEntry, false, "code=?", null, new TypedValue(Type.STRING, 1, "ABC").correctValue(ErrorEntry.class, "code"));
			assertNotNull(entries);
			assertEquals("Anzahl gefundener Einträge mit command TEST", 1, entries.size());
			
			// delete mit bean und primary key
			errorEntry = new ErrorEntry();
			errorEntry.setCommand("TEST");
			errorEntry = dbContext.loadObject(errorEntry, false);
			assertNotNull("ErrorEntry TEST", errorEntry);
			dbContext.delete(errorEntry);
			assertEquals("Anzahl gefundener Einträge mit command TEST", 0, countCommandEntries(dbContext, "TEST"));
			
			// delete mit bean ohne primary key
			create(dbContext, "ABC");
			assertEquals("Anzahl gefundener Einträge mit command TEST", 1, countCommandEntries(dbContext, "TEST"));
			errorEntry = new ErrorEntry();
			errorEntry.setCommand("TEST");
			try {
				dbContext.delete(errorEntry);
				assertTrue("delete darf nicht erfolgt sein", false);
			} catch (NotSupportedException e) {
				assertNotNull("delete mit bean ohne primary key ist nicht erlaubt", e);
			}
			assertEquals("Anzahl gefundener Einträge mit command TEST", 1, countCommandEntries(dbContext, "TEST"));
			
			// update ohne primary key
			errorEntry = new ErrorEntry();
			errorEntry.setCommand("FINISH");
			try {
				dbContext.update(errorEntry);
				assertTrue("update darf nicht erfolgt sein", false);
			} catch (NotSupportedException e) {
				assertNotNull("update mit bean ohne primary key ist nicht erlaubt", e);
			}
			
			// update mit primary key
			errorEntry = new ErrorEntry();
			errorEntry.setCommand("TEST");
			errorEntry = dbContext.loadObject(errorEntry, false);
			assertNotNull("ErrorEntry TEST", errorEntry);
			errorEntry.setCommand("FINISH");
			dbContext.update(errorEntry);
			assertEquals("Anzahl gefundener Einträge mit command FINISH", 1, countCommandEntries(dbContext, "FINISH"));
			
			
		} catch (SQLException | InitProfileException | NotSupportedException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			assertNotNull("DB Error", null);
		}
	}

	
	public void create(DBContext dbContext, String code) throws SQLException, InitProfileException, NotSupportedException  {

		assertNotNull(dbContext);
		ErrorEntry errorEntry = new ErrorEntry();
		errorEntry.setCommand("TEST");
		errorEntry.setUserId("rhirning");
		errorEntry.setCode(code);
		errorEntry.setData("SQL unit testing");
		dbContext.create(errorEntry);
	}
}
