package org.mgnl.nicki.db.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.ForeignKey;
import org.mgnl.nicki.db.annotation.SubTable;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
import org.mgnl.nicki.db.helper.BeanHelper;
import org.mgnl.nicki.db.helper.Type;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseDBContext
		implements DBContext {
	public final static String TIMESTAMP_ORACLE = "YYYY-MM-DD HH24:MI:SS";
	public final static String TIMESTAMP_FOR_ORACLE = "yyyy-MM-dd HH:mm:ss";
	private static final Logger LOG = LoggerFactory.getLogger(BaseDBContext.class);
	public enum PREPARED {TRUE, FALSE}
	private String name;
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
	
	protected Attribute getSequence(Class<? extends Object> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey() && StringUtils.isNotBlank(attribute.sequence())) {
				return attribute;
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
				this.closeConnection();
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
					if (rs.next()) {
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
					} else {
						return null;
					}					
				}
			}
		} finally {
			if (!inTransaction) {
				this.closeConnection();
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
				this.closeConnection();
			}
		}
	}
	
	@Override
	public <T> long count(T bean, String filter) throws SQLException, InitProfileException  {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			try (Statement stmt = this.connection.createStatement()) {
				String searchStatement = getLoadObjectsSearchStatement(bean, "count(*)", filter, null);
				LOG.debug(searchStatement);
				try (ResultSet rs = stmt.executeQuery(searchStatement)) {
					if (rs != null && rs.next()) {
						return rs.getLong(1);
					}
				}
				return 0;
			}
		} finally {
			if (!inTransaction) {
				this.closeConnection();
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
						java.lang.reflect.Type genericFieldType = field.getGenericType();
						if(genericFieldType instanceof ParameterizedType){
						    ParameterizedType aType = (ParameterizedType) genericFieldType;
						    java.lang.reflect.Type[] fieldArgTypes = aType.getActualTypeArguments();
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
		PrimaryKey primaryKey = new PrimaryKey();
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				String getter = "get" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(getter);
					primaryKey.add(bean.getClass(), attribute.name(), method.invoke(bean));
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
				String columnName = attribute.name();
				String setter = "set" + StringUtils.capitalize(field.getName());
				try {
					Method method = bean.getClass().getMethod(setter, field.getType());
					if (field.getType() == long.class || field.getType() == Long.class) {
						method.invoke(bean, primaryKey.getLong(columnName));
					} else if (field.getType() == int.class || field.getType() == Integer.class) {
						method.invoke(bean, primaryKey.getInt(columnName));
					} else if (field.getType().isAssignableFrom(String.class)) {
						method.invoke(bean, primaryKey.getString(columnName));
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

	public <T> T get(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
		T entry = beanClass.newInstance();
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				try {
					String setter = "set" + StringUtils.capitalize(field.getName());
					Method method = beanClass.getMethod(setter, field.getType());
					if (field.getType() == String.class) {
						String value = rs.getString(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, StringUtils.trim(value));
						}
					} else if (field.getType() == int.class || field.getType() == Integer.class) {
						int value = rs.getInt(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, value);
						}
					} else if (field.getType() == float.class || field.getType() == Float.class) {
						float value = rs.getFloat(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, value);
						}
					} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
						boolean value = rs.getBoolean(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, value);
						}
					} else if (field.getType() == long.class || field.getType() == Long.class) {
						long value = rs.getLong(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, value);
						}
					} else if (field.getType() == Date.class) {
						Timestamp value = rs.getTimestamp(attribute.name());
						if (!rs.wasNull()) {
							method.invoke(entry, value);
						}
					} else if (field.getType() == byte[].class) {
						Blob blob = rs.getBlob(attribute.name());
						if (blob != null) {
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							try {
								IOUtils.copy(blob.getBinaryStream(), out);
								if (!rs.wasNull()) {
									method.invoke(entry, out.toByteArray());
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
		return getLoadObjectsSearchStatement(bean, "*", filter, orderBy);
	}
	
	protected String getLoadObjectsSearchStatement(Object bean, String columns, String filter, String orderBy) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select ").append(columns).append(" from ").append(getQualifiedTableName(bean.getClass()));
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
			} else if (type == float.class || type == Float.class) {
				return Float.toString((float) value);
			} else if (type == boolean.class || type == Boolean.class) {
				return Boolean.toString((Boolean) value);
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
		return getDateValue(date, attribute.type());
	}
	public String getDateValue(Date date, DataType dataType) {
		if (dataType == DataType.TIMESTAMP) {
			return this.toTimestamp(date);
		} else if (dataType == DataType.DATE) {
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
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null) {
					String columnName = foreignKey.columnName();
					String setter = "set" + StringUtils.capitalize(field.getName());
					Method method = bean.getClass().getMethod(setter, field.getType());
					if (Long.class == field.getType() || long.class == field.getType()) {
						method.invoke(bean, primaryKey.getLong(columnName));
					} else if (Integer.class == field.getType() || int.class == field.getType()) {
						method.invoke(bean, primaryKey.getInt(columnName));
					} else if (field.getType().isAssignableFrom(String.class)) {
						method.invoke(bean, primaryKey.getString(columnName));
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
		Attribute sequenceAttribute = getSequence(bean.getClass());
		if (sequenceAttribute != null) {
			try {
				primaryKey = getSequenceNumber(bean.getClass(), sequenceAttribute);
				setPrimaryKey(bean, primaryKey);
			} catch (Exception e) {
				LOG.error("Could not use sequence " + sequenceAttribute.sequence(), e);
			}
		}
		
		if (usePreparedStatement(bean)) {
			String generatedColumns[] = this.getGeneratedKeys(bean);
			if (generatedColumns != null) {
				try (PreparedStatement pstmt = getPreparedInsertStatement(bean, generatedColumns)) {
					pstmt.executeUpdate();
					return getGeneratedKey(pstmt, generatedColumns, bean.getClass());
				}
			} else {
				try (PreparedStatement pstmt = getPreparedInsertStatement(bean)) {
					pstmt.executeUpdate();
					return primaryKey;
				}
			}
			
		} else {	
			try (Statement stmt = this.getConnection().createStatement()) {
				String statement = this.createInsertStatement(bean);
				LOG.debug(statement);
				String generatedColumns[] = this.getGeneratedKeys(bean);
				if (generatedColumns != null) {
					stmt.executeUpdate(statement, generatedColumns);
					return getGeneratedKey(stmt, generatedColumns, getPrimaryKeyType(bean.getClass()));
				} else {
					stmt.executeUpdate(statement);
					return primaryKey;
				}
			}
		}
	}

	private void fillPreparedStatement(PreparedStatement pstmt, Class<?> beanClass, ColumnsAndValues cv, String... columns) throws SQLException {

		List<String> cols = null;
		if (columns != null && columns.length > 0) {
			cols = Arrays.asList(columns);
		}
		
		int pos = 0;

		for (String columnName : cv.getColumnNames()) {
			String beanName = BeanHelper.getFieldName(beanClass, columnName);
			if (cols == null || cols.contains(beanName)) {
				Type type = cv.getType(columnName);
				if (type == Type.STRING) {
					pos++;
					String value = (String) cv.getValue(columnName);
					if (value != null) {
						pstmt.setString(pos, value);
					} else {
						pstmt.setNull(pos, Types.VARCHAR);
					}
				} else if (type == Type.TIMESTAMP) {
					pos++;
					Date value = (Date) cv.getValue(columnName);
					if (value != null) {
						pstmt.setTimestamp(pos, new Timestamp(value.getTime()));
					} else {
						pstmt.setNull(pos, Types.TIMESTAMP);
					}
				} else if (type == Type.DATE) {
					pos++;
					Date value = (Date) cv.getValue(columnName);
					if (value != null) {
						pstmt.setDate(pos, new java.sql.Date(value.getTime()));
					} else {
						pstmt.setNull(pos, Types.DATE);
					}
				} else if (type == Type.LONG) {
					pos++;
					Long value = (Long) cv.getValue(columnName);
					if (value != null) {
						pstmt.setLong(pos, value);
					} else {
						pstmt.setNull(pos, Types.BIGINT);
					}
				} else if (type == Type.INT) {
					pos++;
					Integer value = (Integer) cv.getValue(columnName);
					if (value != null) {
						pstmt.setInt(pos, value);
					} else {
						pstmt.setNull(pos, Types.INTEGER);
					}
				} else if (type == Type.FLOAT) {
					pos++;
					Float value = (Float) cv.getValue(columnName);
					if (value != null) {
						pstmt.setFloat(pos, value);
					} else {
						pstmt.setNull(pos, Types.FLOAT);
					}
				} else if (type == Type.BOOLEAN) {
					pos++;
					Boolean value = (Boolean) cv.getValue(columnName);
					if (value != null) {
						pstmt.setBoolean(pos, value);
					} else {
						pstmt.setNull(pos, Types.BOOLEAN);
					}
				} else if (type == Type.BLOB) {
					pos++;
					byte[] value = (byte[]) cv.getValue(columnName);
					if (value != null) {
						pstmt.setBlob(pos, new ByteArrayInputStream(value));
					} else {
						pstmt.setNull(pos, Types.BLOB);
					}
				}
			}
		}
	}

	protected PreparedStatement getPreparedInsertStatement(Object bean, String... generatedColumns) throws NotSupportedException, SQLException  {
		String tableName = this.getQualifiedTableName(bean.getClass());
		ColumnsAndValues cv = getInsertColumnValues(bean);
		String insertStatementString = getPreparedInsertStatement(PREPARED.TRUE, tableName, cv);
		PreparedStatement pstmt;
		if (generatedColumns != null && generatedColumns.length > 0) {
			pstmt = this.getConnection().prepareStatement(insertStatementString, generatedColumns);
		} else {
			pstmt = this.getConnection().prepareStatement(insertStatementString);
		}
		fillPreparedStatement(pstmt, bean.getClass(), cv);
		return pstmt;
	}

	protected PreparedStatement getPreparedUpdateStatement(Object bean, String where, String... columns) throws NotSupportedException, NothingToDoException, SQLException {
		String tableName = this.getQualifiedTableName(bean.getClass());
		ColumnsAndValues cv = getUpdateColumnValues(bean, columns);
		String whereClause = getWhereClause(bean, where, columns);
		String updateStatementString = getUpdateStatement(PREPARED.TRUE, tableName, cv, whereClause);
		PreparedStatement pstmt = this.connection.prepareStatement(updateStatementString);
		fillPreparedStatement(pstmt, bean.getClass(), cv, columns);
		return pstmt;
	}

	private String getWhereClause(Object bean, String where, String... columns) throws NotSupportedException {

		StringBuilder whereClause = new StringBuilder();
		if (StringUtils.isNotBlank(where)) {
			whereClause.append(where);
		}

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
					} else if (field.getType() == float.class || field.getType() == Float.class) {
						attributeValue = this.getFloatValue(bean, field, attribute);
					} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
						attributeValue = this.getBooleanValue(bean, field, attribute);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					LOG.error("Error converting value", e);
				}
				if (attribute.primaryKey()) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(attribute.name()).append("=").append(attributeValue);
				}
			}
		}
		if (whereClause.length() > 0) {
			return whereClause.toString();
		} else {
			return null;
		}
	}

	private ColumnsAndValues getUpdateColumnValues(Object bean, String... columns) {

		List<String> cols= null;
		if (columns != null && columns.length > 0) {
			cols = Arrays.asList(columns);
		}
		ColumnsAndValues cv = new ColumnsAndValues(bean.getClass());

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (cols == null || cols.contains(field.getName())) {
				if (field.getAnnotation(Attribute.class) != null) {
					Attribute attribute = field.getAnnotation(Attribute.class);
					if (!attribute.primaryKey()) {
						String columnName = attribute.name();
						try {
							if (field.getType() == String.class) {
								cv.add(columnName, getValue(bean, String.class, field, attribute));
							} else if (field.getType() == Date.class) {
								cv.add(columnName, getValue(bean, Date.class, field, attribute));
							} else if (field.getType() == long.class || field.getType() == Long.class) {
								cv.add(columnName, getValue(bean, Long.class, field, attribute));
							} else if (field.getType() == int.class || field.getType() == Integer.class) {
								cv.add(columnName, getValue(bean, Integer.class, field, attribute));
							} else if (field.getType() == float.class || field.getType() == Float.class) {
								cv.add(columnName, getValue(bean, Float.class, field, attribute));
							} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
								cv.add(columnName, getValue(bean, Boolean.class, field, attribute));
							} else if (field.getType() == byte[].class) {
								cv.add(columnName, getValue(bean, byte[].class, field, attribute));
							}
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							LOG.error("Error converting value", e);
						}
					}
				}
			}
		}
		LOG.debug("CV=" + cv);
		return cv;
	}

	protected String getPreparedInsertStatement(PREPARED prepared, String tableName, ColumnsAndValues cv) {
		String result = "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getPreparedValues() + ")";
		LOG.debug(result);
		return result;
	}

	private boolean usePreparedStatement(Object bean) {
		Class<? extends Object> clazz = bean.getClass();
		return clazz.isAnnotationPresent(Table.class) && clazz.getAnnotation(Table.class).usePreparedStatement();
	}

	@Override
	public <T> T update(T bean, String... columns) throws SQLException, InitProfileException, NotSupportedException {
		return updateWhere(bean, null, columns);
	}

	@Override
	public <T> T updateWhere(T bean, String where, String... columns) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			if (usePreparedStatement(bean)) {
				try (PreparedStatement pstmt = getPreparedUpdateStatement(bean, where, columns)) {
					int count = pstmt.executeUpdate();
					if (!inTransaction) {
						try {
							this.commit();
						} catch (NotInTransactionException e) {
							LOG.error("Error on commit", e);
						}
					}
					if (count == 1) {
						return this.reload(bean);
					} else {
						return null;
					}
				} catch (NothingToDoException e1) {
					LOG.error("Nothing to do");
					return null;
				}
			} else {
				try (Statement stmt = this.connection.createStatement()) {
					int count = 0;
					try {
						String statement = this.createUpdateWhereStatement(bean, where, columns);
						LOG.debug(statement);
						count = stmt.executeUpdate(statement);
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
					if (count == 1) {
						return this.reload(bean);
					} else {
						return null;
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
					String statement = null;
					PrimaryKey primaryKey = getPrimaryKey(bean);
					if (primaryKey != null && primaryKey.size() > 0) {
						try {
							@SuppressWarnings("unchecked")
							T deleteBean = (T) bean.getClass().newInstance();
							setPrimaryKey(deleteBean, primaryKey);
							statement = this.createDeleteStatement(deleteBean);
						} catch (InstantiationException | IllegalAccessException e) {
							LOG.error("Eror creating deleteBean", e);
						}
					}
					if (statement == null) {
						statement = this.createDeleteStatement(bean);
					}
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
				this.closeConnection();
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
					if (LOG.isDebugEnabled()) {
						LOG.debug(handler.getSearchStatement());
					}
				}
				try (ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
				}
			}
		} finally {
			if (!inTransaction) {
				this.closeConnection();
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
			closeConnection();
		}
	}
	
	private void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				LOG.error("Error closing connection", e);
			}
			this.connection = null;
		}
		
	}

	private <T> T reload(T bean) {
		List<T> list = null;
		try {
			@SuppressWarnings("unchecked")
			T b = (T) bean.getClass().newInstance();
			list = loadObjects(b, true, getReloadObjectsWhereClause(bean), null);
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
			LOG.error("Error reloading bean", e);
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
		return getInsertStatement(PREPARED.FALSE, this.getQualifiedTableName(bean.getClass()), getInsertColumnValues(bean));
	}

	/**
	 * 
	 * 
	 * @param bean
	 * @return ColumnsAndValues
	 * @throws NotSupportedException
	 */
	protected ColumnsAndValues getInsertColumnValues(Object bean) throws NotSupportedException {

		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		ColumnsAndValues cv = new ColumnsAndValues(bean.getClass());

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (!attribute.autogen()) {
					try {
						if (field.getType() == String.class) {
							String value = getValue(bean, String.class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == Date.class) {
							Date value;
							if (attribute.now()) {
								value = new Date();
							} else {
								value = getValue(bean, Date.class, field, attribute);
							}
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							Long value = getValue(bean, Long.class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == int.class || field.getType() == Integer.class) {
							Integer value = getValue(bean, Integer.class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == float.class || field.getType() == Float.class) {
							Float value = getValue(bean, Float.class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
							Boolean value = getValue(bean, Boolean.class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						} else if (field.getType() == byte[].class) {
							byte[] value = getValue(bean, byte[].class, field, attribute);
							if (value != null) {
								cv.add(attribute.name(), value);
							}
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						LOG.error("Error creating statement", e);
					}
				}
			}
		}
		LOG.debug("CV=" + cv);

		return cv;
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

	@SuppressWarnings("unchecked")
	protected <T> T getValue(Object bean, Class<T> clazz, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException  {
		return (T) this.getValue(bean, field);
	}

	protected String getIntValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Integer) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	protected String getFloatValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Float) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	protected String getBooleanValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Boolean) this.getValue(bean, field)).toString();
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
		return getQualifiedName(table.name());
	}

	@Override
	public String getColumn(Class<? extends Object> clazz, String fieldName) throws NoSuchFieldException {

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

	protected String getInsertStatement(PREPARED prepared, String tableName, ColumnsAndValues cv) {
		return "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getValues(this, prepared) + ")";
	}

	protected String getUpdateStatement(PREPARED prepared, String tableName, ColumnsAndValues cv, String whereClause) throws NothingToDoException {
		if (cv.size() == 0) {
			throw new NothingToDoException();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(tableName).append(" set ");
		
		boolean multi = false;
		for (String columnName : cv.getColumnNames()) {
			if (multi) {
				sb.append(",");
			}
			if (prepared == PREPARED.TRUE) {
				sb.append(columnName).append("=").append("?");
				
			} else {
				sb.append(columnName).append("=").append(cv.getDbString(this, columnName));
			}
			multi = true;
		}

		if (StringUtils.isNotBlank(whereClause)) {
			sb.append(" where ");
			sb.append(whereClause);
		}
		LOG.debug(sb.toString());
		return sb.toString();
	}

	protected String getDeleteStatement(String tableName, String whereClause) {
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

	protected Date getDateValueXXX(Object bean, Field field, Attribute attribute) {
		try {
			Date date;
			if (attribute.now()) {
				date = new Date();
			} else {
				date = (Date) this.getValue(bean, field);
			}
			return date;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.error("Error converting date", e);
		}

		return null;
	}

	protected Calendar getCalendarValue(Object bean, Field field, Attribute attribute) {
		try {
			Date date;
			if (attribute.now()) {
				date = new Date();
			} else {
				date = (Date) this.getValue(bean, field);
			}
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(date);
			  return cal;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.error("Error converting date", e);
		}

		return null;
	}

	@Override
	public String toTimestamp(Date date) {
		return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	@Override
	public String toDate(Date date) {
		return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
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
		return createUpdateWhereStatement(bean, null, columns);
	}

	protected <T> String createUpdateWhereStatement(T bean, String where, String... columns) throws NotSupportedException, NothingToDoException {
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		String whereClause = addPrimaryKeyWhereClause(bean, where);
		
		ColumnsAndValues cv = getUpdateColumnValues(bean, columns);

		return getUpdateStatement(PREPARED.FALSE, 
				this.getQualifiedTableName(bean.getClass()), cv, whereClause);
	}
	
	private String addPrimaryKeyWhereClause(Object bean, String where) throws NotSupportedException {
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		StringBuilder whereClause = new StringBuilder();
		if (StringUtils.isNotBlank(where)) {
			whereClause.append(where);
		}
		
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.primaryKey()) {
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
						} else if (field.getType() == float.class || field.getType() == Float.class) {
							attributeValue = this.getFloatValue(bean, field, attribute);
						} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
							attributeValue = this.getBooleanValue(bean, field, attribute);
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						LOG.error("Error converting value", e);
					}
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(attribute.name()).append("=").append(attributeValue);
				}
			}
		}
		return whereClause.toString();
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
					} else if (field.getType() == float.class || field.getType() == Float.class) {
						attributeValue = this.getFloatValue(bean, field, attribute);
					} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
						attributeValue = this.getBooleanValue(bean, field, attribute);
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
	
	public String getNowPlusHours(int hours) {
		return "SYSDATE " + ((hours >= 0) ? "+":"") + hours + " / 24";
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

	public PrimaryKey getSequenceNumber(Class<?> beanClazz, Attribute sequenceAttribute) throws Exception {

		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(getQualifiedName(sequenceAttribute.sequence()));
		select(handler);
		return new PrimaryKey(beanClazz, sequenceAttribute.name(), handler.getResult());
	}

	public Long getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(getQualifiedName(sequenceName));
		select(handler);
		return handler.getResult();
	}
	
	public String getQualifiedName(String name) {
		if (!StringUtils.contains(name, '.') && this.schema != null) {
			return "\"" + this.schema + "\".\"" + name + "\"";
		} else {
			return "\"" + name + "\"";
		}
	}

	public PrimaryKey getGeneratedKey(Statement stmt, String[] generatedColumns, Class<?> beanClazz) throws SQLException {
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		return new PrimaryKey(beanClazz, generatedColumns, generatedKeys);
	}

	@Override
	public DataSource getDataSource() {
		return new JDBCDataSource(this.name);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getStringAsDbString(String value) {
		return "'" + value + "'";
	}

	@Override
	public String getDateAsDbString(Date value) {
		return toDate(value);
	}

	@Override
	public String getTimestampAsDbString(Date value) {
		return toTimestamp(value);
	}

	@Override
	public String getLongAsDbString(Long value) {
		return Long.toString(value);
	}

	@Override
	public String getIntAsDbString(Integer value) {
		return Integer.toString(value);
	}

	@Override
	public String getFloatAsDbString(Float value) {
		return Float.toString(value);
	}

	@Override
	public String getBooleanAsDbString(Boolean value) {
		return Boolean.toString(value);
	}
}
