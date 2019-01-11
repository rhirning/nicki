package org.mgnl.nicki.db.dynamic.objects;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.db.annotation.Sync;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotInTransactionException;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.dynamic.objects.SyncChange.ACTION;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncManager implements Serializable{
	private static final long serialVersionUID = 3719182743769453368L;
	private static SyncManager instance = new SyncManager();
	
	public static SyncManager getInstance() {
		return instance;
	}
	
	public static void syncObject(DynamicObject dynamicObject) throws NotSupportedException, SyncException {
		getInstance().sync(dynamicObject);
	}
	
	public static <T extends DynamicObject> List<SyncEntry> loadObject(Class<T> clazz, String id, Date date) throws NotSupportedException, SyncException {
		return getInstance().load(clazz, id, date);
	}
	
	public static <T extends DynamicObject> List<SyncChange> diffObject(Class<T> clazz, String id, Date date1, Date date2) throws NotSupportedException, SyncException {
		return getInstance().diff(clazz, id, date1, date2);
	}

	private <T extends DynamicObject> List<SyncChange> diff(Class<T> clazz, String id, Date date1, Date date2) throws NotSupportedException, SyncException {
		List<SyncChange> changes = new ArrayList<>();
		
		List<SyncEntry> list1 = load(clazz, id, date1);
		List<SyncEntry> list2 = load(clazz, id, date2);
		
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
					changes.add(new SyncChange(ACTION.REMOVE, e1.getAttribute(), e1.getContent()));
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
					changes.add(new SyncChange(ACTION.ADD, e1.getAttribute(), e1.getContent()));
				}
			}
			
		}
		
		return changes;
	}

	private <T extends DynamicObject> List<SyncEntry> load(Class<T> clazz, String id, Date date) throws NotSupportedException, SyncException {

		if (!clazz.isAnnotationPresent(Sync.class)) {
			throw new NotSupportedException("@Sync Annotation not present in class " + clazz.getName());
		}
		Sync annotation = clazz.getAnnotation(Sync.class);
		try (DBContext dbContext = DBContextManager.getContext(Config.getString(annotation.context()))) {
			SyncEntry syncEntry = getEmptyEntry(annotation.entryClass());
			syncEntry.setId(id);
			syncEntry.setType(annotation.entryType());
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
	
	private void sync(DynamicObject dynamicObject) throws NotSupportedException, SyncException {
		if (dynamicObject == null) {
			return;
		}

		if (!dynamicObject.getClass().isAnnotationPresent(Sync.class)) {
			throw new NotSupportedException("@Sync Annotation not present in class " + dynamicObject.getClass().getName());
		}
		
		Sync annotation = dynamicObject.getClass().getAnnotation(Sync.class);
		
		
		DataModel model = dynamicObject.getModel();
		Date now = new Date();
		synchronized (instance) {
			try (DBContext dbContext = DBContextManager.getContext(Config.getString(annotation.context()))) {
				dbContext.beginTransaction();
				List<SyncEntry> storedEntries = getStoredEntries(dbContext, annotation, dynamicObject.getNamingValue());
	
				for (String attributeName : model.getAttributes().keySet()) {
					List<SyncEntry> storedAttributeEntries = getStoredAttributeEntries(storedEntries, attributeName);
					DynamicAttribute attribute = model.getAttributes().get(attributeName);
					SyncEntry syncEntry = getEmptyEntry(annotation.entryClass());
					syncEntry.setAttribute(attributeName);
					syncEntry.setFrom(now);
					syncEntry.setId(dynamicObject.getNamingValue());
					syncEntry.setType(annotation.entryType());
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
									log.debug(dynamicObject.getName() + ": add attribute value " + attributeName + "=" + value);
								}
							}
						}
						// delete deleted
						if (storedAttributeEntries != null && storedAttributeEntries.size() > 0) {
							for (SyncEntry storedEntry: storedAttributeEntries) {
								String content = storedEntry.getContent();
								if (values == null || values.size() == 0 || !contains(values, content)) {
									deleteEntry(dbContext, storedEntry, now);
									log.debug(dynamicObject.getName() + ": delete attribute value " + attributeName + "=" + content);
								}
							}
						}
					} else {
						SyncEntry storedSyncEntry = null;
						if (storedAttributeEntries != null && storedAttributeEntries.size() > 0) {
							storedSyncEntry = storedAttributeEntries.get(0);
						}
						String value = StringUtils.stripToNull(dynamicObject.getAttribute(attributeName));
						// delete or modify?
						if (storedSyncEntry != null) {
							if (value == null) {
								deleteEntry(dbContext, storedSyncEntry, now);
								log.debug(dynamicObject.getName() + ": delete attribute " + attributeName);
							} else if (!StringUtils.equals(value, storedSyncEntry.getContent())) {
								deleteEntry(dbContext, storedSyncEntry, now);
								syncEntry.setContent(value);
								dbContext.create(syncEntry);							
								log.debug(dynamicObject.getName() + ": modify attribute " + attributeName + "=" + value);
							}
						} else if (value != null) {
							syncEntry.setContent(value);
							dbContext.create(syncEntry);
							log.debug(dynamicObject.getName() + ": add attribute " + attributeName + "=" + value);
						}
					}
				}
				dbContext.commit();
			} catch (SQLException | InitProfileException | NotInTransactionException | InstantiationException | IllegalAccessException e) {
				log.error("Error synching object", e);
				throw new SyncException(e);
			}
		}
	}

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

	private void deleteEntry(DBContext dbContext, SyncEntry storedEntry, Date toTime) throws InitProfileException, NotSupportedException, SQLException {
		storedEntry.setTo(toTime);
		dbContext.update(storedEntry, "to");
	}

	private SyncEntry getEmptyEntry(Class<? extends SyncEntry> entryClass) throws InstantiationException, IllegalAccessException {
		return entryClass.newInstance();
	}


	private List<SyncEntry> getStoredEntries(DBContext dbContext, Sync annotation, String id) throws InstantiationException, IllegalAccessException, SQLException, InitProfileException {
		SyncEntry searchEntry = annotation.entryClass().newInstance();
		searchEntry.setId(id);
		searchEntry.setType(annotation.entryType());
		List<SyncEntry> entries = dbContext.loadObjects(searchEntry, false, "TO_TIME IS NULL", null);
		if (entries != null) {
			return entries;
		} else {
			return new ArrayList<>();
		}
	}

}
