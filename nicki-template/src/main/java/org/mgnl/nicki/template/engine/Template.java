
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class Template.
 */
public class Template extends HashMap<String, String> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7277660752803306281L;
	
	/** The name. */
	private String name;
	
	/** The data. */
	private String data;

	/**
	 * Instantiates a new template.
	 *
	 * @param name the name
	 */
	public Template(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Put part.
	 *
	 * @param partName the part name
	 * @param partValue the part value
	 */
	public void putPart(String partName, String partValue) {
		put(partName, partValue);
	}

	/**
	 * Gets the part.
	 *
	 * @param name the name
	 * @return the part
	 */
	public String getPart(String name) {
		return super.get(name);
	}

}
