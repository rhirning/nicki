package org.mgnl.nicki.db.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonValue.ValueType;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.SubTable;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDBContext
		implements DBContext {
	public final static String TIMESTAMP_ORACLE = "YYYY-MM-DD HH24:MI:SS";
	public final static String TIMESTAMP_FOR_ORACLE = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat timestampOracle = new SimpleDateFormat(TIMESTAMP_FOR_ORACLE);
	private static final Logger LOG = LoggerFactory.getLogger(BaseDBContext.class);
	private DBProfile profile;
	private Connection connection;

	private String schema;

	public BaseDBContext() {
	}

	@Override
	public void setProfile(DBProfile profile) {
		this.profile = profile;
	}

	@Override
	public <T> T create(T bean) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			Long primaryKey = this._create(bean);
			if (this.hasSubs(bean)) {
				for (Object sub : this.getSubs(bean, primaryKey)) {
					this.create(sub);
				}
			}

			if (!inTransaction) {
				try {
					this.commit();
				} catch (NotInTransactionException e) {
					;
				}
			}
			return this.load(bean);
		} finally {
			if (!inTransaction) {
				try {
					this.rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String searchStatement = getLoadObjectsSearchStatement(bean);
				LOG.debug(searchStatement);
				List<T> list = null;
				try (ResultSet rs = stmt.executeQuery(searchStatement)) {
					list = (List<T>) handle(bean.getClass(), rs);
				}
				if (list != null && deepSearch) {
					for (T t : list) {
						addObjects(t, deepSearch);
					}
				}
				return list;
			}
		} finally {
			if (!inTransaction) {
				try {
					this.rollback();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	private void addObjects(Object bean, boolean deepSearch) {
		Object primaryKey = null;
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				String getter = "get" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(getter);
					primaryKey = method.invoke(bean);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error reading primary key ", e);
				}
			}
		}
		if (primaryKey != null) {
			for (Field field : bean.getClass().getDeclaredFields()) {
				SubTable subTable = field.getAnnotation(SubTable.class);
				if (subTable != null) {
					if (field.getType().isAssignableFrom(List.class)) {
						Type genericFieldType = field.getGenericType();
						if(genericFieldType instanceof ParameterizedType){
						    ParameterizedType aType = (ParameterizedType) genericFieldType;
						    Type[] fieldArgTypes = aType.getActualTypeArguments();
							Class<?> entryClass = (Class<?>) fieldArgTypes[0];
							addObjects(bean, field, entryClass, primaryKey);
						}
					} else {
						addObject(bean, field, field.getType(), primaryKey);
					}
	
				}
			}
		}
		// TODO Auto-generated method stub
		
	}

	private <T> void addObject(Object bean, Field field, Class<T> entryClass, Object primaryKey) {
		T subBean = getNewInstance(entryClass);
		setPrimaryKey(subBean, primaryKey);
		try {
			List<T> subs = loadObjects(subBean, true);
			if (subs != null && subs.size() > 0) {
				String setter = "set" + StringUtils.capitalize(field.getName());
				Method method = bean.getClass().getMethod(setter, entryClass);
				method.invoke(bean, subs.get(0));
			}			
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error adding objects " + field.getName(), e);
		}
	}

	private <T> void addObjects(Object bean, Field field, Class<T> entryClass, Object primaryKey) {
		T subBean = getNewInstance(entryClass);
		setPrimaryKey(subBean, primaryKey);
		try {
			List<T> subs = loadObjects(subBean, true);
			if (subs != null && subs.size() > 0) {
				String setter = "set" + StringUtils.capitalize(field.getName());
				Method method = bean.getClass().getMethod(setter, List.class);
				method.invoke(bean, subs);
			}			
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error adding objects " + field.getName(), e);
		}
	}

	private <T> T getNewInstance(Class<T> clazz) {

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Error creating instance of  " + clazz.getName(), e);
		}
		return null;
	}


	private void setPrimaryKey(Object bean, Object primaryKey) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.foreignKey()) {
				String setter = "set" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(setter, primaryKey.getClass());
					method.invoke(bean, primaryKey);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error reading primary key ", e);
				}
			}
		}
	}

	public <T> List<T> handle(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			T entry = beanClass.newInstance();
			for (Field field : beanClass.getDeclaredFields()) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null) {
					try {
						String setter = "set" + StringUtils.capitalize(field.getName());
						Method method = beanClass.getMethod(setter, field.getType());
						if (field.getType() == String.class) {
							method.invoke(entry, rs.getString(attribute.name()));
						} else if (field.getType() == int.class || field.getType() == Integer.class) {
							method.invoke(entry, rs.getInt(attribute.name()));
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							method.invoke(entry, rs.getLong(attribute.name()));
						} else if (field.getType() == Date.class) {
							method.invoke(entry, rs.getTimestamp(attribute.name()));
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			list.add(entry);
		}
		return list;

	}
	
	protected String getLoadObjectsSearchStatement(Object bean) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(getQualifiedTableName(bean.getClass()));
			sb.append(" where ");
			int count = 0;
			for (Field field : bean.getClass().getDeclaredFields()) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null) {
					String getter = "get" + StringUtils.capitalize(field.getName());
					Method method;
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					String value = null;
					if (rawValue != null) {
						value = getStringValue(method.getReturnType(), rawValue);
					}
					if (value != null) {
						if (count > 0) {
							sb.append(" AND ");
						}
						count++;
						sb.append(attribute.name()).append("=").append(value);
					}
				}
			}
			return sb.toString();
		} catch (NotSupportedException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error creating load objects search statement ", e);
			return e.getMessage();
		}
	}

	protected String getStringValue(Class<?> type, Object value) {
		try {
			if (type == String.class) {
				return "'" + (String) value + "'";
			} else if (type == Date.class) {
				return this.toTimestamp((Date) value);
			} else if (type == long.class || type == Long.class) {
				return Long.toString((long) value);
			} else if (type == int.class || type == Integer.class) {
				return Integer.toString((int) value);
			}
		} catch (SecurityException | IllegalArgumentException e) {
			LOG.error("Error converting", e);
		}
		return null;
	}

	private Collection<Object> getSubs(Object bean, long primaryKey) {
		Collection<Object> list = new ArrayList<>();
		for (Field field : bean.getClass().getDeclaredFields()) {
			try {
				if (field.getAnnotation(SubTable.class) != null) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						String getter = "get" + StringUtils.capitalize(field.getName());
						Method method;
						method = bean.getClass().getMethod(getter);
						@SuppressWarnings("unchecked")
						Collection<Object> fieldList = (Collection<Object>) method.invoke(bean);
						if (fieldList != null && fieldList.size() > 0) {
							list.addAll(fieldList);
						}
					} else {
						String getter = "get" + StringUtils.capitalize(field.getName());
						Method method = bean.getClass().getMethod(getter);
						Object fieldEntry = method.invoke(bean);
						if (fieldEntry != null) {
							list.add(fieldEntry);
						}
					}
				}
				for (Object object : list) {
					this.setPrimaryKey(object, primaryKey);
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	private void setPrimaryKey(Object bean, long primaryKey) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.foreignKey()) {
					String setter = "set" + StringUtils.capitalize(field.getName());
					Method method = bean.getClass().getMethod(setter, field.getType());
					method.invoke(bean, primaryKey);
				}
			}
		}
	}

	private boolean hasSubs(Object bean) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(SubTable.class) != null) {
				return true;
			}
		}
		return false;
	}

	protected <T> Long _create(T bean) throws SQLException, NotSupportedException {
		try (Statement stmt = this.connection.createStatement()) {
			String statement = this.createInsertStatement(bean);
			LOG.debug(statement);
			stmt.executeUpdate(statement, Statement.RETURN_GENERATED_KEYS);
			ResultSet generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys != null && generatedKeys.next()) {
				return new Long(generatedKeys.getLong(1));
			} else {
				return null;
			}
		}

	}

	@Override
	public <T> T update(T bean) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String statement = this.createUpdateStatement(bean);
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						;
					}
				}
				return this.load(bean);
			}
		} finally {
			if (!inTransaction) {
				try {
					this.rollback();
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
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String statement = this.createDeleteStatement(bean);
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						;
					}
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					this.rollback();
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
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				if (handler.isLoggingEnabled()) {
					LOG.debug(handler.getSearchStatement());
				}
				try (ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
					return handler.getResults();
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					this.rollback();
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
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				if (handler.isLoggingEnabled()) {
					LOG.debug(handler.getSearchStatement());
				}
				try (ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
				}
			}
		} finally {
			if (!inTransaction) {
				try {
					this.commit();
				} catch (NotInTransactionException e) {
					;
				}
			}
		}
	}

	@Override
	public Connection beginTransaction() throws SQLException, InitProfileException {
		if (this.connection == null) {
			this.connection = this.profile.getConnection();
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

	private <T> T load(T bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String createInsertStatement(T bean) throws NotSupportedException {
		/**
		 * insert into SCHEMA.TABLE (a,b,c) values (" ", " ", 2);
		 */

		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		Map<String, String> columnValues = new HashMap<>();

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (!attribute.autogen()) {
					try {
						if (field.getType() == String.class) {
							columnValues.put(attribute.name(), this.getStringValue(bean, field));
						} else if (field.getType() == Date.class) {
							columnValues.put(attribute.name(), this.getDateValue(bean, field, attribute));
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							columnValues.put(attribute.name(), this.getLongValue(bean, field, attribute));
						} else if (field.getType() == int.class || field.getType() == Integer.class) {
							columnValues.put(attribute.name(), this.getIntValue(bean, field, attribute));
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// TODO Auto-generated method stub
		return getInsertStatement(this.getQualifiedTableName(bean.getClass()), columnValues);
	}

	protected String[] getGeneratedKeys(Object bean) {
		List<String> keys = new ArrayList<>();

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.autogen()) {
					keys.add(attribute.name());
				}
			}
		}
		if (keys.size() > 0) {
			return keys.toArray(new String[] {});
		} else {
			return null;
		}
	}

	protected String getLongValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return ((Long) this.getValue(bean, field)).toString();
	}

	protected String getIntValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return ((Integer) this.getValue(bean, field)).toString();
	}

	@Override
	public String getQualifiedTableName(Class<? extends Object> clazz) throws NotSupportedException {

		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}
		if (this.schema != null) {
			return this.schema + "." + table.name();
		} else {
			return table.name();
		}
	}

	@Override
	public Object getColumn(Class<? extends Object> clazz, String fieldName) throws NoSuchFieldException {

		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				return attribute.name();
			} else {
				return null;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new NoSuchFieldException(fieldName);
		}

	}

	protected static String getInsertStatement(String tableName, Map<String, String> columnValues) {
		ColumnsAndValues cv = new ColumnsAndValues("", "");
		for (String columnName : columnValues.keySet()) {
			cv.add(columnName, columnValues.get(columnName));
		}

		return "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getValues() + ")";
	}

	protected String getDateValue(Object bean, Field field, Attribute attribute) {
		try {
			Date date;
			if (attribute.now()) {
				date = new Date();
			} else {
				date = (Date) this.getValue(bean, field);
			}
			return this.toTimestamp(date);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return null;
	}

	protected String toTimestamp(Date date) {
		return "to_date('" + timestampOracle.format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	protected String getStringValue(Object bean, Field field) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return "'" + (String) this.getValue(bean, field) + "'";
	}

	protected Object getValue(Object bean, Field field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String getter = "get" + StringUtils.capitalize(field.getName());
		Method method = bean.getClass().getMethod(getter);
		return method.invoke(bean);
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

	public String getSchema() {
		return this.schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Connection getConnection() {
		return this.connection;
	}
}
