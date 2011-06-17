package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.Iterator;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DataModel;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.fields.AttributeSelectField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextField;
import org.mgnl.nicki.vaadin.base.fields.ListAttributeField;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
class DynamicObjectFieldFactory implements Serializable {
	private DynamicObjectValueChangeListener objectListener = null;
	private NickiContext context;
	
	public DynamicObjectFieldFactory(NickiContext context, DynamicObjectValueChangeListener objectListener) {
		this.context = context;
		this.objectListener = objectListener;
	}

	public Component createField(DynamicObject dynamicObject, String attributeName, boolean create) {
		DynamicAttribute dynAttribute = dynamicObject.getDynamicAttribute(attributeName);
		if (dynAttribute.isMultiple()) {
			return new ListAttributeField(attributeName, dynamicObject, objectListener).getContainer();
		} else if (dynAttribute.isForeignKey()) {
			return new AttributeSelectField(context, attributeName, dynamicObject, objectListener).getField();
		} else {
			boolean readOnly = false;
			if (!create && dynAttribute.isNaming()) {
				readOnly = true;
			}
			return new AttributeTextField(attributeName, dynamicObject, objectListener).getField(readOnly);
		}
	}
	
	
	public void addFields(VerticalLayout layout, DynamicObject dynamicObject, boolean create) {
		DataModel model = dynamicObject.getModel();
		for (Iterator<DynamicAttribute> iterator = model.getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (objectListener == null || objectListener.acceptAttribute(dynAttribute.getName())) {
				layout.addComponent(createField(dynamicObject, dynAttribute.getName(), create));
			}
		}
	}
	
}