
package org.mgnl.nicki.db.context;

/*-
 * #%L
 * nicki-db
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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.helper.TypedValue;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;

public interface DBContext extends AutoCloseable {
	<T> PrimaryKey create(T bean) throws SQLException, InitProfileException, NotSupportedException;

	<T> T update(T bean, String... columns) throws NotSupportedException, SQLException, InitProfileException;
	
	<T> T updateWhere(T bean, String whereClause, String... columns) throws NotSupportedException, SQLException, InitProfileException;
	
	<T> void delete(T bean) throws SQLException, InitProfileException, NotSupportedException;

	<T> List<T> select(Class<T> clazz, ListSelectHandler<T> handler) throws SQLException, InitProfileException;
	
	void select(SelectHandler handler) throws SQLException, InitProfileException;
	
	Connection beginTransaction() throws SQLException, InitProfileException;
	
	void commit() throws NotInTransactionException, SQLException;
	
	void rollback() throws  NotInTransactionException, SQLException;

	void close() throws SQLException;
	
	<T> String createInsertStatement(T bean) throws NotSupportedException;

	<T> String createUpdateStatement(T bean, String... columns) throws NotSupportedException, NothingToDoException;

	<T> String createDeleteStatement(T bean) throws NotSupportedException;

	void setProfile(DBProfile profile);

	void setSchema(String schema);
	String getSchema();
	
	String getQualifiedName(String name);
	String getQualifiedTableName(Class<? extends Object> clazz) throws NotSupportedException;

	String getColumn(Class<? extends Object> clazz, String string) throws NoSuchFieldException;

	<T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	<T> boolean exists(T bean) throws SQLException, InitProfileException;

	<T> boolean exists(T bean, String filter) throws SQLException, InitProfileException;
	
	<T> long count(T bean, String filter) throws SQLException, InitProfileException;
	
	String toTimestamp(Date date);

	void executeUpdate(String statement) throws SQLException, InitProfileException, NotSupportedException;

	String getSysDate();
	
	String getNowPlusHours(int hours);

	String getTimeStamp();

	<T> T loadObject(T bean, boolean deepSearch)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	<T> T loadObject(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedFilterValues)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;
	
	String getDateValue(Date date, Attribute attribute);

	String toDate(Date date);
	String toTime(Date date);
	
	DataSource getDataSource();

	void setName(String name);

	String getStringAsDbString(String value);
	String getDateAsDbString(Date value);
	String getTimeAsDbString(Date value);
	String getTimestampAsDbString(Date value);
	String getLongAsDbString(Long value);
	String getIntAsDbString(Integer value);
	String getFloatAsDbString(Float value);
	String getBooleanAsDbString(Boolean value);
	
	PrimaryKey getSequenceNumber(Class<?> beanClazz, Attribute sequenceAttribute) throws Exception;
	Long getSequenceNumber(String sequenceName) throws Exception;
	<T> T get(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException;

	<T> List<T> loadObjects(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedValues)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;


}
