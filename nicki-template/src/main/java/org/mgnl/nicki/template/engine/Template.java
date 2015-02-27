/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.engine;

import java.util.HashMap;

public class Template extends HashMap<String, String> {

	private static final long serialVersionUID = 7277660752803306281L;
	private String name;
	private String data;

	public Template(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void putPart(String partName, String partValue) {
		put(partName, partValue);
	}

	public String getPart(String name) {
		return super.get(name);
	}

}
