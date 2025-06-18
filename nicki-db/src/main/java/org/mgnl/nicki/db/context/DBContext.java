
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


/**
 * The Interface DBContext.
 */
public interface DBContext extends AutoCloseable {
	
	/**
	 * Creates the.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the primary key
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	<T> PrimaryKey create(T bean) throws SQLException, InitProfileException, NotSupportedException;

	/**
	 * Update.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param columns the columns
	 * @return the t
	 * @throws NotSupportedException the not supported exception
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> T update(T bean, String... columns) throws NotSupportedException, SQLException, InitProfileException;
	
	/**
	 * Update where.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param whereClause the where clause
	 * @param columns the columns
	 * @return the t
	 * @throws NotSupportedException the not supported exception
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> T updateWhere(T bean, String whereClause, String... columns) throws NotSupportedException, SQLException, InitProfileException;
	
	/**
	 * Delete.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	<T> void delete(T bean) throws SQLException, InitProfileException, NotSupportedException;

	/**
	 * Select.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param handler the handler
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> List<T> select(Class<T> clazz, ListSelectHandler<T> handler) throws SQLException, InitProfileException;
	
	/**
	 * Select.
	 *
	 * @param handler the handler
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	void select(SelectHandler handler) throws SQLException, InitProfileException;
	
	/**
	 * Begin transaction.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	Connection beginTransaction() throws SQLException, InitProfileException;
	
	/**
	 * Commit.
	 *
	 * @throws NotInTransactionException the not in transaction exception
	 * @throws SQLException the SQL exception
	 */
	void commit() throws NotInTransactionException, SQLException;
	
	/**
	 * Rollback.
	 *
	 * @throws NotInTransactionException the not in transaction exception
	 * @throws SQLException the SQL exception
	 */
	void rollback() throws  NotInTransactionException, SQLException;

	/**
	 * Close.
	 *
	 * @throws SQLException the SQL exception
	 */
	void close() throws SQLException;
	
	/**
	 * Creates the insert statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 */
	<T> String createInsertStatement(T bean) throws NotSupportedException;

	/**
	 * Creates the update statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param columns the columns
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 * @throws NothingToDoException the nothing to do exception
	 */
	<T> String createUpdateStatement(T bean, String... columns) throws NotSupportedException, NothingToDoException;

	/**
	 * Creates the delete statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 */
	<T> String createDeleteStatement(T bean) throws NotSupportedException;

	/**
	 * Sets the profile.
	 *
	 * @param profile the new profile
	 */
	void setProfile(DBProfile profile);

	/**
	 * Sets the schema.
	 *
	 * @param schema the new schema
	 */
	void setSchema(String schema);
	
	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	String getSchema();
	
	/**
	 * Gets the qualified name.
	 *
	 * @param name the name
	 * @return the qualified name
	 */
	String getQualifiedName(String name);
	
	/**
	 * Gets the qualified table name.
	 *
	 * @param clazz the clazz
	 * @return the qualified table name
	 * @throws NotSupportedException the not supported exception
	 */
	String getQualifiedTableName(Class<? extends Object> clazz) throws NotSupportedException;

	/**
	 * Gets the column.
	 *
	 * @param clazz the clazz
	 * @param string the string
	 * @return the column
	 * @throws NoSuchFieldException the no such field exception
	 */
	String getColumn(Class<? extends Object> clazz, String string) throws NoSuchFieldException;

	/**
	 * Load objects.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param deepSearch the deep search
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	<T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	/**
	 * Exists.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> boolean exists(T bean) throws SQLException, InitProfileException;

	/**
	 * Exists.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param filter the filter
	 * @param typedFilterValues the typed filter values
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> boolean exists(T bean, String filter, TypedValue... typedFilterValues) throws SQLException, InitProfileException;
	
	/**
	 * Count.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param filter the filter
	 * @return the long
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	<T> long count(T bean, String filter) throws SQLException, InitProfileException;
	
	/**
	 * To timestamp.
	 *
	 * @param date the date
	 * @return the string
	 */
	String toTimestamp(Date date);

