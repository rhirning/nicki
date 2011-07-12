package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;

@SuppressWarnings("serial")
public class DataModel implements Serializable {

	private Map<String, DynamicAttribute> attributes = new HashMap<String, DynamicAttribute>();
	private List<String> objectClasses = new ArrayList<String>();
	private List<String> additionalObjectClasses = new ArrayList<String>();
	private String namingAttribute = null;
	private Map<String, String> acceptors = new HashMap<String, String>();

	private List<DynamicAttribute> mandatoryAttributes = null;
	private List<DynamicAttribute> optionalAttributes = null;
	private List<DynamicAttribute> listOptionalAttributes = null;
	private List<DynamicAttribute> foreignKeys = null;
	private List<DynamicAttribute> listForeignKeys = null;

	private Map<String, String> children = new HashMap<String, String>();
	private Map<String, DynamicReference> references = new HashMap<String, DynamicReference>();
	

	public enum ATTRIBUTE_TYPE {MANDATORY, OPTIONAL, OPTIONAL_LIST, FOREIGN_KEY, FOREIGN_KEY_LIST, STRUCTURED, UNDEFINED};
	
	// TODO Hier fang ich an
	public List<String> getObjectClasses() {
		return objectClasses;
	}
	public String getNamingAttribute() {
		return namingAttribute;
	}
	public List<DynamicAttribute> getMandatoryAttributes() {
		if (this.mandatoryAttributes == null) {
			this.mandatoryAttributes = new ArrayList<DynamicAttribute>();
			for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (dynAttribute.isMandatory()) {
					this.mandatoryAttributes.add(dynAttribute);
				}
			}
		}
		return mandatoryAttributes;
	}
	public List<DynamicAttribute> getOptionalAttributes() {
		if (this.optionalAttributes == null) {
			this.optionalAttributes = new ArrayList<DynamicAttribute>();
			for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (!dynAttribute.isMandatory()) {
					this.optionalAttributes.add(dynAttribute);
				}
			}
		}
		return optionalAttributes;
	}
	public List<DynamicAttribute> getListOptionalAttributes() {
		if (this.listOptionalAttributes == null) {
			this.listOptionalAttributes = new ArrayList<DynamicAttribute>();
			for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (dynAttribute.isMultiple()) {
					this.listOptionalAttributes.add(dynAttribute);
				}
			}
		}
		return listOptionalAttributes;
	}
	public List<DynamicAttribute> getForeignKeys() {
		if (this.foreignKeys == null) {
			this.foreignKeys = new ArrayList<DynamicAttribute>();
			for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (dynAttribute.isForeignKey() && !dynAttribute.isMultiple()) {
					this.foreignKeys.add(dynAttribute);
				}
			}
		}
		return foreignKeys;
	}
	public List<DynamicAttribute> getListForeignKeys() {
		if (this.listForeignKeys == null) {
			this.listForeignKeys = new ArrayList<DynamicAttribute>();
			for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
				DynamicAttribute dynAttribute  = iterator.next();
				if (dynAttribute.isForeignKey() && dynAttribute.isMultiple()) {
					this.listForeignKeys.add(dynAttribute);
				}
			}
		}
		return listForeignKeys;
	}
	public Map<String, String> getChildren() {
		return children;
	}
	public Map<String, DynamicReference> getReferences() {
		return references;
	}

	public Map<String, String> getAcceptors() {
		return acceptors;
	}
	public void addAcceptor(String attribute, String value) {
		acceptors.put(attribute, value);		
	}
	public void addChild(String attribute, String filter) {
		children.put(attribute, filter);		
	}
	public void addObjectClasses(String objectClass) {
		objectClasses.add(objectClass);
	}
	public void addAdditionalObjectClasses(String objectClass) {
		additionalObjectClasses.add(objectClass);
	}
	
	public Attributes getLdapAttributesForCreate(DynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addBasicLdapAttributes(myAttrs, dynamicObject);
		addLdapAttributes(myAttrs, dynamicObject, false);
		return myAttrs;
	}
	
	// objectClass + naming
	public void addBasicLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject) {
		Attribute oc = new BasicAttribute("objectClass");
		for (Iterator<String> iterator = dynamicObject.getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		for (Iterator<String> iterator = dynamicObject.getModel().getAdditionalObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		myAttrs.put(oc);
		for (Iterator<DynamicAttribute> iterator = dynamicObject.getModel().getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute =  iterator.next();
			if (dynAttribute.isNaming()) {
				myAttrs.put(dynAttribute.getLdapName(), dynamicObject.getAttribute(dynAttribute.getName()));
			}			
		}
	}

	public Attributes getLdapAttributes(DynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addLdapAttributes(myAttrs, dynamicObject, true);
		return myAttrs;
	}
	
	public void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable) {

		// single attributes (except namingAttribute)
		for (Iterator<DynamicAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (nullable || value != null) {
					Attribute attribute = new BasicAttribute(dynAttribute.getLdapName(), value);
					myAttrs.put(attribute);
				}
			}
		}
		
		// multi attributes
		for (Iterator<DynamicAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Attribute attribute = new BasicAttribute(dynAttribute.getLdapName());
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null) {
					for (Iterator<String> iterator2 = list.iterator(); iterator2.hasNext();) {
						String value = iterator2.next();
						if (StringUtils.isNotEmpty(value)) {
							attribute.add(value);
						}
					}
				}
				if (nullable || attribute.size() > 0) {
					myAttrs.put(attribute);
				}
			}
		}
		
	}

	public Map<DynamicAttribute, Object> getNonMandatoryAttributes(DynamicObject dynamicObject) {
		Map<DynamicAttribute, Object> map = new HashMap<DynamicAttribute, Object>();
		// single attributes (except namingAttribute)
		for (Iterator<DynamicAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple()
					&& !dynAttribute.isReadonly() && !dynAttribute.isMandatory()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (value != null) {
					map.put(dynAttribute, value);
				}
			}
		}
		
		// multi attributes
		for (Iterator<DynamicAttribute> iterator = getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()
					&& !dynAttribute.isMandatory()) {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null && list.size() > 0) {
					map.put(dynAttribute, list);
				}
			}
		}
		
		return map;
	}

	
	public String getNamingLdapAttribute() {
		return getAttributes().get(getNamingAttribute()).getLdapName();
	}
	
	public DynamicAttribute getDynamicAttribute(String name) {
		return getAttributes().get(name);
	}
	
	public boolean childrenAllowed() {
		try {
			return (getChildren().keySet().size() > 0);
		} catch (Exception e) {
			return false;
		}
	}
	
	public void addAttribute(DynamicAttribute dynAttribute) {
		String attributeName =  dynAttribute.getName();
		this.attributes.put(attributeName, dynAttribute);
		if (dynAttribute.isNaming()) {
			this.namingAttribute = attributeName;
		}
	}
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		for (Iterator<DynamicAttribute> iterator = this.attributes.values().iterator(); iterator.hasNext();) {
			iterator.next().init(context, dynamicObject, rs);
		}
	}
	public boolean isComplete(DynamicObject dynamicObject) {
		boolean complete = true;
		// check naming attribute
		complete &= dynamicObject.attributeIsNotEmpty(getNamingAttribute());
		
		// check mandatory attributes
		for (Iterator<DynamicAttribute> iterator = getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (dynAttribute.isStatic()) {
				StaticAttribute staticAttribute = (StaticAttribute) dynAttribute;
				complete &= StringUtils.isNotEmpty(staticAttribute.getValue());
			} else {
				complete &= dynamicObject.attributeIsNotEmpty(dynAttribute.getName());
			}
		}
		
		/**
		 *  check path
		 *  - parent must exist
		 *  - new: unique
		**/
		// path must exist
		complete &= (StringUtils.isNotEmpty(dynamicObject.getPath()));

		// parent must exist
		complete &= dynamicObject.getContext().isExist(dynamicObject.getParentPath());
		
		// new: must be unique
		if (dynamicObject.isNew()) {
			complete &= !dynamicObject.getContext().isExist(dynamicObject.getPath());			
		}
		return complete;
	}
	public Map<String, DynamicAttribute> getAttributes() {
		return attributes;
	}
	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}

}
