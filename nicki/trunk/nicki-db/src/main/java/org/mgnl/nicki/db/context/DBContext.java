package org.mgnl.nicki.db.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;

public interface DBContext extends AutoCloseable {
	<T> void create(T bean) throws SQLException, InitProfileException, NotSupportedException;

	<T> T update(T bean, String... columns) throws NotSupportedException, SQLException, InitProfileException;
	
	<T> void delete(T bean) throws SQLException, InitProfileException;

	<T> List<T> select(Class<T> clazz, ListSelectHandler<T> handler) throws SQLException, InitProfileException;
	
	void select(SelectHandler handler) throws SQLException, InitProfileException;
	
	Connection beginTransaction() throws SQLException, InitProfileException;
	
	void commit() throws NotInTransactionException, SQLException;
	
	void rollback() throws  NotInTransactionException, SQLException;

	<T> String createInsertStatement(T bean) throws NotSupportedException;

	<T> String createUpdateStatement(T bean, String... columns) throws NotSupportedException, NothingToDoException;

	<T> String createDeleteStatement(T bean) throws NotSupportedException;

	void setProfile(DBProfile profile);

	void setSchema(String schema);
	String getSchema();
	
	String getQualifiedTableName(Class<? extends Object> clazz) throws NotSupportedException;

	Object getColumn(Class<? extends Object> clazz, String string) throws NoSuchFieldException;

	<T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	<T> List<T> loadObjects(T bean, boolean deepSearch, String filter, String orderBy)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	<T> boolean exists(T bean) throws SQLException, InitProfileException;

	<T> boolean exists(T bean, String filter) throws SQLException, InitProfileException;
	
	String toTimestamp(Date date);

	void executeUpdate(String statement) throws SQLException, InitProfileException, NotSupportedException;

	String getSysDate();

	String getTimeStamp();

	<T> T loadObject(T searchOrder, boolean deepSearch)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;

	<T> T loadObject(T bean, boolean deepSearch, String filter, String orderBy)
			throws SQLException, InitProfileException, InstantiationException, IllegalAccessException;
	
	PrimaryKey getSequenceNumber(String sequenceName) throws Exception;

	String getDateValue(Date date, Attribute attribute);

	String toDate(Date date);

	PrimaryKey getGeneratedKey(Statement stmt, Class<?> clazz) throws SQLException;

}
