
package org.mgnl.nicki.core.data;

/*-
 * #%L
 * nicki-core
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


import java.util.Collection;
import org.mgnl.nicki.core.context.NickiContext;



/**
 * The Interface DataProvider.
 *
 * @param <T> the generic type
 */
public interface DataProvider<T> {

	/**
	 * Gets the children.
	 *
	 * @param context the context
	 * @return the children
	 */
	Collection<? extends T> getChildren(NickiContext context);
	
	/**
	 * Gets the children.
	 *
	 * @param parent the parent
	 * @return the children
	 */
	Collection<? extends T> getChildren(T parent);
	
	/**
	 * Gets the root.
	 *
	 * @param context the context
	 * @return the root
	 */
	T getRoot(NickiContext context);
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	String getMessage();
	
	/**
	 * Gets the entry filter.
	 *
	 * @return the entry filter
	 */
	EntryFilter getEntryFilter();

}
