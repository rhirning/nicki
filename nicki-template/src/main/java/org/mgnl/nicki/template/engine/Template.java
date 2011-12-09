/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;

import java.util.HashMap;

public class Template extends HashMap<String, String> {

	private static final long serialVersionUID = 7277660752803306281L;
	private String name = null;;

	public Template(String name) {
		this.name = name;
		// TODO Auto-generated constructor stub
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

	private String data = null;

	public void putPart(String partName, String partValue) {
		put(partName, partValue);
	}

	public String getPart(String name) {
		return super.get(name);
	}

}
