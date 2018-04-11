package org.mgnl.nicki.vaadin.base.views;

import org.mgnl.nicki.dynamic.objects.objects.Template;

/**
 * 
 * @author Ralf Hirning
 * 
 * configuration:
 * 
 * targetName (optional)		Name of Target where to store the script
 * configPath					Config key for Script path
 * 
 *
 */
public class TemplateInfo extends DynamicObjectInfo<Template> implements InfoStore {

	public TemplateInfo() {
		super(Template.class);
	}

	@Override
	protected String getAttributeName() {
		return "data";
	}

}
