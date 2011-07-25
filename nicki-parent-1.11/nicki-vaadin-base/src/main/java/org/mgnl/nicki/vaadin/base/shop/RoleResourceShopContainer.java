package org.mgnl.nicki.vaadin.base.shop;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Function;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

@SuppressWarnings("serial")
public class RoleResourceShopContainer implements ShopContainer{

	private String STATUS_ORDERED; 
	private String STATUS_CANCELED; 
	private String STATUS_PROVISIONED; 

    private String[] visibleColumns = new String[] {
    	PROPERTY_STATUS,
    	PROPERTY_NAME,
    	PROPERTY_PATH
    };
	
	private IndexedContainer container = new IndexedContainer();
	private DynamicObject person;
	private String rolesBasedn;
	private String resourcesBasedn;

	public RoleResourceShopContainer(DynamicObject person, String i18nBase) {
		super();
		this.person = person;
		STATUS_ORDERED = I18n.getText(i18nBase + ".status.ordered");
		STATUS_CANCELED = I18n.getText(i18nBase + ".status.canceled");
		STATUS_PROVISIONED = I18n.getText(i18nBase + ".status.provisioned");
		this.rolesBasedn = Config.getProperty("nicki.roles.basedn", "");
		this.resourcesBasedn = Config.getProperty("nicki.resources.basedn", "");
	}



	public IndexedContainer getArticles() throws DynamicObjectException {
		container = new IndexedContainer();
		container.addContainerProperty(PROPERTY_STATUS, String.class, null);
		container.addContainerProperty(PROPERTY_NAME, String.class, null);
		container.addContainerProperty(PROPERTY_CATEGORY, String.class, null);

		container.addContainerProperty(PROPERTY_PATH, String.class, null);
		addRoles();
		addResources();
		
		return container;
	}



	private void addRoles() throws DynamicObjectException {
		Function function = new Function(person.getContext());
		function.initDataModel();
		@SuppressWarnings("unchecked")
		List<DynamicObject> loadedRoles = (List<DynamicObject>) function.execute("getAllRoles", null);

	    if (loadedRoles != null) {
		    for (Iterator<DynamicObject> iterator = loadedRoles.iterator(); iterator.hasNext();) {
				DynamicObject p = iterator.next();
				if (p instanceof Role) {
					Role role = (Role) p;
					if (StringUtils.isNotEmpty(role.getDisplayName())) {
						Item item = container.addItem(role);
						item.getItemProperty(PROPERTY_NAME).setValue(role.getDisplayName());
						item.getItemProperty(PROPERTY_PATH).setValue(role.getSlashPath(rolesBasedn));
						item.getItemProperty(PROPERTY_CATEGORY).setValue("role");
						if (getPerson().hasRole(role)) {
							item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_PROVISIONED);
						}
					}
				}
			}
	    }
	}

	private void addResources() throws DynamicObjectException {
		Function function = new Function(person.getContext());
		function.initDataModel();
		@SuppressWarnings("unchecked")
		List<DynamicObject> loadedResources = (List<DynamicObject>) function.execute("getAllResources", null);

	    if (loadedResources != null) {
		    for (Iterator<DynamicObject> iterator = loadedResources.iterator(); iterator.hasNext();) {
				DynamicObject p = iterator.next();
				if (p instanceof Resource) {
					Resource resource = (Resource) p;
					if (StringUtils.isNotEmpty(resource.getDisplayName())) {
						Item item = container.addItem(resource);
						item.getItemProperty(PROPERTY_NAME).setValue(resource.getDisplayName());
						item.getItemProperty(PROPERTY_PATH).setValue(resource.getSlashPath(resourcesBasedn));
						item.getItemProperty(PROPERTY_CATEGORY).setValue("resource");
						if (getPerson().hasResource(resource)) {
							item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_PROVISIONED);
						}
					}
				}
			}
	    }
	}



	public void orderItem(Object target) {
		Item item = container.getItem(target);
		item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_ORDERED);
	}
	
	public void cancelItem(Object target) {
		Item item = container.getItem(target);
		String oldValue = (String) item.getItemProperty(PROPERTY_STATUS).getValue();

		if (StringUtils.equals(oldValue, STATUS_ORDERED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue("");
		} else if (StringUtils.equals(oldValue, STATUS_PROVISIONED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_CANCELED);
		}
	}



	public void setCategoryFilter(List<Object> values) {
		this.container.removeAllContainerFilters();
		if (values != null && values.size() > 0) {
			for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
				String filterString = (String) iterator.next();
				this.container.addContainerFilter(PROPERTY_CATEGORY, filterString, true, false);
			}
		}
	}



	public Person getPerson() {
		return ((Person)person);
	}



	public String[] getVisibleColumns() {
		return visibleColumns;
	}
	

}
