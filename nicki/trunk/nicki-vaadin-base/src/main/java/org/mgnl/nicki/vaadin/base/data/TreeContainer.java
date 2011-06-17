package org.mgnl.nicki.vaadin.base.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;

@SuppressWarnings("serial")
public class TreeContainer implements Serializable {
	public static final String PROPERTY_NAME = "name"; 
	public static final String PROPERTY_LOADED = "loaded"; 
	public static final String PROPERTY_ICON = "icon"; 
	
	private HierarchicalContainer container = new HierarchicalContainer();
	private String name;
	private DynamicObject root;
	private DataProvider treeDataProvider;
	private Map<Class<?>, Icon> classIcons= new HashMap<Class<?>, Icon>();
	private EntryFilter entryFilter;
	private NickiContext context;
	
	public TreeContainer(NickiContext context, DataProvider treeDataProvider, String name) {
		this.context = context;
		this.treeDataProvider = treeDataProvider;
		this.root = treeDataProvider.getRoot(context);
		this.name = name;
		this.entryFilter = treeDataProvider.getEntryFilter();
	}
	public HierarchicalContainer getTree() {
		container = new HierarchicalContainer();
		container.addContainerProperty(PROPERTY_NAME, String.class, null);
		container.addContainerProperty(PROPERTY_LOADED, Boolean.class, null);
        container.addContainerProperty(PROPERTY_ICON, ThemeResource.class, null);
        
        // add Root
//	    root = ObjectBuilder.loadObject(this.parentPath);
	    Item item = container.addItem(root);
		item.getItemProperty(PROPERTY_NAME).setValue(name);
		item.getItemProperty(PROPERTY_LOADED).setValue(false);
		item.getItemProperty(PROPERTY_ICON).setValue(null);
	    
	    return container;
	}
	
	public void setClassIcon(Class<?> classDefinition, Icon icon) {
		this.classIcons.put(classDefinition, icon);
	}

	public Item addItem(DynamicObject object) {
		if (this.entryFilter.accepts(object)) {
			Item item = container.addItem(object);
			item.getItemProperty(PROPERTY_NAME).setValue(object.getName());
			item.getItemProperty(PROPERTY_LOADED).setValue(false);
			if (this.classIcons.keySet().contains(object.getClass())) {
	            item.getItemProperty(PROPERTY_ICON).setValue(
	                    new ThemeResource(this.classIcons.get(object.getClass()).getResourcePath()));
			}
			return item;
		}
		return null;
	}

	public void loadChildren(DynamicObject object) {
		Property loaded = container.getItem(object).getItemProperty(PROPERTY_LOADED);
		if (!(Boolean)loaded.getValue()) {
			if (object == this.root) {
			    List<DynamicObject> objects = this.treeDataProvider.getChildren(context);
			    if (objects != null) {
				    for (Iterator<DynamicObject> iterator = objects.iterator(); iterator.hasNext();) {
						DynamicObject p = iterator.next();
						if (this.entryFilter.accepts(p)) {
							addItem(p, root, true);
							boolean childrenAllowed = p.getModel().childrenAllowed();
							container.setChildrenAllowed(p, childrenAllowed);
						}
					}
			    }

			} else {
				addChildren(object, object.getAllChildren());
			}
		}
		loaded.setValue(true);
	}
	
	public void addChildren(Object parent,
			List<DynamicObject> children) {
		for (Iterator<DynamicObject> iterator = children.iterator(); iterator.hasNext();) {
			DynamicObject p = iterator.next();
			if (this.entryFilter.accepts(p)) {
				addChild(parent, p);
			}
		}
	}

	public void addChild(Object parent, DynamicObject child) {
		if (this.entryFilter.accepts(child)) {
			boolean childrenAllowed = child.getModel().childrenAllowed();
			addItem(child, parent, childrenAllowed);
		}
	}

	public void addItem(DynamicObject object, Object parent, boolean childrenAllowed) {
		if (this.entryFilter.accepts(object)) {
			Item item = container.addItem(object);
			if (item != null) {
				item.getItemProperty(PROPERTY_NAME).setValue(object.getName());
				item.getItemProperty(PROPERTY_LOADED).setValue(false);
				if (this.classIcons.keySet().contains(object.getClass())) {
		            item.getItemProperty(PROPERTY_ICON).setValue(
		                    new ThemeResource(this.classIcons.get(object.getClass()).getResourcePath()));
				}

				container.setParent(object, parent);
				container.setChildrenAllowed(object, childrenAllowed);
			}
		}
	}

	public DynamicObject getRoot() {
		return root;
	}

	public void setParent(DynamicObject object, DynamicObject parent) throws DynamicObjectException {
		container.setParent(object, parent);
		String newPath = LdapHelper.getPath(parent.getPath(), object.getModel().getNamingLdapAttribute(), object.getNamingValue());
		object.move(newPath);
	}

	public DynamicObject getParent(DynamicObject child) {
		return (DynamicObject) container.getParent(child);
	}
	
	public void removeChildren(DynamicObject parent) {
		if (parent != null) {
			Item item = container.getItem(parent);
			if (item != null) {
				while (container.getChildren(parent)!= null && container.getChildren(parent).size() > 0) {
					DynamicObject child = (DynamicObject) container.getChildren(parent).iterator().next();
					container.removeItemRecursively(child);
				}
				item.getItemProperty(PROPERTY_LOADED).setValue(false);
			}
		}
	}
	
	public boolean isParent(DynamicObject parent,
			DynamicObject child) {
		DynamicObject object = child;
		while (object != null) {
			if (object == parent) {
				return true;
			}
			object = getParent(object);
		}
		return false;
	}
	public String getRootMessage() {
		return this.treeDataProvider.getMessage();
	}



}
