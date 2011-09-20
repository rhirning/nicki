package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
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

	public Component createField(Component parent, DynamicObject dynamicObject, String attributeName, boolean create) {
		DynamicAttribute dynAttribute = dynamicObject.getDynamicAttribute(attributeName);
		DynamicAttributeField field = null;
		if (StringUtils.isNotEmpty(dynAttribute.getEditorClass())) {
			try {
				field = (DynamicAttributeField) Class.forName(dynAttribute.getEditorClass()).newInstance();
				field.init(attributeName, dynamicObject, objectListener);
			} catch (Exception e) {
				field = null;
				e.printStackTrace();
			}
		}
		if (field == null) {
			if (dynAttribute.isMultiple()) {
				field = new TableListAttributeField();
			} else if (dynAttribute.isForeignKey()) {
				field = new AttributeSelectObjectField();
			} else if (dynAttribute.getAttributeClass() == TextArea.class) {
				field = new AttributeTextAreaField();
			} else {
				field = new AttributeTextField();
			}
			field.init(attributeName, dynamicObject, objectListener);
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
					layout.addComponent(createField(layout, dynamicObject, dynAttribute.getName(), create));
				}
			}
		}
	}
	
}