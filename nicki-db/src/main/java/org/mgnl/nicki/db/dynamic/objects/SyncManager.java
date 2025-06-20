package org.mgnl.nicki.db.dynamic.objects;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.helper.AttributeMapper;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotInTransactionException;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.dynamic.objects.SyncChange.ACTION;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class SyncManager.
 */
@Slf4j
public class SyncManager implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3719182743769453368L;
	
	/** The instance. */
	private static SyncManager instance = new SyncManager();
	
	/**
	 * Gets the single instance of SyncManager.
	 *
	 * @return single instance of SyncManager
	 */
	public static SyncManager getInstance() {
		return instance;
	}
	
	/**
	 * Sync object.
	 *
	 * @param dynamicObject the dynamic object
	 * @param syncConfig the sync config
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	public static List<SyncChange> syncObject(DynamicObject dynamicObject, SyncConfig syncConfig) throws NotSupportedException, SyncException {
		return getInstance().sync(dynamicObject, syncConfig);
	}
	
	/**
	 * Sync object.
	 *
	 * @param context the context
	 * @param path the path
	 * @param syncConfig the sync config
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public static List<SyncChange> syncObject(String context, String path, SyncConfig syncConfig) throws NotSupportedException, SyncException, InvalidPrincipalException {
		DynamicObject dynamicObject = AppContext.getSystemContext(context).loadObject(path);
		return getInstance().sync(dynamicObject, syncConfig);
	}
	
	/**
	 * Load object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param syncConfig the sync config
	 * @param id the id
	 * @param date the date
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	public static <T extends DynamicObject> List<SyncEntry> loadObject(Class<T> clazz, SyncConfig syncConfig, String id, Date date) throws NotSupportedException, SyncException {
		return getInstance().load(clazz, syncConfig, id, date);
	}
	
	/**
	 * Diff object.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param syncConfig the sync config
	 * @param id the id
	 * @param date1 the date 1
	 * @param date2 the date 2
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	public static <T extends DynamicObject> List<SyncChange> diffObject(Class<T> clazz, SyncConfig syncConfig, String id, Date date1, Date date2) throws NotSupportedException, SyncException {
		return getInstance().diff(clazz, syncConfig, id, date1, date2);
	}

	/**
	 * Diff.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param syncConfig the sync config
	 * @param id the id
	 * @param date1 the date 1
	 * @param date2 the date 2
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	private <T extends DynamicObject> List<SyncChange> diff(Class<T> clazz, SyncConfig syncConfig, String id, Date date1, Date date2) throws NotSupportedException, SyncException {
		List<SyncChange> changes = new ArrayList<>();
		
		List<SyncEntry> list1 = load(clazz, syncConfig, id, date1);
		List<SyncEntry> list2 = load(clazz, syncConfig, id, date2);
		
		if (list1 != null && list2 != null) {
			// remove
			for (SyncEntry e1 : list1) {
				
				boolean found = false;
				for (SyncEntry e2 : list2) {
					if (StringUtils.equals(e1.getAttribute(), e2.getAttribute()) && StringUtils.equals(e1.getContent(), e2.getContent())) {
						found = true;
					}
				}
				if (!found) {
					changes.add(new SyncChange(e1.getTo(), ACTION.REMOVE, e1.getAttribute(), e1.getContent()));
				}
			}
			// add
			for (SyncEntry e1 : list2) {
				
				boolean found = false;
				for (SyncEntry e2 : list1) {
					if (StringUtils.equals(e1.getAttribute(), e2.getAttribute()) && StringUtils.equals(e1.getContent(), e2.getContent())) {
						found = true;
					}
				}
				if (!found) {
					changes.add(new SyncChange(e1.getFrom(), ACTION.ADD, e1.getAttribute(), e1.getContent()));
				}
			}
			
		}
		
		return changes;
	}

	/**
	 * Load.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param syncConfig the sync config
	 * @param id the id
	 * @param date the date
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	private <T extends DynamicObject> List<SyncEntry> load(Class<T> clazz, SyncConfig syncConfig, String id, Date date) throws NotSupportedException, SyncException {

		try (DBContext dbContext = DBContextManager.getContext(Config.getString(syncConfig.getContext()))) {
			SyncEntry syncEntry = getEmptyEntry(syncConfig.getEntryClass());
			syncEntry.setId(id);
			syncEntry.setType(syncConfig.getEntryType());
			StringBuilder filter = new StringBuilder();
			filter.append("FROM_TIME <= ").append(dbContext.getTimestampAsDbString(date));
			filter.append(" AND (");
			filter.append("TO_TIME IS NULL OR ");
			filter.append("TO_TIME >= ").append(dbContext.getTimestampAsDbString(date));
			filter.append(")");
			return dbContext.loadObjects(syncEntry, false, filter.toString(), "ATTRIBUTE, CONTENT");
		} catch (SQLException | InstantiationException | IllegalAccessException | InitProfileException e) {
			log.error("Error loading", e);
			throw new SyncException(e);
		}		
	}
	
	/**
	 * Sync.
	 *
	 * @param dynamicObject the dynamic object
	 * @param syncConfig the sync config
	 * @return the list
	 * @throws NotSupportedException the not supported exception
	 * @throws SyncException the sync exception
	 */
	private List<SyncChange> sync(DynamicObject dynamicObject, SyncConfig syncConfig) throws NotSupportedException, SyncException {
		List<SyncChange> changes = new ArrayList<>();
		if (dynamicObject == null) {
			return changes;
		}
				
		DELETE delete = syncConfig.isHistory() ? DELETE.NO : DELETE.YES;

		AttributeMapper attributeMapper = syncConfig.getAttributeMapper();
		
		
		DataModel model = dynamicObject.getModel();
		Date now = new Date();
		synchronized (instance) {
			try (DBContext dbContext = DBContextManager.getContext(Config.getString(syncConfig.getContext()))) {
				dbContext.beginTransaction();
				SyncEntry syncEntry = getEmptyEntry(syncConfig.getEntryClass());
				List<SyncEntry> storedEntries = getStoredEntries(dbContext, syncConfig, syncConfig.getIdGenerator().getId(dynamicObject));
	
				for (String attributeName : model.getAttributes().keySet()) {
					if (attributeMapper.hasInternal(attributeName)) {
						String externalAttribute = attributeMapper.toExternal(attributeName);
						List<SyncEntry> storedAttributeEntries = getStoredAttributeEntries(storedEntries, attributeName);
						DynamicAttribute attribute = model.getAttributes().get(attributeName);
						
						syncEntry.setAttribute(externalAttribute);
						syncEntry.setFrom(now);
						syncEntry.setId(syncConfig.getIdGenerator().getId(dynamicObject));
						syncEntry.setType(syncConfig.getEntryType());
						if (attribute.isMultiple()) {
							@SuppressWarnings("unchecked")
							List<String> values = (List<String>) dynamicObject.get(attributeName);
							// create missing
							if (values != null) {
								for (String valueEntry : values) {
									String value = StringUtils.stripToNull(valueEntry);
									if (!containsValue(storedEntries, value)) {
										syncEntry.setContent(value);
										dbContext.create(syncEntry);
										changes.add(new SyncChange(now, ACTION.ADD, syncEntry.getAttribute(), syncEntry.getContent()));
										log.debug(dynamicObject.getName() + ": add attribute value (internal=" + attributeName + ") " + externalAttribute + "=" + value);
									}
								}
							}
							// delete deleted
							if (storedAttributeEntries != null && storedAttributeEntries.size() > 0) {
								for (SyncEntry storedEntry: storedAttributeEntries) {
									String content = storedEntry.getContent();
									if (values == null || values.size() == 0 || !contains(values, content)) {
										deleteEntry(dbContext, storedEntry, now, delete);
										changes.add(new SyncChange(now, ACTION.REMOVE, storedEntry.getAttribute(), storedEntry.getContent()));
										log.debug(dynamicObject.getName() + ": delete attribute value (internal=" + attributeName + ") " + externalAttribute + "=" + content);
									}
								}
							}
						} else {
							SyncEntry storedEntry = null;
							if (storedAttributeEntries != null && storedAttributeEntries.size() > 0) {
								storedEntry = storedAttributeEntries.get(0);
							}
							String value = StringUtils.stripToNull(dynamicObject.getAttribute(attributeName));
							// delete or modify?
							if (storedEntry != null) {
								if (value == null) {
									deleteEntry(dbContext, storedEntry, now, delete);
									changes.add(new SyncChange(now, ACTION.REMOVE, storedEntry.getAttribute(), storedEntry.getContent()));
									log.debug(dynamicObject.getName() + ": delete attribute (internal=" + attributeName + ")" + externalAttribute);
								} else if (!StringUtils.equals(value, storedEntry.getContent())) {
									deleteEntry(dbContext, storedEntry, now, delete);
									changes.add(new SyncChange(now, ACTION.REMOVE, storedEntry.getAttribute(), storedEntry.getContent()));
									syncEntry.setContent(value);
									dbContext.create(syncEntry);
									changes.add(new SyncChange(now, ACTION.ADD, syncEntry.getAttribute(), syncEntry.getContent()));
									log.debug(dynamicObject.getName() + ": modify attribute (internal=" + attributeName + ")" + externalAttribute + "=" + value);
								}
							} else if (value != null) {
								syncEntry.setContent(value);
								dbContext.create(syncEntry);
								changes.add(new SyncChange(now, ACTION.ADD, syncEntry.getAttribute(), syncEntry.getContent()));
								log.debug(dynamicObject.getName() + ": add attribute (internal=" + attributeName + ")" + externalAttribute + "=" + value);
							}
						}
					}
				}
				dbContext.commit();
			} catch (SQLException | InitProfileException | NotInTransactionException | InstantiationException | IllegalAccessException e) {
				log.error("Error synching object", e);
				throw new SyncException(e);
			}
		}
		return changes;
	}

	/**
	 * Contains.
	 *
	 * @param values the values
	 * @param value the value
	 * @return true, if successful
	 */
	private boolean contains(List<String> values, String value) {
		value = StringUtils.stripToNull(value);
		if (values != null && value != null) {
			for (String entry : values) {
				if (StringUtils.equals(StringUtils.stripToNull(entry), value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the stored attribute entries.
	 *
	 * @param entries the entries
	 * @param attributeName the attribute name
	 * @return the stored attribute entries
	 */
	private List<SyncEntry> getStoredAttributeEntries(List<SyncEntry> entries,
			String attributeName) {
		List<SyncEntry> list = new ArrayList<>();
		if (entries != null) {
			for (SyncEntry entry : entries) {
				if (StringUtils.equals(attributeName, entry.getAttribute())) {
					list.add(entry);
				}
			}
		}
		return list;
	}

	/**
	 * Contains value.
	 *
	 * @param entries the entries
	 * @param value the value
	 * @return true, if successful
	 */
	private boolean containsValue(List<SyncEntry> entries, String value) {
		value = StringUtils.stripToNull(value);
		if (entries != null && value != null) {
			for (SyncEntry entry : entries) {
				if (StringUtils.equals(StringUtils.stripToNull(entry.getContent()), value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * The Enum DELETE.
	 */
	enum DELETE {
/** The yes. */
YES, 
 /** The no. */
 NO}

	/**
	 * Delete entry.
	 *
	 * @param dbContext the db context
	 * @param storedEntry the stored entry
	 * @param toTime the to time
	 * @param delete the delete
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 * @throws SQLException the SQL exception
	 */
	private void deleteEntry(DBContext dbContext, SyncEntry storedEntry, Date toTime, DELETE delete) throws InitProfileException, NotSupportedException, SQLException {
		if (delete == DELETE.YES) {
			dbContext.delete(storedEntry);
		} else {
			storedEntry.setTo(toTime);
			dbContext.update(storedEntry, "to");
		}
	}

	/**
	 * Gets the empty entry.
	 *
	 * @param entryClass the entry class
	 * @return the empty entry
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private SyncEntry getEmptyEntry(Class<? extends SyncEntry> entryClass) throws InstantiationException, IllegalAccessException {
		return Classes.newInstance(entryClass);
	}


	/**
	 * Gets the stored entries.
	 *
	 * @param dbContext the db context
	 * @param syncConfig the sync config
	 * @param id the id
	 * @return the stored entries
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	private List<SyncEntry> getStoredEntries(DBContext dbContext, SyncConfig syncConfig, String id) throws InstantiationException, IllegalAccessException, SQLException, InitProfileException {
		SyncEntry searchEntry = Classes.newInstance(syncConfig.getEntryClass());
		searchEntry.setId(id);
		searchEntry.setType(syncConfig.getEntryType());
		List<SyncEntry> entries = dbContext.loadObjects(searchEntry, false, "TO_TIME IS NULL", null);
		if (entries != null) {
			return entries;
		} else {
			return new ArrayList<>();
		}
	}

}
