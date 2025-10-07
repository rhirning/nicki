package org.mgnl.nicki.db.test.tests;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2025 Ralf Hirning
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

import org.mgnl.nicki.db.test.base.TestBase;

/**
 * The Class DatabaseTest.
 */
public class DatabaseTest extends TestBase {
	/*
	 * @Test public void runDatabaseTest() throws InvalidPrincipalException,
	 * IOException, SQLException, InitProfileException, NotSupportedException {
	 * 
	 * try(DBContext dbContext = DBContextManager.getContext("test")) {
	 * assertNotNull(dbContext, "DbContext"); dbContext.exists(new ErrorEntry());
	 * 
	 * } }
	 * 
	 * private long countCodeEntries(DBContext dbContext, String code) throws
	 * SQLException, InitProfileException { ErrorEntry errorEntry = new
	 * ErrorEntry(); errorEntry.setCode(code); return dbContext.count(errorEntry,
	 * null); }
	 * 
	 * private long countCommandEntries(DBContext dbContext, String command) throws
	 * SQLException, InitProfileException { ErrorEntry errorEntry = new
	 * ErrorEntry(); errorEntry.setCommand(command); return
	 * dbContext.count(errorEntry, null); }
	 * 
	 * @Test public void runSecondDatabaseTest() throws InvalidPrincipalException,
	 * IOException, SQLException, InitProfileException {
	 * 
	 * try(DBContext dbContext = DBContextManager.getContext("test")) {
	 * assertNotNull(dbContext, "DbContext"); dbContext.exists(new ErrorEntry()); }
	 * }
	 */
	/**
	 * Insert mit code = "ABC" update where code = 'ABC' search mit where clause
	 *
	 * @throws InvalidPrincipalException
	 * @throws IOException
	 * @throws InitProfileException
	 * @throws SQLException
	 * @throws NotSupportedException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	/*
	 * @Test public void testUpdateWhereChar() throws InvalidPrincipalException,
	 * IOException, SQLException, InitProfileException, NotSupportedException,
	 * InstantiationException, IllegalAccessException {
	 * 
	 * try(DBContext dbContext = DBContextManager.getContext("test")) {
	 * 
	 * // count assertNotNull(dbContext, "DbContext"); assertEquals(0,
	 * countCodeEntries(dbContext, "XXX"), "Anzahl Datenbankeinträge mit XXX");
	 * create(dbContext, "ABC"); assertEquals(1, countCodeEntries(dbContext, "ABC"),
	 * "Anzahl Datenbankeinträge mit ABC");
	 * 
	 * // search with bean ErrorEntry errorEntry = new ErrorEntry();
	 * errorEntry.setCode("ABC"); List<ErrorEntry> entries =
	 * dbContext.loadObjects(errorEntry, false); assertNotNull(entries);
	 * assertEquals(1, entries.size(), "Anzahl gefundener Einträge mit ABC");
	 * 
	 * // search with where clause errorEntry = new ErrorEntry(); entries =
	 * dbContext.loadObjects(errorEntry, false, "code='ABC'", null);
	 * assertNotNull(entries); assertEquals(1, entries.size(),
	 * "Anzahl gefundener Einträge ABC in where clause");
	 * 
	 * // search with prepared where clause errorEntry = new ErrorEntry(); entries =
	 * dbContext.loadObjects(errorEntry, false, "code=?", null, new
	 * TypedValue(Type.STRING, 1, "ABC").correctValue(ErrorEntry.class, "code"));
	 * assertNotNull(entries); assertEquals(1, entries.size(),
	 * "Anzahl gefundener Einträge ABC als TypedValue");
	 * 
	 * // search with bean and prepared where clause errorEntry = new ErrorEntry();
	 * errorEntry.setCommand("TEST"); entries = dbContext.loadObjects(errorEntry,
	 * false, "code=?", null, new TypedValue(Type.STRING, 1,
	 * "ABC").correctValue(ErrorEntry.class, "code")); assertNotNull(entries);
	 * assertEquals(1, entries.size(),
	 * "Anzahl gefundener Einträge mit command TEST");
	 * 
	 * // delete mit bean und primary key errorEntry = new ErrorEntry();
	 * errorEntry.setCommand("TEST"); errorEntry = dbContext.loadObject(errorEntry,
	 * false); assertNotNull(errorEntry, "ErrorEntry TEST");
	 * dbContext.delete(errorEntry); assertEquals(0, countCommandEntries(dbContext,
	 * "TEST"), "Anzahl gefundener Einträge mit command TEST");
	 * 
	 * // delete mit bean ohne primary key create(dbContext, "ABC"); assertEquals(1,
	 * countCommandEntries(dbContext, "TEST"),
	 * "Anzahl gefundener Einträge mit command TEST"); errorEntry = new
	 * ErrorEntry(); errorEntry.setCommand("TEST");
	 * 
	 * try { dbContext.delete(errorEntry); assertTrue(false,
	 * "delete darf nicht erfolgt sein"); } catch (NotSupportedException e) {
	 * assertNotNull(e, "delete mit bean ohne primary key ist nicht erlaubt"); }
	 * assertEquals(1, countCommandEntries(dbContext, "TEST"),
	 * "Anzahl gefundener Einträge mit command TEST");
	 * 
	 * // update ohne primary key errorEntry = new ErrorEntry();
	 * errorEntry.setCommand("FINISH"); try { dbContext.update(errorEntry);
	 * assertTrue(false, "update darf nicht erfolgt sein"); } catch
	 * (NotSupportedException e) { assertNotNull(e,
	 * "update mit bean ohne primary key ist nicht erlaubt"); }
	 * 
	 * // update mit primary key errorEntry = new ErrorEntry();
	 * errorEntry.setCommand("TEST"); errorEntry = dbContext.loadObject(errorEntry,
	 * false); assertNotNull(errorEntry, "ErrorEntry TEST");
	 * errorEntry.setCommand("FINISH"); dbContext.update(errorEntry);
	 * assertEquals(1, countCommandEntries(dbContext, "FINISH"),
	 * "Anzahl gefundener Einträge mit command FINISH");
	 * 
	 * // exists ohne Filter mit blankem bean errorEntry = new ErrorEntry();
	 * assertTrue(dbContext.exists(errorEntry),
	 * "exists ohne Filter mit blankem bean");
	 * 
	 * // exists ohne Filter mit bean und passendem Eintrag errorEntry = new
	 * ErrorEntry(); errorEntry.setCommand("FINISH");
	 * assertTrue(dbContext.exists(errorEntry),
	 * "exists ohne Filter mit bean und passendem Eintrag");
	 * 
	 * // exists ohne Filter mit bean und nicht passendem Eintrag errorEntry = new
	 * ErrorEntry(); errorEntry.setCommand("TEST");
	 * assertTrue(!dbContext.exists(errorEntry),
	 * "exists ohne Filter mit bean und nicht passendem Eintrag");
	 * 
	 * // exists ohne Filter errorEntry = new ErrorEntry();
	 * assertTrue(dbContext.exists(errorEntry), "Exist one filter"); // exists mit
	 * Filter, der passt errorEntry = new ErrorEntry();
	 * assertTrue(dbContext.exists(errorEntry, "COMMAND='FINISH'"),
	 * "exists mit Filter, der passt"); // exists mit Filter, der nicht passt
	 * errorEntry = new ErrorEntry(); assertTrue(!dbContext.exists(errorEntry,
	 * "COMMAND='TEST'"), "exists mit Filter, der nicht passt");
	 * 
	 * // exists mit Filter, der passt und typedValues errorEntry = new
	 * ErrorEntry(); assertTrue(dbContext.exists(errorEntry, "COMMAND=?", new
	 * TypedValue(Type.STRING, 1, "FINISH")), "exists mit Filter, der passt"); //
	 * exists mit Filter, der nicht passt und typedValues errorEntry = new
	 * ErrorEntry(); assertTrue(!dbContext.exists(errorEntry, "COMMAND=?", new
	 * TypedValue(Type.STRING, 1, "TEST")), "exists mit Filter, der nicht passt");
	 * 
	 * } }
	 * 
	 * 
	 * public void create(DBContext dbContext, String code) throws SQLException,
	 * InitProfileException, NotSupportedException {
	 * 
	 * assertNotNull(dbContext); ErrorEntry errorEntry = new ErrorEntry();
	 * errorEntry.setCommand("TEST"); errorEntry.setUserId("rhirning");
	 * errorEntry.setCode(code); errorEntry.setData("SQL unit testing");
	 * dbContext.create(errorEntry); }
	 */
}