	/**
	 * Execute update.
	 *
	 * @param statement the statement
	 * @param typedFilterValues the typed filter values
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	void executeUpdate(String statement, TypedValue... typedFilterValues) throws SQLException, InitProfileException, NotSupportedException;

	/**
	 * Gets the sys date.
	 *
	 * @return the sys date
	 */
	String getSysDate();
	
	/**
	 * Gets the now plus hours.
	 *
	 * @param hours the hours
	 * @return the now plus hours
	 */
	String getNowPlusHours(int hours);

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	String getTimeStamp();

	/**
	 * Load object.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param deepSearch the deep search
	 * @return the t
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	<T> T loadObject(T bean, boolean deepSearch)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	/**
	 * Load object.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param deepSearch the deep search
	 * @param filter the filter
	 * @param orderBy the order by
	 * @param typedFilterValues the typed filter values
	 * @return the t
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	<T> T loadObject(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedFilterValues)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;
	
	/**
	 * Gets the date value.
	 *
	 * @param date the date
	 * @param attribute the attribute
	 * @return the date value
	 */
	String getDateValue(Date date, Attribute attribute);

	/**
	 * To date.
	 *
	 * @param date the date
	 * @return the string
	 */
	String toDate(Date date);
	
	/**
	 * To time.
	 *
	 * @param date the date
	 * @return the string
	 */
	String toTime(Date date);
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	DataSource getDataSource();

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	void setName(String name);

	/**
	 * Gets the string as db string.
	 *
	 * @param value the value
	 * @return the string as db string
	 */
	String getStringAsDbString(String value);
	
	/**
	 * Gets the date as db string.
	 *
	 * @param value the value
	 * @return the date as db string
	 */
	String getDateAsDbString(Date value);
	
	/**
	 * Gets the time as db string.
	 *
	 * @param value the value
	 * @return the time as db string
	 */
	String getTimeAsDbString(Date value);
	
	/**
	 * Gets the timestamp as db string.
	 *
	 * @param value the value
	 * @return the timestamp as db string
	 */
	String getTimestampAsDbString(Date value);
	
	/**
	 * Gets the long as db string.
	 *
	 * @param value the value
	 * @return the long as db string
	 */
	String getLongAsDbString(Long value);
	
	/**
	 * Gets the int as db string.
	 *
	 * @param value the value
	 * @return the int as db string
	 */
	String getIntAsDbString(Integer value);
	
	/**
	 * Gets the float as db string.
	 *
	 * @param value the value
	 * @return the float as db string
	 */
	String getFloatAsDbString(Float value);
	
	/**
	 * Gets the boolean as db string.
	 *
	 * @param value the value
	 * @return the boolean as db string
	 */
	String getBooleanAsDbString(Boolean value);
	
	/**
	 * Gets the sequence number.
	 *
	 * @param beanClazz the bean clazz
	 * @param sequenceAttribute the sequence attribute
	 * @return the sequence number
	 * @throws Exception the exception
	 */
	PrimaryKey getSequenceNumber(Class<?> beanClazz, Attribute sequenceAttribute) throws Exception;
	
	/**
	 * Gets the sequence number.
	 *
	 * @param sequenceName the sequence name
	 * @return the sequence number
	 * @throws Exception the exception
	 */
	Long getSequenceNumber(String sequenceName) throws Exception;
	
	/**
	 * Gets the.
	 *
	 * @param <T> the generic type
	 * @param beanClass the bean class
	 * @param rs the rs
	 * @return the t
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	<T> T get(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException;

	/**
	 * Load objects.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param deepSearch the deep search
	 * @param filter the filter
	 * @param orderBy the order by
	 * @param typedValues the typed values
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	<T> List<T> loadObjects(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedValues)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;


}
