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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.SubTable;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
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
	public <T> PrimaryKey create(T bean) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			PrimaryKey primaryKey = this.createInDB(bean);
			if (this.hasSubs(bean.getClass()) && primaryKey != null) {
				for (Object sub : this.getSubs(bean, primaryKey)) {
					this.create(sub);
				}
			}

			if (!inTransaction) {
				try {
					this.commit();
				} catch (NotInTransactionException e) {
					LOG.error("Error on commit", e);
				}
			}
			return primaryKey;
			//return this.load(bean);
		} finally {
			if (!inTransaction) {
				this.rollback();
			}
		}
	}
	
	protected String getSequence(Class<? extends Object> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				return StringUtils.stripToNull(attribute.sequence());
			}
		}
		return null;
	}
	
	protected Class<?> getPrimaryKeyType(Class<? extends Object> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				return field.getType();
			}
		}
		return null;
	}

	@Override
	public <T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		return loadObjects(bean, deepSearch, null, null);
	}
	
	@Override
	public <T> T loadObject(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		return loadObject(bean, deepSearch, null, null);
	}
	
	@Override
	public <T> boolean exists(T bean) throws SQLException, InitProfileException  {
		return exists(bean, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadObjects(T bean, boolean deepSearch, String filter, String orderBy) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			try {
				throw new NotSupportedException();
			} catch (NotSupportedException e) {
				LOG.error("Missing Table annotation", e);
			}
		}


		
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String searchStatement = getLoadObjectsSearchStatement(bean, filter, orderBy);
				LOG.debug(searchStatement);
				List<T> list = null;
				try (ResultSet rs = stmt.executeQuery(searchStatement)) {
					list = (List<T>) handle(bean.getClass(), rs, table.postInit());
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
				this.rollback();
			}
		}
	}
	
	@Override
	public <T> T loadObject(T bean, boolean deepSearch, String filter, String orderBy) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		Method postMethod = null;
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			try {
				throw new NotSupportedException();
			} catch (NotSupportedException e) {
				LOG.error("Missing Table annotation", e);
			}
		}
		if (StringUtils.isNotBlank(table.postInit())) {
			try {
				postMethod = bean.getClass().getDeclaredMethod(table.postInit());
			} catch (NoSuchMethodException | SecurityException e) {
				LOG.error("Invalid postInitMethod (" + table.postInit() + ") for class " + bean.getClass().getName(), e);
			}
		}
		
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String searchStatement = getLoadObjectsSearchStatement(bean, filter, orderBy);
				LOG.debug(searchStatement);
				try (ResultSet rs = stmt.executeQuery(searchStatement)) {
					rs.next();
					@SuppressWarnings("unchecked")
					T result = (T) get(bean.getClass(), rs);
					if (postMethod != null) {
						try {
							postMethod.invoke(result);
						} catch (Exception e) {
							LOG.error("Unable to execute postInitMethod (" + table.postInit() + ") for class "
									+ result.getClass().getName(), e);
						}
					}

					
					if (result != null && deepSearch){
						addObjects(result, deepSearch);
					}
					return result;
				}
			}
		} finally {
			if (!inTransaction) {
				this.rollback();
			}
		}
	}
	
	@Override
	public <T> boolean exists(T bean, String filter) throws SQLException, InitProfileException  {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String searchStatement = getLoadObjectsSearchStatement(bean, filter, null);
				LOG.debug(searchStatement);
				try (ResultSet rs = stmt.executeQuery(searchStatement)) {
					if (rs != null) {
						boolean hasNext = rs.next();
						if (hasNext) {
							return true;
						}
					}
				}
				return false;
			}
		} finally {
			if (!inTransaction) {
				this.rollback();
			}
		}
	}

	private void addObjects(Object bean, boolean deepSearch) {
		PrimaryKey primaryKey = getPrimaryKey(bean);
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
							addObjects(bean, field, entryClass, primaryKey, deepSearch);
						}
					} else {
						addObject(bean, field, field.getType(), primaryKey, deepSearch);
					}
	
				}
			}
		}
		
	}

	private PrimaryKey getPrimaryKey(Object bean) {
		PrimaryKey primaryKey = null;
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				String getter = "get" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(getter);
					primaryKey = new PrimaryKey(method.invoke(bean));
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error reading primary key ", e);
				}
			}
		}
		return primaryKey;
	}

	private <T> void addObject(Object bean, Field field, Class<T> entryClass, PrimaryKey primaryKey, boolean deepSearch) {
		T subBean = getNewInstance(entryClass);
		setPrimaryKey(subBean, primaryKey);
		try {
			List<T> subs = loadObjects(subBean, deepSearch);
			if (subs != null && subs.size() > 0) {
				String setter = "set" + StringUtils.capitalize(field.getName());
				Method method = bean.getClass().getMethod(setter, entryClass);
				method.invoke(bean, subs.get(0));
			}			
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error adding objects " + field.getName(), e);
		}
	}

	private <T> void addObjects(Object bean, Field field, Class<T> entryClass, PrimaryKey primaryKey, boolean deepSearch) {
		T subBean = getNewInstance(entryClass);
		try {
			setForeignKey(subBean, primaryKey);
			List<T> subs = loadObjects(subBean, deepSearch);
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


	protected void setPrimaryKey(Object bean, PrimaryKey primaryKey) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				String setter = "set" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(setter, field.getType());
					if (Long.class.isAssignableFrom(field.getType())) {
						method.invoke(bean, primaryKey.getLong());
					} else if (Integer.class.isAssignableFrom(field.getType())) {
						method.invoke(bean, primaryKey.getInt());
					} else if (field.getType().isAssignableFrom(String.class)) {
						method.invoke(bean, primaryKey.getValue());
					} 
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error setting primary key ", e);
				}
			}
		}
	}

	private <T> List<T> handle(Class<T> beanClass, ResultSet rs, String postInitMethod) throws SQLException, InstantiationException, IllegalAccessException {

		Method postMethod = null;
		if (StringUtils.isNotBlank(postInitMethod)) {
			try {
				postMethod = beanClass.getDeclaredMethod(postInitMethod);
			} catch (NoSuchMethodException | SecurityException e) {
				LOG.error("Invalid postInitMethod (" + postInitMethod + ") for class " + beanClass.getName(), e);
			}
		}
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			T bean = get(beanClass, rs);
			if (postMethod != null) {
				try {
					postMethod.invoke(bean);
				} catch (Exception e) {
					LOG.error("Unable to execute postInitMethod (" + postInitMethod + ") for class " + beanClass.getName(), e);
				}
			}
			list.add(bean);
		}
		return list;

	}

	private <T> T get(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
		T entry = beanClass.newInstance();
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				try {
					String setter = "set" + StringUtils.capitalize(field.getName());
					Method method = beanClass.getMethod(setter, field.getType());
					if (field.getType() == String.class) {
						method.invoke(entry, StringUtils.trim(rs.getString(attribute.name())));
					} else if (field.getType() == int.class || field.getType() == Integer.class) {
						method.invoke(entry, rs.getInt(attribute.name()));
					} else if (field.getType() == long.class || field.getType() == Long.class) {
						method.invoke(entry, rs.getLong(attribute.name()));
					} else if (field.getType() == Date.class) {
						method.invoke(entry, rs.getTimestamp(attribute.name()));
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					LOG.error("Error handling ResultSet", e);
				}
			}
		}
		return entry;
	}
	
	protected String getLoadObjectsSearchStatement(Object bean, String filter, String orderBy) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(getQualifiedTableName(bean.getClass()));
			int count = 0;
			if (StringUtils.isNotBlank(filter)) {
				sb.append(" where (");
				sb.append(filter);
				sb.append(") ");
				count++;
			}
			for (Field field : bean.getClass().getDeclaredFields()) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null) {
					String getter = "get" + StringUtils.capitalize(field.getName());
					Method method;
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					String value = null;
					if (rawValue != null) {
						value = getStringValue(method.getReturnType(), rawValue, attribute);
					}
					if (value != null) {
						if (count > 0) {
							sb.append(" AND ");
						} else {
							sb.append(" where ");
						}
						count++;
						sb.append(attribute.name()).append("=").append(value);
					}
				}
			}
			if (StringUtils.isNotBlank(orderBy)) {
				sb.append(" order by ").append(orderBy);
			}
			return sb.toString();
		} catch (NotSupportedException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error creating load objects search statement ", e);
			return e.getMessage();
		}
	}
	
	private String getReloadObjectsWhereClause(Object bean) {
		try {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for (Field field : bean.getClass().getDeclaredFields()) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null && attribute.primaryKey()) {
					String getter = "get" + StringUtils.capitalize(field.getName());
					Method method;
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					String value = null;
					if (rawValue != null) {
						value = getStringValue(method.getReturnType(), rawValue, attribute);
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
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Error creating reload objects search statement ", e);
			return e.getMessage();
		}
	}

	protected String getStringValue(Class<?> type, Object value, Attribute attribute) {
		try {
			if (type == String.class) {
				return "'" + (String) value + "'";
			} else if (type == Date.class) {
				return this.getDateValue((Date) value, attribute);
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
	

	@Override
	public String getDateValue(Date date, Attribute attribute) {
		if (attribute.type() == DataType.TIMESTAMP) {
			return this.toTimestamp(date);
		} else if (attribute.type() == DataType.DATE) {
			return this.toDate(date);
		} else {
			return null;
		}
	}

	private Collection<Object> getSubs(Object bean, PrimaryKey primaryKey) {
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
					this.setForeignKey(object, primaryKey);
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOG.error("Error reading subs", e);
			}
		}
		return list;
	}

	protected Collection<Object> getAllSubs(Object bean) {
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
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOG.error("Error reading subs", e);
			}
		}
		return list;
	}

	private Collection<Object> getSubs(Object bean, Field field) {
		Collection<Object> list = new ArrayList<>();
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
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.error("Error reading subs", e);
		}
		return list;
	}

	private void setForeignKey(Object bean, PrimaryKey primaryKey) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.foreignKey()) {
					String setter = "set" + StringUtils.capitalize(field.getName());
					Method method = bean.getClass().getMethod(setter, field.getType());
					if (Long.class.isAssignableFrom(field.getType())) {
						method.invoke(bean, primaryKey.getLong());
					} else if (Integer.class.isAssignableFrom(field.getType())) {
						method.invoke(bean, primaryKey.getInt());
					} else if (field.getType().isAssignableFrom(String.class)) {
						method.invoke(bean, primaryKey.getValue());
					} 
				}
			}
		}
	}

	private boolean hasSubs(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(SubTable.class) != null) {
				return true;
			}
		}
		return false;
	}

	protected <T> PrimaryKey createInDB(T bean) throws SQLException, NotSupportedException {
		PrimaryKey primaryKey = null;
		String sequence = getSequence(bean.getClass());
		if (StringUtils.isNotBlank(sequence)) {
			try {
				primaryKey = getSequenceNumber(sequence);
				setPrimaryKey(bean, primaryKey);
			} catch (Exception e) {
				LOG.error("Could not use sequence " + sequence, e);
			}
		}
		try (Statement stmt = this.getConnection().createStatement()) {
			String statement = this.createInsertStatement(bean);
			LOG.debug(statement);
			String generatedColumns[] = this.getGeneratedKeys(bean);
			if (generatedColumns != null) {
				stmt.executeUpdate(statement, generatedColumns);
				return getGeneratedKey(stmt, getPrimaryKeyType(bean.getClass()));
			} else {
				stmt.executeUpdate(statement);
				return primaryKey;
			}
		}

	}

	@Override
	public <T> T update(T bean, String... columns) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				try {
					String statement = this.createUpdateStatement(bean, columns);
					LOG.debug(statement);
					stmt.executeUpdate(statement);
					if (!inTransaction) {
						try {
							this.commit();
						} catch (NotInTransactionException e) {
							LOG.error("Error on commit", e);
						}
					}
				} catch (NothingToDoException e) {
					LOG.error("Nothing to do");
				}
				return this.reload(bean);
			}
		} finally {
			if (!inTransaction) {
				this.rollback();
			}
		}
	}

	@Override
	public void executeUpdate(String statement) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				LOG.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						LOG.error("Error on commit", e);
					}
				}
			}
		} finally {
			if (!inTransaction) {
				this.rollback();
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
		this.deleteSubs(bean);

		try {
			try (Statement stmt = this.connection.createStatement()) {
				try {
					String statement = this.createDeleteStatement(bean);
					LOG.debug(statement);
					stmt.executeUpdate(statement);
					if (!inTransaction) {
						try {
							this.commit();
						} catch (NotInTransactionException e) {
							LOG.error("Error on commit", e);
						}
					}
				} catch (NotSupportedException e) {
					LOG.error("Delete not supported");
				}
			}
		} finally {
			if (!inTransaction) {
				this.rollback();
			}
		}
	}

	protected void deleteSubs(Object bean) throws SQLException, InitProfileException {
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(SubTable.class) != null) {
				if (this.hasSubs(field.getType())) {
					for (Object sub : this.getSubs(bean, field)) {
						this.delete(sub);
					}					
				}
				this.deleteSubs(bean, field);
			}
		}
	}

	private void deleteSubs(Object bean, Field field) throws SQLException, InitProfileException {
		for (Object sub : this.getSubs(bean, field)) {
			this.delete(sub);
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
					this.commit();
				} catch (NotInTransactionException e) {
					LOG.error("Error committing", e);
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
					LOG.error("Error committing", e);
				}
			}
		}
	}

	@Override
	public Connection beginTransaction() throws SQLException, InitProfileException {
		if (this.connection == null) {
			LOG.debug("profile: " + this.profile);
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
					LOG.error("Error closing connection", e);
				}
				this.connection = null;
			}
		}
	}

	@Override
	public void rollback() throws SQLException {
		if (this.connection == null) {
			// nothing to do
			return;
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
					LOG.error("Error closing connection", e);
				}
				this.connection = null;
			}
		}
	}

	private <T> T reload(T bean) {
		List<T> list = null;
		try {
			@SuppressWarnings("unchecked")
			T b = (T) bean.getClass().newInstance();
			list = loadObjects(b, true, getReloadObjectsWhereClause(bean), null);
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
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
						LOG.error("Error creating statement", e);
					}
				}
			}
		}

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
		if (null != this.getValue(bean, field)) {
			return ((Long) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	protected String getIntValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Integer) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
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

	protected static String getUpdateStatement(String tableName, Map<String, String> columnValues, String whereClause) throws NothingToDoException {
		if (columnValues == null || columnValues.size() == 0) {
			throw new NothingToDoException();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(tableName).append(" set ");
		
		boolean multi = false;
		for (String columnName : columnValues.keySet()) {
			if (multi) {
				sb.append(",");
			}
			sb.append(columnName).append("=").append(columnValues.get(columnName));
			multi = true;
		}

		if (StringUtils.isNotBlank(whereClause)) {
			sb.append(" where ");
			sb.append(whereClause);
		}
		return sb.toString();
	}

	protected static String getDeleteStatement(String tableName, String whereClause) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(whereClause)) {
			sb.append("delete from ").append(tableName);
			sb.append(" where ");
			sb.append(whereClause);
		}
		return sb.toString();
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
			LOG.error("Error converting date", e);
		}

		return null;
	}

	@Override
	public String toTimestamp(Date date) {
		return "to_date('" + timestampOracle.format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	@Override
	public String toDate(Date date) {
		return "to_date('" + timestampOracle.format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	protected String getStringValue(Object bean, Field field) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return "'" + (String) this.getValue(bean, field) + "'";
		} else {
			return null;
		}
	}

	protected Object getValue(Object bean, Field field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String getter = "get" + StringUtils.capitalize(field.getName());
		Method method = bean.getClass().getMethod(getter);
		return method.invoke(bean);
	}

	@Override
	public <T> String createUpdateStatement(T bean, String... columns) throws NotSupportedException, NothingToDoException {
		/**
		 * update SCHEMA.TABLE set a=' ', b=' ', c=' ' where clause;
		 */
		List<String> cols= null;
		if (columns != null && columns.length > 0) {
			cols = Arrays.asList(columns);
		}
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		StringBuilder whereClause = new StringBuilder();
		Map<String, String> columnValues = new HashMap<>();

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				String attributeValue = null;
				try {
					if (field.getType() == String.class) {
						attributeValue = this.getStringValue(bean, field);
					} else if (field.getType() == Date.class) {
						attributeValue = this.getDateValue(bean, field, attribute);
					} else if (field.getType() == long.class || field.getType() == Long.class) {
						attributeValue = this.getLongValue(bean, field, attribute);
					} else if (field.getType() == int.class || field.getType() == Integer.class) {
						attributeValue = this.getIntValue(bean, field, attribute);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error converting value", e);
				}
				if (cols == null | cols.contains(field.getName())) {
					columnValues.put(attribute.name(), attributeValue);
				}
				if (attribute.primaryKey()) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(attribute.name()).append("=").append(attributeValue);
				}
			}
		}

		return getUpdateStatement(this.getQualifiedTableName(bean.getClass()), columnValues, whereClause.toString());
	}

	@Override
	public <T> String createDeleteStatement(T bean) throws NotSupportedException {
		/**
		 * delete from SCHEMA.TABLE where clause;
		 */
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		StringBuilder whereClause = new StringBuilder();

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				String attributeValue = null;
				try {
					if (field.getType() == String.class) {
						attributeValue = this.getStringValue(bean, field);
					} else if (field.getType() == long.class || field.getType() == Long.class) {
						attributeValue = this.getLongValue(bean, field, attribute);
					} else if (field.getType() == int.class || field.getType() == Integer.class) {
						attributeValue = this.getIntValue(bean, field, attribute);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error converting value", e);
				}
				if (StringUtils.isNotBlank(attributeValue)) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(attribute.name()).append("=").append(attributeValue);
				}
			}
		}

		return getDeleteStatement(this.getQualifiedTableName(bean.getClass()), whereClause.toString());
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

	@Override
	public String getTimeStamp() {
		return "SYSDATE";
	}

	@Override
	public String getSysDate() {
		return "SYSDATE";
	}

	@Override
	public void close() throws SQLException {
		synchronized (this) {
			if (this.connection != null) {
				this.rollback();
				this.connection = null;
			}
		}
	}

	@Override
	public PrimaryKey getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(sequenceName);
		select(handler);
		return new PrimaryKey(handler.getResult());
	}

	@Override
	public PrimaryKey getGeneratedKey(Statement stmt, Class<?> clazz) throws SQLException {
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		return new PrimaryKey(clazz, generatedKeys);
	}
}
