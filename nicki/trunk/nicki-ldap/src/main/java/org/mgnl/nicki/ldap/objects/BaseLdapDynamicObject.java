/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.methods.StructuredData;

@SuppressWarnings("serial")
public abstract class BaseLdapDynamicObject extends BaseDynamicObject implements DynamicObject, Serializable, Cloneable {
	public static final String ATTRIBUTE_NAME = "name";
	public static final String SEPARATOR = "/";
	
	private LdapDataModel model = null;
	
	protected BaseLdapDynamicObject() {
		// removed: must be called in TargetObjectFactory
		//		initDataModel();
	}
	
	@Override
	public void initNew(String parentPath, String namingValue) {
		this.setStatus(STATUS.NEW);
		setParentPath(parentPath);
		setPath(LdapHelper.getPath(parentPath, getModel().getNamingLdapAttribute(), namingValue));
		put(getModel().getNamingAttribute(), namingValue);
	}

	@Override
	public String getNamingValue() {
		return getAttribute(getModel().getNamingAttribute());
	}

	@Override
	public String getParentPath() {
		return LdapHelper.getParentPath(getPath());
	}

	@Override
	public void initExisting(NickiContext context, String path) {
		this.setStatus(STATUS.EXISTS);
		setContext(context);
		setPath(path);
		setParentPath(LdapHelper.getParentPath(getPath()));

		getMap().put(getModel().getNamingAttribute(), LdapHelper.getNamingValue(path));
	}

	public void init(ContextSearchResult rs) throws DynamicObjectException {
		if (getStatus() != STATUS.EXISTS) {
			throw new DynamicObjectException("Invalid call");
		}
		this.setStatus(STATUS.LOADED);
		this.getModel().init(getContext(), this, rs);
		setOriginal((DynamicObject) this.clone());
	}

	public boolean accept(ContextSearchResult rs) {
		boolean accepted = true;
		for (Iterator<String> iterator = getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			accepted &= checkAttribute(rs,"objectClass", objectClass);
		}
		return accepted;
	}

	private boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value) {
		try {
			for (Object attributeValue : rs.getValues(attribute)) {
				if (StringUtils.equalsIgnoreCase(value, (String) attributeValue)) {
					return true;
				
			}
		}
		} catch (Exception e) {
		}
		return false;
	}

	public void loadChildren() {
		init();
		if (getChildObjects() == null) {
			initChildren();
			for (Iterator<String> iterator = getModel().getChildren().keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				String filter = getModel().getChildren().get(key);
				@SuppressWarnings("unchecked")
				List<DynamicObject> list = (List<DynamicObject>) getContext().loadChildObjects(getPath(), filter);
				if (list != null) {
					getChildObjects().put(key, list);
				}
			}
		}
	}
	// Schema

	public void merge(Map<DynamicAttribute, Object> changeAttributes) {
		for (Iterator<DynamicAttribute> iterator = changeAttributes.keySet().iterator(); iterator.hasNext();) {
			DynamicAttribute dynamicAttribute = iterator.next();
			put(dynamicAttribute.getName(), changeAttributes.get(dynamicAttribute));
		}
	};

	public String getInfo(String xml, String infoPath) {
		String parts[] = StringUtils.split(infoPath, SEPARATOR);
		if (parts.length < 2) {
			return null;
		}
		// correct xml
		StringUtils.replace(xml, "&lt;", "<");
		StringUtils.replace(xml, "&gt;", ">");
		StructuredData data  = new StructuredData(xml); 
		try {
			Element element = data.getDocument().getRootElement();
			int i = 1;
			while (i < parts.length - 1) {
				element = element.getChild(parts[i]);
				i++;
			}
			
			return element.getChildTextTrim(parts[i]);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getLocalizedValue(String attributeName, String locale) {
		Map<String, String> valueMap = DataHelper.getMap(getAttribute(attributeName), "|", "~");
		if (valueMap.size() == 0) {
			return null;
		} else if (valueMap.containsKey(locale)) {
			return valueMap.get(locale);
		} else {
			return valueMap.values().iterator().next();
		}
	}
	
	public String toString() {
		return getDisplayName();
	}

	@Override
	public void setModel(DataModel model) {
		this.model = (LdapDataModel) model;
	}

	@Override
	public LdapDataModel getModel() {
		if (model == null) {
			model = new LdapDataModel();
		}
		return model;
	}
	
	@Override
	public String getPath(String parentPath, String name) {
		return LdapHelper.getPath(parentPath, getModel().getNamingLdapAttribute(), name);
	}

}
