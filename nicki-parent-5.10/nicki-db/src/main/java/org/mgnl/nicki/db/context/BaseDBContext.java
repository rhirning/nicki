package org.mgnl.nicki.db.context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDBContext implements DBContext {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDBContext.class);
	private DBProfile profile;
	private Connection connection;
	
	public BaseDBContext() {
	}

	@Override
	public void setProfile(DBProfile profile) {
		this.profile = profile;
	}

	@Override
	public <T> T create(T bean) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			beginTransaction();
		}
		
		try {
			try(Statement stmt = this.connection.createStatement()) {
				String statement = createInsertStatement(bean);
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						commit();
					} catch (NotInTransactionException e) {
						;
					}
				}
				return load(bean);
			}
		} finally {
			if (!inTransaction) {
				try {
					rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public <T> T update(T bean) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			beginTransaction();
		}
		
		try {
			try(Statement stmt = this.connection.createStatement()) {
				String statement = createUpdateStatement(bean);
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						commit();
					} catch (NotInTransactionException e) {
						;
					}
				}
				return load(bean);
			}
		} finally {
			if (!inTransaction) {
				try {
					rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public <T> void delete(T bean) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			beginTransaction();
		}
		
		try {
			try(Statement stmt = this.connection.createStatement()) {
				String statement = createDeleteStatement(bean);
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						commit();
					} catch (NotInTransactionException e) {
						;
					}
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public <T> List<T> select(Class<T> clazz, ListSelectHandler<T> handler) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			beginTransaction();
		}

		try {
			try(Statement stmt = this.connection.createStatement()) {
				if (handler.isLoggingEnabled()) {
					LOG.debug(handler.getSearchStatement());
				}
				try(ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
					return handler.getResults();
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public void select(SelectHandler handler) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			beginTransaction();
		}

		try {
			try(Statement stmt = this.connection.createStatement()) {
				if (handler.isLoggingEnabled()) {
					LOG.debug(handler.getSearchStatement());
				}
				try(ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					commit();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public Connection beginTransaction() throws SQLException, InitProfileException {
		if (this.connection == null) {
			this.connection = profile.getConnection();
		}
		return this.connection;
	}

	@Override
	public void commit() throws NotInTransactionException, SQLException {
		if (this.connection == null) {
			throw new NotInTransactionException();
		}
		try {
			this.connection.commit();
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (this.connection != null) {
				try {
					this.connection.close();
				} catch (SQLException e) {
					;
				}
				this.connection = null;
			}
		}
	}

	@Override
	public void rollback() throws NotInTransactionException, SQLException {
		if (this.connection == null) {
			throw new NotInTransactionException();
		}
		try {
			this.connection.rollback();
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (this.connection != null) {
				try {
					this.connection.close();
				} catch (SQLException e) {
					;
				}
				this.connection = null;
			}
		}
	}

	private<T> T load(T bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String createInsertStatement(T bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String createUpdateStatement(T bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String createDeleteStatement(T bean) {
		// TODO Auto-generated method stub
		return null;
	}

}
