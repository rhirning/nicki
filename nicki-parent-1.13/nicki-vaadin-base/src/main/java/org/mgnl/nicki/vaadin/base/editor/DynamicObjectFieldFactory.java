package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.Iterator;

import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.DataModel;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.fields.AttributeSelectObjectField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextAreaField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextField;
import org.mgnl.nicki.vaadin.base.fields.DynamicAttributeField;
import org.mgnl.nicki.vaadin.base.fields.TableListAttributeField;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class DynamicObjectFieldFactory implements Serializable {
	private DynamicObjectValueChangeListener objectListener = null;
	
	public DynamicObjectFieldFactory(DynamicObjectValueChangeListener objectListener) {
		this.objectListener = objectListener;
	}

	public Component createField(DynamicObject dynamicObject, String attributeName, boolean create) {
		DynamicAttribute dynAttribute = dynamicObject.getDynamicAttribute(attributeName);
		DynamicAttributeField field = null;
		if (dynAttribute.isMultiple()) {
			field = new TableListAttributeField(attributeName, dynamicObject, objectListener);
		} else if (dynAttribute.isForeignKey()) {
			field = new AttributeSelectObjectField(attributeName, dynamicObject, objectListener);
		} else if (dynAttribute.getAttributeClass() == TextArea.class) {
			field = new AttributeTextAreaField(attributeName, dynamicObject, objectListener);
		} else {
			field = new AttributeTextField(attributeName, dynamicObject, objectListener);
		}
		boolean readOnly = dynAttribute.isReadonly();
		if (!create && dynAttribute.isNaming()) {
			readOnly = true;
		}
		return field.getComponent(readOnly);
	}
	
	
	public void addFields(AbstractOrderedLayout layout, DynamicObject dynamicObject, boolean create) {
		DataModel model = dynamicObject.getModel();
		for (Iterator<DynamicAttribute> iterator = model.getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()) {
				if (objectListener == null || objectListener.acceptAttribute(dynAttribute.getName())) {
					layout.addComponent(createField(dynamicObject, dynAttribute.getName(), create));
				}
			}
		}
	}
	
}