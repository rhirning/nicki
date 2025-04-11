package org.mgnl.nicki.core.data;

// TODO: Auto-generated Javadoc
/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

/**
 * The Class TreeObjectWrapper.
 *
 * @param <T> the generic type
 */
public class TreeObjectWrapper<T extends TreeObject> implements TreeObject {
	
	/** The object. */
	private T object;
	
	
	/**
	 * Instantiates a new tree object wrapper.
	 *
	 * @param object the object
	 */
	public TreeObjectWrapper(T object) {
		this.object = object;
	}


	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
	public String getDisplayName() {
		return object.getDisplayName();
	}
	
	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public TreeObject getObject() {
		return object;
	}

}
