package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.CustomComponent;

	@SuppressWarnings("serial")
	public abstract class BaseTreeAction extends CustomComponent implements TreeAction, Serializable {

		private Class<? extends DynamicObject> targetClass;
		private String name;
		
		public BaseTreeAction(Class<? extends DynamicObject> classDefinition, String name) {
			this.targetClass = classDefinition;
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public Class<? extends DynamicObject> getTargetClass() {
			return targetClass;
		}

	}
