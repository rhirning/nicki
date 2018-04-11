package org.mgnl.nicki.vaadin.base.views;

import org.mgnl.nicki.dynamic.objects.objects.Script;

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
public class ScriptInfo extends DynamicObjectInfo<Script> implements InfoStore {

	public ScriptInfo() {
		super(Script.class);
	}

	@Override
	protected String getAttributeName() {
		return "data";
	}

}
