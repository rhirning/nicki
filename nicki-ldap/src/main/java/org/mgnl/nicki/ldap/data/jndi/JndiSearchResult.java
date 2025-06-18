
package org.mgnl.nicki.ldap.data.jndi;

/*-
 * #%L
 * nicki-ldap
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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import org.mgnl.nicki.core.objects.ContextAttributes;
import org.mgnl.nicki.core.objects.ContextSearchResult;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class JndiSearchResult.
 */
@Slf4j
public class JndiSearchResult implements ContextSearchResult {
	
	/** The rs. */
	private SearchResult rs;

	/**
	 * Instantiates a new jndi search result.
	 *
	 * @param rs the rs
	 */
	public JndiSearchResult(SearchResult rs) {
		this.rs = rs;
	}

	/**
	 * Gets the name in namespace.
	 *
	 * @return the name in namespace
	 */
	@Override
	public String getNameInNamespace() {
		return rs.getNameInNamespace();
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public ContextAttributes getAttributes() {
		return new JndiAttributes(rs.getAttributes());
	}

	/**
	 * Gets the value.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @return the value
	 */
	@Override
	public Object getValue(Class<?> clazz, String name) {
		try {
			Attribute attribute = rs.getAttributes().get(name);

			if (attribute != null) {
				Object o = attribute.get();
				log.debug("getValue " + name + ":" + clazz.getName() + ":" + o);
				if (clazz == byte[].class && o instanceof String) {
					return ((String) o).getBytes();
				}
				return o;
			}
		} catch (Exception e) {
			log.error("Error retrieving " + name, e);
		}
		if (clazz == String.class) {
			return "";
		}
		return null;
	}

	/**
	 * Gets the values.
	 *
	 * @param name the name
	 * @return the values
	 */
	@Override
	public List<Object> getValues(String name) {
		List<Object> list = new ArrayList<>();
		try {
			for (NamingEnumeration<?> iterator = rs.getAttributes().get(name).getAll(); iterator.hasMoreElements();) {
				list.add(iterator.next());
			}
		} catch (Exception e) {
			// nothing to do
		}
		return list;
	}

	/**
	 * Checks for attribute.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	@Override
	public boolean hasAttribute(String name) {
		return rs.getAttributes().get(name) != null;
	}

}
