package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Window;

	@SuppressWarnings("serial")
	public abstract class BaseTreeAction extends CustomComponent implements TreeAction, Serializable {

		private Class<?> targetClass;
		private String name;
		
		public BaseTreeAction(Class<?> classDefinition, String name) {
			this.targetClass = classDefinition;
			this.name = name;
		}

		@Override
		public abstract void execute(Window parentWindow, DynamicObject dynamicObject);

		public String getName() {
			return this.name;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}

	}
