package org.mgnl.nicki.db.context;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.ForeignKey;
import org.mgnl.nicki.db.annotation.SubTable;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;
import org.mgnl.nicki.db.handler.ListSelectHandler;
import org.mgnl.nicki.db.handler.PreparedStatementSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
import org.mgnl.nicki.db.helper.BasicDBHelper;
import org.mgnl.nicki.db.helper.BeanHelper;
import org.mgnl.nicki.db.helper.Type;
import org.mgnl.nicki.db.helper.TypedValue;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseDBContext.
 */
@Slf4j
public class BaseDBContext
		implements DBContext {
	
	/** The valid types. */
	public Class<?> VALID_TYPES[] = {String.class,
			Date.class,
			long.class, Long.class,
			int.class, Integer.class,
			float.class, Float.class,
			boolean.class, Boolean.class,
			byte[].class
	};
	
	/** The Constant TIMESTAMP_ORACLE. */
	public final static String TIMESTAMP_ORACLE = "YYYY-MM-DD HH24:MI:SS";
	
	/** The Constant TIMESTAMP_FOR_ORACLE. */
	public final static String TIMESTAMP_FOR_ORACLE = "yyyy-MM-dd HH:mm:ss";
	
	/** The Constant TIME_ORACLE. */
	public final static String TIME_ORACLE = "HH24.MI.SS";
	
	/** The Constant TIME_FOR_ORACLE. */
	public final static String TIME_FOR_ORACLE = "HH.mm.ss";
	
	/**
	 * The Enum PREPARED.
	 */
	public enum PREPARED {
/** The true. */
TRUE, 
 /** The false. */
 FALSE}
	
	/** The name. */
	private String name;
	
	/** The profile. */
	private DBProfile profile;
	
	/** The connection. */
	private Connection connection;

	/** The schema. */
	private String schema;

	/** The allow prepared where. */
	private boolean allowPreparedWhere = BasicDBHelper.isAllowPreparedWhere(this);

	/**
	 * Checks if is trim strings.
	 *
	 * @param beanClass the bean class
	 * @param field the field
	 * @return true, if is trim strings
	 */
	protected boolean isTrimStrings(Class<?> beanClass, Field field) {
		return BasicDBHelper.isTrimStrings(getClass(), beanClass, field);
	}
	
	/**
	 * Sets the profile.
	 *
	 * @param profile the new profile
	 */
	@Override
	public void setProfile(DBProfile profile) {
		this.profile = profile;
	}

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
					log.error("Error on commit", e);
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
	
	/**
	 * Gets the sequence.
	 *
	 * @param clazz the clazz
	 * @return the sequence
	 */
	protected Attribute getSequence(Class<? extends Object> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey() && StringUtils.isNotBlank(attribute.sequence())) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * Gets the primary key type.
	 *
	 * @param clazz the clazz
	 * @return the primary key type
	 */
	protected Class<?> getPrimaryKeyType(Class<? extends Object> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				return field.getType();
			}
		}
		return null;
	}

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
	@Override
	public <T> List<T> loadObjects(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		return loadObjects(bean, deepSearch, null, null);
	}
	
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
	@Override
	public <T> T loadObject(T bean, boolean deepSearch) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		return loadObject(bean, deepSearch, null, null);
	}
	
	/**
	 * Exists.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	@Override
	public <T> boolean exists(T bean) throws SQLException, InitProfileException  {
		return exists(bean, null);
	}
	
	/**
	 * Load objects.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param deepSearch the deep search
	 * @param filter the filter
	 * @param orderBy the order by
	 * @param typedFilterValues the typed filter values
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadObjects(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedFilterValues) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			try {
				throw new NotSupportedException();
			} catch (NotSupportedException e) {
				log.error("Missing Table annotation", e);
			}
		}


		
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		if (usePreparedWhereStatement(bean)) {
			List<T> list = null;
			try (PreparedStatement pstmt = getPreparedSelectStatement(bean, filter, orderBy, typedFilterValues); ResultSet rs = pstmt.executeQuery()) {
				list = (List<T>) handle(bean.getClass(), rs, table.postInit());
				if (list != null && deepSearch) {
					for (T t : list) {
						addObjects(t, deepSearch);
					}
				}
				return list;
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		} else {
			String searchStatement = getLoadObjectsSearchStatement(bean, filter, orderBy);
			log.debug(searchStatement);
			List<T> list = null;
			try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery(searchStatement)) {
				list = (List<T>) handle(bean.getClass(), rs, table.postInit());
				if (list != null && deepSearch) {
					for (T t : list) {
						addObjects(t, deepSearch);
					}
				}
				return list;
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		}
	}

	/**
	 * Gets the prepared select statement.
	 *
	 * @param bean the bean
	 * @return the prepared select statement
	 * @throws SQLException the SQL exception
	 */
	protected PreparedStatement getPreparedSelectStatement(Object bean) throws SQLException   {
		String selectStatementString = getPreparedSelectStatement("*", bean);
		PreparedStatement pstmt = this.getConnection().prepareStatement(selectStatementString);
		fillPreparedStatement(pstmt, bean);
		return pstmt;
	}

	/**
	 * Fill prepared statement.
	 *
	 * @param pstmt the pstmt
	 * @param bean the bean
	 */
	private void fillPreparedStatement(PreparedStatement pstmt, Object bean) {
		int pos = 0;
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				String getter = "get" + StringUtils.capitalize(field.getName());
				Method method;
				try {
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					if (rawValue != null) {
						pos++;
						Type type = BeanHelper.getTypeOfField(bean.getClass(), field.getName());
						if (type == Type.STRING && attribute.length() > 0) {
							rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
						}
						type.fillPreparedStatement(pstmt, pos, rawValue);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | SQLException e) {
					log.error("Error reading value of " + field.getName() + " in class " + bean.getClass(), e);
				}
			}
		}
	}

	/**
	 * Gets the prepared select statement.
	 *
	 * @param bean the bean
	 * @param filter the filter
	 * @param orderBy the order by
	 * @param typedFilterValues the typed filter values
	 * @return the prepared select statement
	 * @throws SQLException the SQL exception
	 */
	protected PreparedStatement getPreparedSelectStatement(Object bean, String filter, String orderBy, TypedValue... typedFilterValues) throws SQLException  {
		List<TypedValue> typedValues = new ArrayList<TypedValue>();
		if (typedFilterValues != null && typedFilterValues.length > 0) {
			for (TypedValue typedValue : typedFilterValues) {
				typedValues.add(typedValue);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getQualifiedTableName(bean.getClass()));
		int count = 0;
		if (StringUtils.isNotBlank(filter)) {
			sb.append(" where ");
			sb.append(filter);
			count++;
		}
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				Type type = BeanHelper.getTypeOfField(bean.getClass(), field.getName());
				String getter = "get" + StringUtils.capitalize(field.getName());
				Method method;
				try {
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					if (rawValue != null) {
						if (count > 0) {
							sb.append(" AND ");
						} else {
							sb.append(" where ");
						}
						count++;
						if (isTrimStrings(bean.getClass(), field) && method.getReturnType() == String.class) {
							sb.append("trim(").append(attribute.name()).append(")=").append(ColumnsAndValues.PREP_VALUE);
						} else {
							sb.append(attribute.name()).append("=").append(ColumnsAndValues.PREP_VALUE);
						}
						typedValues.add(new TypedValue(type, typedValues.size() + 1, rawValue).correctValue(field));
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					log.error("Error reading value of " + field.getName() + " in class " + bean.getClass(), e);
				}
			}
		}
		log.debug(sb.toString());
		PreparedStatement pstmt = this.getConnection().prepareStatement(sb.toString());
		fillPreparedStatement(pstmt, typedValues);

		return pstmt;
	}

	/**
	 * Gets the prepared select statement.
	 *
	 * @param columns the columns
	 * @param bean the bean
	 * @return the prepared select statement
	 */
	protected String getPreparedSelectStatement(String columns, Object bean)  {
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(columns).append(" from ").append(getQualifiedTableName(bean.getClass()));
		int count = 0;
		for (Field field : bean.getClass().getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				String getter = "get" + StringUtils.capitalize(field.getName());
				Method method;
				try {
					method = bean.getClass().getMethod(getter);
					Object rawValue = method.invoke(bean);
					if (rawValue != null) {
						if (count > 0) {
							sb.append(" AND ");
						} else {
							sb.append(" where ");
						}
						count++;
						if (isTrimStrings(bean.getClass(), field) && method.getReturnType() == String.class) {
							sb.append("trim(").append(attribute.name()).append(")=").append(ColumnsAndValues.PREP_VALUE);
						} else {
							sb.append(attribute.name()).append("=").append(ColumnsAndValues.PREP_VALUE);
						}
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					log.error("Error reading value of " + field.getName() + " in class " + bean.getClass(), e);
				}
			}
		}
		log.debug(sb.toString());
		return sb.toString();
	}
	
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
	@Override
	public <T> T loadObject(T bean, boolean deepSearch, String filter, String orderBy, TypedValue... typedFilterValues) throws SQLException, InitProfileException, InstantiationException, IllegalAccessException {
		Method postMethod = null;
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			try {
				throw new NotSupportedException();
			} catch (NotSupportedException e) {
				log.error("Missing Table annotation", e);
			}
		}
		if (StringUtils.isNotBlank(table.postInit())) {
			try {
				postMethod = bean.getClass().getDeclaredMethod(table.postInit());
			} catch (NoSuchMethodException | SecurityException e) {
				log.error("Invalid postInitMethod (" + table.postInit() + ") for class " + bean.getClass().getName(), e);
			}
		}
		
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}
		
		if (usePreparedWhereStatement(bean)) {			
			try (PreparedStatement pstmt = getPreparedSelectStatement(bean, filter, orderBy, typedFilterValues); ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					@SuppressWarnings("unchecked")
					T result = (T) get(bean.getClass(), rs);
					if (postMethod != null) {
						try {
							postMethod.invoke(result);
						} catch (Exception e) {
							log.error("Unable to execute postInitMethod (" + table.postInit() + ") for class "
									+ result.getClass().getName(), e);
						}
					}

					if (result != null && deepSearch) {
						addObjects(result, deepSearch);
					}
					return result;
				} else {
					return null;
				}
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		} else {
			String searchStatement = getLoadObjectsSearchStatement(bean, filter, orderBy);
			log.debug(searchStatement);
			try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery(searchStatement)) {
				if (rs.next()) {
					@SuppressWarnings("unchecked")
					T result = (T) get(bean.getClass(), rs);
					if (postMethod != null) {
						try {
							postMethod.invoke(result);
						} catch (Exception e) {
							log.error("Unable to execute postInitMethod (" + table.postInit() + ") for class "
									+ result.getClass().getName(), e);
						}
					}

					if (result != null && deepSearch) {
						addObjects(result, deepSearch);
					}
					return result;
				} else {
					return null;
				}
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		}
	}
	
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
	@Override
	public <T> boolean exists(T bean, String filter, TypedValue... typedFilterValues) throws SQLException, InitProfileException  {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		if (usePreparedWhereStatement(bean)) {
			try (PreparedStatement pstmt = getPreparedSelectStatement(bean, filter, null, typedFilterValues); ResultSet rs = pstmt.executeQuery()) {
				if (rs != null) {
					boolean hasNext = rs.next();
					if (hasNext) {
						return true;
					}
				}
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
			return false;
		} else {
			String searchStatement = getLoadObjectsSearchStatement(bean, filter, null);
			log.debug(searchStatement);
			try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery(searchStatement)) {
				if (rs != null) {
					boolean hasNext = rs.next();
					if (hasNext) {
						return true;
					}
				}
				return false;
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		}
	}
	
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
	@Override
	public <T> long count(T bean, String filter) throws SQLException, InitProfileException  {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		try {
			String searchStatement = getLoadObjectsSearchStatement(bean, "count(*)", filter, null);
			log.debug(searchStatement);
			try (Statement stmt = this.connection.createStatement(); ResultSet rs = stmt.executeQuery(searchStatement)) {
				if (rs != null && rs.next()) {
					return rs.getLong(1);
				}
				return 0;
			}
		} finally {
			if (!inTransaction) {
				this.closeConnection();
			}
		}
	}

	/**
	 * Adds the objects.
	 *
	 * @param bean the bean
	 * @param deepSearch the deep search
	 */
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

	/**
	 * Gets the primary key.
	 *
	 * @param bean the bean
	 * @return the primary key
	 */
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
					log.error("Error reading primary key ", e);
				}
			}
		}
		return primaryKey;
	}

	/**
	 * Adds the object.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param field the field
	 * @param entryClass the entry class
	 * @param primaryKey the primary key
	 * @param deepSearch the deep search
	 */
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
			log.error("Error adding objects " + field.getName(), e);
		}
	}

	/**
	 * Adds the objects.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param field the field
	 * @param entryClass the entry class
	 * @param primaryKey the primary key
	 * @param deepSearch the deep search
	 */
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
			log.error("Error adding objects " + field.getName(), e);
		}
	}

	/**
	 * Gets the new instance.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @return the new instance
	 */
	private <T> T getNewInstance(Class<T> clazz) {

		try {
			return Classes.newInstance(clazz);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Error creating instance of  " + clazz.getName(), e);
		}
		return null;
	}


	/**
	 * Sets the primary key.
	 *
	 * @param bean the bean
	 * @param primaryKey the primary key
	 */
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
					log.error("Error setting primary key ", e);
				}
			}
		}
	}

	/**
	 * Handle.
	 *
	 * @param <T> the generic type
	 * @param beanClass the bean class
	 * @param rs the rs
	 * @param postInitMethod the post init method
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private <T> List<T> handle(Class<T> beanClass, ResultSet rs, String postInitMethod) throws SQLException, InstantiationException, IllegalAccessException {

		Method postMethod = null;
		if (StringUtils.isNotBlank(postInitMethod)) {
			try {
				postMethod = beanClass.getDeclaredMethod(postInitMethod);
			} catch (NoSuchMethodException | SecurityException e) {
				log.error("Invalid postInitMethod (" + postInitMethod + ") for class " + beanClass.getName(), e);
			}
		}
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			T bean = get(beanClass, rs);
			if (postMethod != null) {
				try {
					postMethod.invoke(bean);
				} catch (Exception e) {
					log.error("Unable to execute postInitMethod (" + postInitMethod + ") for class " + beanClass.getName(), e);
				}
			}
			list.add(bean);
		}
		return list;

	}

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
	public <T> T get(Class<T> beanClass, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
		T entry = Classes.newInstance(beanClass);
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
					log.error("Error handling ResultSet", e);
				}
			}
		}
		return entry;
	}
	
	/**
	 * Gets the load objects search statement.
	 *
	 * @param bean the bean
	 * @param filter the filter
	 * @param orderBy the order by
	 * @return the load objects search statement
	 */
	protected String getLoadObjectsSearchStatement(Object bean, String filter, String orderBy) {
		return getLoadObjectsSearchStatement(bean, "*", filter, orderBy);
	}
	
	/**
	 * Gets the load objects search statement.
	 *
	 * @param bean the bean
	 * @param columns the columns
	 * @param filter the filter
	 * @param orderBy the order by
	 * @return the load objects search statement
	 */
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
					String condition = null;
					if (rawValue != null) {
						value = getStringValue(method.getReturnType(), rawValue, attribute);
						if (isTrimStrings(bean.getClass(), field) && method.getReturnType() == String.class) {
							condition = "trim(" + attribute.name() + ")=" + value;
						}
					}
					if (value != null) {
						if (count > 0) {
							sb.append(" AND ");
						} else {
							sb.append(" where ");
						}
						count++;
						if (condition != null) {
							sb.append(condition);
						} else {
							sb.append(attribute.name()).append("=").append(value);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(orderBy)) {
				sb.append(" order by ").append(orderBy);
			}
			return sb.toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("Error creating load objects search statement ", e);
			return e.getMessage();
		}
	}

	/**
	 * Gets the string value.
	 *
	 * @param type the type
	 * @param value the value
	 * @param attribute the attribute
	 * @return the string value
	 */
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
			log.error("Error converting", e);
		}
		return null;
	}
	

	/**
	 * Gets the date value.
	 *
	 * @param date the date
	 * @param attribute the attribute
	 * @return the date value
	 */
	@Override
	public String getDateValue(Date date, Attribute attribute) {
		return getDateValue(date, attribute.type());
	}
	
	/**
	 * Gets the date value.
	 *
	 * @param date the date
	 * @param dataType the data type
	 * @return the date value
	 */
	public String getDateValue(Date date, DataType dataType) {
		if (dataType == DataType.TIMESTAMP) {
			return this.toTimestamp(date);
		} else if (dataType == DataType.DATE) {
			return this.toDate(date);
		} else {
			return null;
		}
	}

	/**
	 * Gets the subs.
	 *
	 * @param bean the bean
	 * @param primaryKey the primary key
	 * @return the subs
	 */
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
				log.error("Error reading subs", e);
			}
		}
		return list;
	}

	/**
	 * Gets the all subs.
	 *
	 * @param bean the bean
	 * @return the all subs
	 */
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
				log.error("Error reading subs", e);
			}
		}
		return list;
	}

	/**
	 * Gets the subs.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @return the subs
	 */
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
			log.error("Error reading subs", e);
		}
		return list;
	}

	/**
	 * Sets the foreign key.
	 *
	 * @param bean the bean
	 * @param primaryKey the primary key
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
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

	/**
	 * Checks for subs.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	private boolean hasSubs(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(SubTable.class) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates the in DB.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the primary key
	 * @throws SQLException the SQL exception
	 * @throws NotSupportedException the not supported exception
	 */
	protected <T> PrimaryKey createInDB(T bean) throws SQLException, NotSupportedException {
		PrimaryKey primaryKey = null;
		Attribute sequenceAttribute = getSequence(bean.getClass());
		if (sequenceAttribute != null) {
			try {
				primaryKey = getSequenceNumber(bean.getClass(), sequenceAttribute);
				setPrimaryKey(bean, primaryKey);
			} catch (Exception e) {
				log.error("Could not use sequence " + sequenceAttribute.sequence(), e);
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
				log.debug(statement);
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

	/**
	 * Fill prepared statement.
	 *
	 * @param pstmt the pstmt
	 * @param typedValues the typed values
	 * @throws SQLException the SQL exception
	 */
	private void fillPreparedStatement(PreparedStatement pstmt, List<TypedValue> typedValues) throws SQLException {
		for (TypedValue typedValue : typedValues) {
			typedValue.fillPreparedStatement(pstmt);
		}
	}

	/**
	 * Fill prepared statement.
	 *
	 * @param pstmt the pstmt
	 * @param typedValues the typed values
	 * @throws SQLException the SQL exception
	 */
	private void fillPreparedStatement(PreparedStatement pstmt, TypedValue[] typedValues) throws SQLException {
		for (TypedValue typedValue : typedValues) {
			typedValue.fillPreparedStatement(pstmt);
		}
	}

	/**
	 * Fill prepared statement.
	 *
	 * @param pstmt the pstmt
	 * @param beanClass the bean class
	 * @param cv the cv
	 * @param columns the columns
	 * @throws SQLException the SQL exception
	 */
	private void fillPreparedStatement(PreparedStatement pstmt, Class<?> beanClass, ColumnsAndValues cv, String... columns) throws SQLException {

		List<String> cols = null;
		if (columns != null && columns.length > 0) {
			cols = Arrays.asList(columns);
		}
		
		int pos = 0;

		for (String columnName : cv.getColumnNames()) {
			String beanName = BeanHelper.getFieldName(beanClass, columnName);
			if (cols == null || cols.contains(beanName)) {
				pos++;
				Type type = cv.getType(columnName);
				type.fillPreparedStatement(pstmt, pos, cv.getValue(columnName));
			}
		}
	}

	/**
	 * Gets the prepared insert statement.
	 *
	 * @param bean the bean
	 * @param generatedColumns the generated columns
	 * @return the prepared insert statement
	 * @throws NotSupportedException the not supported exception
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Gets the prepared update statement.
	 *
	 * @param bean the bean
	 * @param where the where
	 * @param columns the columns
	 * @return the prepared update statement
	 * @throws NotSupportedException the not supported exception
	 * @throws NothingToDoException the nothing to do exception
	 * @throws SQLException the SQL exception
	 */
	protected PreparedStatement getPreparedUpdateStatement(Object bean, String where, String... columns) throws NotSupportedException, NothingToDoException, SQLException {
		String tableName = this.getQualifiedTableName(bean.getClass());
		List<TypedValue> typedValues = new ArrayList<TypedValue>();
		ColumnsAndValues cv = getUpdateColumnValues(bean, typedValues, columns);
		String whereClause = getPrimaryKeyWhereClause(bean, typedValues, where);
		if (StringUtils.isBlank(whereClause)) {
			throw new NotSupportedException("Missing primary key value");
		}
		String updateStatementString = getUpdateStatement(PREPARED.TRUE, tableName, cv, whereClause);
		PreparedStatement pstmt = this.connection.prepareStatement(updateStatementString);
		fillPreparedStatement(pstmt, typedValues);
		return pstmt;
	}

	/**
	 * Gets the prepared delete statement.
	 *
	 * @param bean the bean
	 * @return the prepared delete statement
	 * @throws NotSupportedException the not supported exception
	 * @throws SQLException the SQL exception
	 */
	protected PreparedStatement getPreparedDeleteStatement(Object bean) throws NotSupportedException, SQLException {
		String tableName = this.getQualifiedTableName(bean.getClass());
		List<TypedValue> typedValues = new ArrayList<TypedValue>();
		String whereClause = getPrimaryKeyWhereClause(bean, typedValues, null);
		if (StringUtils.isBlank(whereClause)) {
			throw new NotSupportedException("Missing primary key value");
		}
		String deleteStatementString = getDeleteStatement(tableName, whereClause);
		PreparedStatement pstmt = this.connection.prepareStatement(deleteStatementString);
		fillPreparedStatement(pstmt, typedValues);
		return pstmt;
	}

	/**
	 * Gets the primary key where clause.
	 *
	 * @param bean the bean
	 * @param typedValues the typed values
	 * @param where the where
	 * @return the primary key where clause
	 * @throws NotSupportedException the not supported exception
	 */
	private String getPrimaryKeyWhereClause(Object bean, List<TypedValue> typedValues, String where) throws NotSupportedException {

		int pos = typedValues.size();
		StringBuilder whereClause = new StringBuilder();
		if (StringUtils.isNotBlank(where)) {
			whereClause.append(where);
		}

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Attribute.class) != null) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				Type type = BeanHelper.getTypeOfField(field);
				if (attribute.primaryKey()) {
					String condition = null;
					String attributeValue = null;
					try {
						if (field.getType() == String.class) {
							attributeValue = this.getStringValue(bean, field);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)).correctValue(field));
							} else if (isTrimStrings(bean.getClass(), field)) {
								condition = "trim(" + attribute.name() + ")=" + attributeValue;
							}
						} else if (field.getType() == Date.class) {
							attributeValue = this.getDateValue(bean, field, attribute);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)));
							}
						} else if (field.getType() == long.class || field.getType() == Long.class) {
							attributeValue = this.getLongValue(bean, field, attribute);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)));
							}
						} else if (field.getType() == int.class || field.getType() == Integer.class) {
							attributeValue = this.getIntValue(bean, field, attribute);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)));
							}
						} else if (field.getType() == float.class || field.getType() == Float.class) {
							attributeValue = this.getFloatValue(bean, field, attribute);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)));
							}
						} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
							attributeValue = this.getBooleanValue(bean, field, attribute);
							if (usePreparedWhereStatement(bean)) {
								typedValues.add(new TypedValue(type, ++pos, getValue(bean, type.getTypeClass(), field, attribute)));
							}
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						log.error("Error converting value", e);
					}
					if (StringUtils.isNotBlank(attributeValue)) {
						if (whereClause.length() > 0) {
							whereClause.append(" AND ");
						}
						if (usePreparedWhereStatement(bean)) {
							whereClause.append(attribute.name()).append("=").append(ColumnsAndValues.PREP_VALUE);
						} else if (condition != null) {
							whereClause.append(condition);
						} else {
							whereClause.append(attribute.name()).append("=").append(attributeValue);
						}
					}
				}
			}
		}
		if (whereClause.length() > 0) {
			return whereClause.toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the update column values.
	 *
	 * @param bean the bean
	 * @param typedValues the typed values
	 * @param columns the columns
	 * @return the update column values
	 */
	private ColumnsAndValues getUpdateColumnValues(Object bean, List<TypedValue> typedValues, String... columns) {
		int pos = 0;
		List<String> cols= null;
		if (columns != null && columns.length > 0) {
			cols = Arrays.asList(columns);
		}
		ColumnsAndValues cv = new ColumnsAndValues(bean.getClass());

		for (Field field : bean.getClass().getDeclaredFields()) {
			if (cols == null || cols.contains(field.getName())) {
				if (field.getAnnotation(Attribute.class) != null) {
					Attribute attribute = field.getAnnotation(Attribute.class);
					Type type = BeanHelper.getTypeOfField(field);
					if (!attribute.primaryKey()) {
						String columnName = attribute.name();
						try {
							if (DataHelper.contains(VALID_TYPES, field.getType())) {
								Object rawValue = getValue(bean, type.getTypeClass(), field, attribute);
								cv.add(columnName, rawValue);
								typedValues.add(new TypedValue(type, ++pos, rawValue).correctValue(field));
							}
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							log.error("Error converting value", e);
						}
					}
				}
			}
		}
		log.debug("CV=" + cv);
		return cv;
	}

	/**
	 * Gets the prepared insert statement.
	 *
	 * @param prepared the prepared
	 * @param tableName the table name
	 * @param cv the cv
	 * @return the prepared insert statement
	 */
	protected String getPreparedInsertStatement(PREPARED prepared, String tableName, ColumnsAndValues cv) {
		String result = "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getPreparedValues() + ")";
		log.debug(result);
		return result;
	}

	/**
	 * Use prepared statement.
	 *
	 * @param bean the bean
	 * @return true, if successful
	 */
	private boolean usePreparedStatement(Object bean) {
		Class<? extends Object> clazz = bean.getClass();
		return clazz.isAnnotationPresent(Table.class) && clazz.getAnnotation(Table.class).usePreparedStatement();
	}

	/**
	 * Use prepared where statement.
	 *
	 * @param bean the bean
	 * @return true, if successful
	 */
	protected boolean usePreparedWhereStatement(Object bean) {
		return usePreparedStatement(bean) && allowPreparedWhere;
	}

	/**
	 * Update.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param columns the columns
	 * @return the t
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	@Override
	public <T> T update(T bean, String... columns) throws SQLException, InitProfileException, NotSupportedException {
		return updateWhere(bean, null, columns);
	}

	/**
	 * Update where.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param where the where
	 * @param columns the columns
	 * @return the t
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
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
							log.error("Error on commit", e);
						}
					}
					if (count == 1) {
						return this.reload(bean);
					} else {
						return null;
					}
				} catch (NothingToDoException e1) {
					log.error("Nothing to do");
					return null;
				}
			} else {
				try (Statement stmt = this.connection.createStatement()) {
					int count = 0;
					try {
						String statement = this.createUpdateWhereStatement(bean, where, columns);
						log.debug(statement);
						count = stmt.executeUpdate(statement);
						if (!inTransaction) {
							try {
								this.commit();
							} catch (NotInTransactionException e) {
								log.error("Error on commit", e);
							}
						}
					} catch (NothingToDoException e) {
						log.error("Nothing to do");
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

	/**
	 * Execute update.
	 *
	 * @param statement the statement
	 * @param typedFilterValues the typed filter values
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	@Override
	public void executeUpdate(String statement, TypedValue... typedFilterValues) throws SQLException, InitProfileException, NotSupportedException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}

		if (typedFilterValues != null && typedFilterValues.length > 0) {
			try (PreparedStatement pstmt = this.getConnection().prepareStatement(statement)) {
				fillPreparedStatement(pstmt, typedFilterValues);
				pstmt.executeUpdate();
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						log.error("Error on commit", e);
					}
				}
			} finally {
				if (!inTransaction) {
					this.rollback();
				}
			}
		} else {
			try (Statement stmt = this.connection.createStatement()) {
				log.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						log.error("Error on commit", e);
					}
				}
			} finally {
				if (!inTransaction) {
					this.rollback();
				}
			}
		}
	}

	/**
	 * Delete.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	@Override
	public <T> void delete(T bean) throws SQLException, InitProfileException, NotSupportedException {

		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}
		this.deleteSubs(bean);


		if (usePreparedStatement(bean)) {
			try (PreparedStatement pstmt = getPreparedDeleteStatement(bean)) {
				pstmt.executeUpdate();
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						log.error("Error on commit", e);
					}
				}
			} finally {
				if (!inTransaction) {
					this.rollback();
				}
			}

		} else {
			try (Statement stmt = this.connection.createStatement()) {
				String statement = null;
				PrimaryKey primaryKey = getPrimaryKey(bean);
				if (primaryKey != null && primaryKey.size() > 0) {
					try {
						@SuppressWarnings("unchecked")
						T deleteBean = (T) Classes.newInstance(bean.getClass());
						setPrimaryKey(deleteBean, primaryKey);
						statement = this.createDeleteStatement(deleteBean);
					} catch (InstantiationException | IllegalAccessException e) {
						log.error("Eror creating deleteBean", e);
					}
				}
				if (statement == null) {
					statement = this.createDeleteStatement(bean);
				}
				log.debug(statement);
				stmt.executeUpdate(statement);
				if (!inTransaction) {
					try {
						this.commit();
					} catch (NotInTransactionException e) {
						log.error("Error on commit", e);
					}
				}
			} finally {
				if (!inTransaction) {
					this.rollback();
				}
			}
		}
	}

	/**
	 * Delete subs.
	 *
	 * @param bean the bean
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	protected void deleteSubs(Object bean) throws SQLException, InitProfileException, NotSupportedException {
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

	/**
	 * Delete subs.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 */
	private void deleteSubs(Object bean, Field field) throws SQLException, InitProfileException, NotSupportedException {
		for (Object sub : this.getSubs(bean, field)) {
			this.delete(sub);
		}
	}

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
					log.debug(handler.getSearchStatement());
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

	/**
	 * Select.
	 *
	 * @param handler the handler
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	@Override
	public void select(SelectHandler handler) throws SQLException, InitProfileException {
		boolean inTransaction = false;
		if (this.connection != null) {
			inTransaction = true;
		} else {
			this.beginTransaction();
		}
		
		if (handler instanceof PreparedStatementSelectHandler) {
			PreparedStatementSelectHandler preparedStatementSelectHandler = (PreparedStatementSelectHandler) handler;
			PreparedStatement pstmt = preparedStatementSelectHandler.getPreparedStatement(this.connection);

			try (ResultSet rs = pstmt.executeQuery()) {
				handler.handle(rs);
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		} else {
			try (Statement stmt = this.connection.createStatement()) {
				if (handler.isLoggingEnabled()) {
					if (log.isDebugEnabled()) {
						log.debug(handler.getSearchStatement());
					}
				}
				try (ResultSet rs = stmt.executeQuery(handler.getSearchStatement())) {
					handler.handle(rs);
				}
			} finally {
				if (!inTransaction) {
					this.closeConnection();
				}
			}
		}
	}

	/**
	 * Begin transaction.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	@Override
	public synchronized Connection beginTransaction() throws SQLException, InitProfileException {
		if (this.connection == null) {
			log.debug("profile: " + this.profile);
			this.connection = this.profile.getConnection();
		}
		return this.connection;
	}

	/**
	 * Commit.
	 *
	 * @throws NotInTransactionException the not in transaction exception
	 * @throws SQLException the SQL exception
	 */
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
					log.error("Error closing connection", e);
				}
				this.connection = null;
			}
		}
	}

	/**
	 * Rollback.
	 *
	 * @throws SQLException the SQL exception
	 */
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
	
	/**
	 * Close connection.
	 */
	private void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				log.error("Error closing connection", e);
			}
			this.connection = null;
		}
		
	}

	/**
	 * Reload.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the t
	 */
	private <T> T reload(T bean) {
		try {
			@SuppressWarnings("unchecked")
			T b = (T) Classes.newInstance(bean.getClass());
			PrimaryKey primaryKey = getPrimaryKey(bean);
			setPrimaryKey(b, primaryKey);
			return loadObject(b, true);
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
			log.error("Error reloading bean", e);
		}
		return null;
	}

	/**
	 * Creates the insert statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 */
	@Override
	public <T> String createInsertStatement(T bean) throws NotSupportedException {
		/**
		 * insert into SCHEMA.TABLE (a,b,c) values (" ", " ", 2);
		 */
		return getInsertStatement(PREPARED.FALSE, this.getQualifiedTableName(bean.getClass()), getInsertColumnValues(bean));
	}

	/**
	 * Gets the insert column values.
	 *
	 * @param bean the bean
	 * @return ColumnsAndValues
	 * @throws NotSupportedException the not supported exception
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
						log.error("Error creating statement", e);
					}
				}
			}
		}
		log.debug("CV=" + cv);

		return cv;
	}

	/**
	 * Gets the generated keys.
	 *
	 * @param bean the bean
	 * @return the generated keys
	 */
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
			return keys.toArray(new String[0]);
		} else {
			return null;
		}
	}

	/**
	 * Gets the long value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the long value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected String getLongValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Long) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param clazz the clazz
	 * @param field the field
	 * @param attribute the attribute
	 * @return the value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getValue(Object bean, Class<T> clazz, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException  {
		return (T) this.getValue(bean, field);
	}

	/**
	 * Gets the int value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the int value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected String getIntValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Integer) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the float value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the float value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected String getFloatValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Float) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the boolean value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the boolean value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected String getBooleanValue(Object bean, Field field, Attribute attribute) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return ((Boolean) this.getValue(bean, field)).toString();
		} else {
			return null;
		}
	}

	/**
	 * Gets the qualified table name.
	 *
	 * @param clazz the clazz
	 * @return the qualified table name
	 */
	@Override
	public String getQualifiedTableName(Class<? extends Object> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			log.error("");
			return "INVALID_TABLE_NAME";
		}
		return getQualifiedName(table.name());
	}

	/**
	 * Gets the column.
	 *
	 * @param clazz the clazz
	 * @param fieldName the field name
	 * @return the column
	 * @throws NoSuchFieldException the no such field exception
	 */
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

	/**
	 * Gets the insert statement.
	 *
	 * @param prepared the prepared
	 * @param tableName the table name
	 * @param cv the cv
	 * @return the insert statement
	 */
	protected String getInsertStatement(PREPARED prepared, String tableName, ColumnsAndValues cv) {
		return "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getValues(this, prepared) + ")";
	}

	/**
	 * Gets the update statement.
	 *
	 * @param prepared the prepared
	 * @param tableName the table name
	 * @param cv the cv
	 * @param whereClause the where clause
	 * @return the update statement
	 * @throws NothingToDoException the nothing to do exception
	 */
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
		log.debug(sb.toString());
		return sb.toString();
	}

	/**
	 * Gets the delete statement.
	 *
	 * @param tableName the table name
	 * @param whereClause the where clause
	 * @return the delete statement
	 */
	protected String getDeleteStatement(String tableName, String whereClause) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(whereClause)) {
			sb.append("delete from ").append(tableName);
			sb.append(" where ");
			sb.append(whereClause);
		}
		return sb.toString();
	}

	/**
	 * Gets the date value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the date value
	 */
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
			log.error("Error converting date", e);
		}

		return null;
	}

	/**
	 * Gets the date value XXX.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the date value XXX
	 */
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
			log.error("Error converting date", e);
		}

		return null;
	}

	/**
	 * Gets the calendar value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the calendar value
	 */
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
			log.error("Error converting date", e);
		}

		return null;
	}

	/**
	 * To timestamp.
	 *
	 * @param date the date
	 * @return the string
	 */
	@Override
	public String toTimestamp(Date date) {
		return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	/**
	 * To date.
	 *
	 * @param date the date
	 * @return the string
	 */
	@Override
	public String toDate(Date date) {
		return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}

	/**
	 * To time.
	 *
	 * @param date the date
	 * @return the string
	 */
	@Override
	public String toTime(Date date) {
		return "to_date('" + new SimpleDateFormat(TIME_FOR_ORACLE).format(date) + "','" + TIME_ORACLE + "')";
	}

	/**
	 * Gets the string value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @return the string value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected String getStringValue(Object bean, Field field) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null != this.getValue(bean, field)) {
			return "'" + (String) this.getValue(bean, field) + "'";
		} else {
			return null;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @return the value
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected Object getValue(Object bean, Field field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String getter = "get" + StringUtils.capitalize(field.getName());
		Method method = bean.getClass().getMethod(getter);
		return method.invoke(bean);
	}

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
	@Override
	public <T> String createUpdateStatement(T bean, String... columns) throws NotSupportedException, NothingToDoException {
		return createUpdateWhereStatement(bean, null, columns);
	}

	/**
	 * Creates the update where statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @param where the where
	 * @param columns the columns
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 * @throws NothingToDoException the nothing to do exception
	 */
	protected <T> String createUpdateWhereStatement(T bean, String where, String... columns) throws NotSupportedException, NothingToDoException {
		Table table = bean.getClass().getAnnotation(Table.class);
		if (table == null) {
			throw new NotSupportedException();
		}

		List<TypedValue> typedValues = new ArrayList<TypedValue>();
		
		ColumnsAndValues cv = getUpdateColumnValues(bean, typedValues, columns);

		String whereClause = addPrimaryKeyWhereClause(bean, where);
		return getUpdateStatement(PREPARED.FALSE, 
				this.getQualifiedTableName(bean.getClass()), cv, whereClause);
	}
	
	/**
	 * Adds the primary key where clause.
	 *
	 * @param bean the bean
	 * @param where the where
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 */
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
					StringBuilder sb = new StringBuilder();
					try {
						if (field.getType() == String.class) {
							attributeValue = this.getStringValue(bean, field);
							if (isTrimStrings(bean.getClass(), field)) {
								sb.append("trim(").append(attribute.name()).append(")=").append(attributeValue);
							} else {
								sb.append(attribute.name()).append("=").append(attributeValue);
							}
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
						log.error("Error converting value", e);
					}
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					if (sb.length() > 0) {
						whereClause.append(sb);
					} else {
						whereClause.append(attribute.name()).append("=").append(attributeValue);
					}
				}
			}
		}
		return whereClause.toString();
	}

	/**
	 * Creates the delete statement.
	 *
	 * @param <T> the generic type
	 * @param bean the bean
	 * @return the string
	 * @throws NotSupportedException the not supported exception
	 */
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
				StringBuilder sb = new StringBuilder();
				try {
					if (field.getType() == String.class) {
						attributeValue = this.getStringValue(bean, field);
						if (isTrimStrings(bean.getClass(), field)) {
							sb.append("trim(").append(attribute.name()).append(")=").append(attributeValue);
						} else {
							sb.append(attribute.name()).append("=").append(attributeValue);
						}
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
					log.error("Error converting value", e);
				}
				if (StringUtils.isNotBlank(attributeValue)) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					if (sb.length() > 0) {
						whereClause.append(sb);
					} else {
						whereClause.append(attribute.name()).append("=").append(attributeValue);
					}
				}
			}
		}

		return getDeleteStatement(this.getQualifiedTableName(bean.getClass()), whereClause.toString());
	}

	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Sets the schema.
	 *
	 * @param schema the new schema
	 */
	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	@Override
	public String getTimeStamp() {
		return "SYSDATE";
	}

	/**
	 * Gets the sys date.
	 *
	 * @return the sys date
	 */
	@Override
	public String getSysDate() {
		return "SYSDATE";
	}
	
	/**
	 * Gets the now plus hours.
	 *
	 * @param hours the hours
	 * @return the now plus hours
	 */
	public String getNowPlusHours(int hours) {
		return "SYSDATE " + ((hours >= 0) ? "+":"") + hours + " / 24";
	}

	/**
	 * Close.
	 *
	 * @throws SQLException the SQL exception
	 */
	@Override
	public void close() throws SQLException {
		synchronized (this) {
			if (this.connection != null) {
				this.rollback();
				this.connection = null;
			}
		}
	}

	/**
	 * Gets the sequence number.
	 *
	 * @param beanClazz the bean clazz
	 * @param sequenceAttribute the sequence attribute
	 * @return the sequence number
	 * @throws Exception the exception
	 */
	public PrimaryKey getSequenceNumber(Class<?> beanClazz, Attribute sequenceAttribute) throws Exception {

		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(getQualifiedQuotedName(sequenceAttribute.sequence()));
		select(handler);
		return new PrimaryKey(beanClazz, sequenceAttribute.name(), handler.getResult());
	}

	/**
	 * Gets the sequence number.
	 *
	 * @param sequenceName the sequence name
	 * @return the sequence number
	 * @throws Exception the exception
	 */
	public Long getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(getQualifiedQuotedName(sequenceName));
		select(handler);
		return handler.getResult();
	}
	
	/** The Constant HOCHKOMMA. */
	static final String HOCHKOMMA = "\"";
	
	/**
	 * Gets the qualified quoted name.
	 *
	 * @param name the name
	 * @return the qualified quoted name
	 */
	public String getQualifiedQuotedName(String name) {
		if (!StringUtils.contains(name, '.') && this.schema != null) {
			return HOCHKOMMA + this.schema + HOCHKOMMA + "." + HOCHKOMMA + name + HOCHKOMMA;
		} else {
			return HOCHKOMMA + name+ HOCHKOMMA;
		}
	}

	/**
	 * Gets the qualified name.
	 *
	 * @param name the name
	 * @return the qualified name
	 */
	public String getQualifiedName(String name) {
		if (!StringUtils.contains(name, '.') && this.schema != null) {
			return this.schema + "." + name;
		} else {
			return name;
		}
	}

	/**
	 * Gets the generated key.
	 *
	 * @param stmt the stmt
	 * @param generatedColumns the generated columns
	 * @param beanClazz the bean clazz
	 * @return the generated key
	 * @throws SQLException the SQL exception
	 */
	public PrimaryKey getGeneratedKey(Statement stmt, String[] generatedColumns, Class<?> beanClazz) throws SQLException {
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		return new PrimaryKey(beanClazz, generatedColumns, generatedKeys);
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	@Override
	public DataSource getDataSource() {
		return new JDBCDataSource(this.name);
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the string as db string.
	 *
	 * @param value the value
	 * @return the string as db string
	 */
	@Override
	public String getStringAsDbString(String value) {
		return "'" + value + "'";
	}

	/**
	 * Gets the date as db string.
	 *
	 * @param value the value
	 * @return the date as db string
	 */
	@Override
	public String getDateAsDbString(Date value) {
		return toDate(value);
	}

	/**
	 * Gets the time as db string.
	 *
	 * @param value the value
	 * @return the time as db string
	 */
	@Override
	public String getTimeAsDbString(Date value) {
		return toTime(value);
	}

	/**
	 * Gets the timestamp as db string.
	 *
	 * @param value the value
	 * @return the timestamp as db string
	 */
	@Override
	public String getTimestampAsDbString(Date value) {
		return toTimestamp(value);
	}

	/**
	 * Gets the long as db string.
	 *
	 * @param value the value
	 * @return the long as db string
	 */
	@Override
	public String getLongAsDbString(Long value) {
		return Long.toString(value);
	}

	/**
	 * Gets the int as db string.
	 *
	 * @param value the value
	 * @return the int as db string
	 */
	@Override
	public String getIntAsDbString(Integer value) {
		return Integer.toString(value);
	}

	/**
	 * Gets the float as db string.
	 *
	 * @param value the value
	 * @return the float as db string
	 */
	@Override
	public String getFloatAsDbString(Float value) {
		return Float.toString(value);
	}

	/**
	 * Gets the boolean as db string.
	 *
	 * @param value the value
	 * @return the boolean as db string
	 */
	@Override
	public String getBooleanAsDbString(Boolean value) {
		return Boolean.toString(value);
	}
}
