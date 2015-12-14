package org.mgnl.nicki.db.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;

public interface DBContext {
	<T> T create(T bean) throws SQLException, InitProfileException, NotSupportedException;

	<T> T update(T bean) throws NotSupportedException, SQLException, InitProfileException;
	
	<T> void delete(T bean) throws SQLException, InitProfileException;

	<T> List<T> select(Class<T> clazz, ListSelectHandler<T> handler) throws SQLException, InitProfileException;
	
	void select(SelectHandler handler) throws SQLException, InitProfileException;
	
	Connection beginTransaction() throws SQLException, InitProfileException;
	
	void commit() throws NotInTransactionException, SQLException;
	
	void rollback() throws  NotInTransactionException, SQLException;

	<T> String createInsertStatement(T bean) throws NotSupportedException;

	<T> String createUpdateStatement(T bean);

	<T> String createDeleteStatement(T bean);

	void setProfile(DBProfile profile);

	void setSchema(String schema);
	
	 String getQualifiedTableName(Class<? extends Object> clazz) throws NotSupportedException;

	Object getColumn(Class<? extends Object> clazz, String string) throws NoSuchFieldException;
	
}
