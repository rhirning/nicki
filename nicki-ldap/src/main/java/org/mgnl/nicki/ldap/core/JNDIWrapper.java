
package org.mgnl.nicki.ldap.core;

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


import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;



/**
 * The Class JNDIWrapper.
 */
public class JNDIWrapper implements DirContext {
	
	/** The attributes. */
	private Attributes attributes = new BasicAttributes(true);

	/**
	 * Instantiates a new JNDI wrapper.
	 */
	public JNDIWrapper() {
		super();
	}

	
	/**
	 * Bind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void bind(Name arg0, Object arg1, Attributes arg2)
			throws NamingException {
	}

	
	/**
	 * Bind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void bind(String arg0, Object arg1, Attributes arg2)
			throws NamingException {
	}

	
	/**
	 * Creates the subcontext.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the dir context
	 * @throws NamingException the naming exception
	 */
	public DirContext createSubcontext(Name arg0, Attributes arg1)
			throws NamingException {
		return null;
	}

	
	/**
	 * Creates the subcontext.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the dir context
	 * @throws NamingException the naming exception
	 */
	public DirContext createSubcontext(String arg0, Attributes arg1)
			throws NamingException {
		return null;
	}

	
	/**
	 * Gets the attributes.
	 *
	 * @param name the name
	 * @return the attributes
	 * @throws NamingException the naming exception
	 */
	public Attributes getAttributes(Name name) throws NamingException {
		return getAttributes(name.toString());
	}

	
	/**
	 * Gets the attributes.
	 *
	 * @param name the name
	 * @return the attributes
	 * @throws NamingException the naming exception
	 */
	public Attributes getAttributes(String name) throws NamingException {
		if (!name.equals("")) {
			throw new NameNotFoundException();
		}
		return attributes;
	}

	
	/**
	 * Gets the attributes.
	 *
	 * @param name the name
	 * @param ids the ids
	 * @return the attributes
	 * @throws NamingException the naming exception
	 */
	public Attributes getAttributes(String name, String[] ids)
			throws NamingException {
		if (!name.equals("")) {
			throw new NameNotFoundException();
		}
		Attributes answer = new BasicAttributes(true);
		Attribute target;
		for (int i = 0; i < ids.length; i++) {
			target = attributes.get(ids[i]);
			if (target != null) {
				answer.put(target);
			}
		}
		return answer;
	}

	
	/**
	 * Gets the attributes.
	 *
	 * @param name the name
	 * @param ids the ids
	 * @return the attributes
	 * @throws NamingException the naming exception
	 */
	public Attributes getAttributes(Name name, String[] ids)
			throws NamingException {
		return getAttributes(name.toString(), ids);
	}

	
	/**
	 * Gets the schema.
	 *
	 * @param arg0 the arg 0
	 * @return the schema
	 * @throws NamingException the naming exception
	 */
	public DirContext getSchema(Name arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Gets the schema.
	 *
	 * @param arg0 the arg 0
	 * @return the schema
	 * @throws NamingException the naming exception
	 */
	public DirContext getSchema(String arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Gets the schema class definition.
	 *
	 * @param arg0 the arg 0
	 * @return the schema class definition
	 * @throws NamingException the naming exception
	 */
	public DirContext getSchemaClassDefinition(Name arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * Gets the schema class definition.
	 *
	 * @param arg0 the arg 0
	 * @return the schema class definition
	 * @throws NamingException the naming exception
	 */
	public DirContext getSchemaClassDefinition(String arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * Modify attributes.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void modifyAttributes(Name arg0, ModificationItem[] arg1)
			throws NamingException {


	}

	
	/**
	 * Modify attributes.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void modifyAttributes(String arg0, ModificationItem[] arg1)
			throws NamingException {


	}

	
	/**
	 * Modify attributes.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void modifyAttributes(Name arg0, int arg1, Attributes arg2)
			throws NamingException {


	}

	
	/**
	 * Modify attributes.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void modifyAttributes(String arg0, int arg1, Attributes arg2)
			throws NamingException {


	}

	
	/**
	 * Rebind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void rebind(Name arg0, Object arg1, Attributes arg2)
			throws NamingException {


	}

	
	/**
	 * Rebind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @throws NamingException the naming exception
	 */
	public void rebind(String arg0, Object arg1, Attributes arg2)
			throws NamingException {


	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1)
			throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1)
			throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1,
			String[] arg2) throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1,
			String[] arg2) throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
			SearchControls arg2) throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(String arg0, String arg1,
			SearchControls arg2) throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @param arg3 the arg 3
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
			Object[] arg2, SearchControls arg3) throws NamingException {

		return null;
	}

	
	/**
	 * Search.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @param arg3 the arg 3
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<SearchResult> search(String arg0, String arg1,
			Object[] arg2, SearchControls arg3) throws NamingException {

		return null;
	}

	
	/**
	 * Adds the to environment.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException {

		return null;
	}

	
	/**
	 * Bind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void bind(Name arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	/**
	 * Bind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void bind(String arg0, Object arg1) throws NamingException {


	}

	
	/**
	 * Close.
	 *
	 * @throws NamingException the naming exception
	 */
	public void close() throws NamingException {


	}

	
	/**
	 * Compose name.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the name
	 * @throws NamingException the naming exception
	 */
	public Name composeName(Name arg0, Name arg1) throws NamingException {

		return null;
	}

	
	/**
	 * Compose name.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return the string
	 * @throws NamingException the naming exception
	 */
	public String composeName(String arg0, String arg1) throws NamingException {

		return null;
	}

	
	/**
	 * Creates the subcontext.
	 *
	 * @param arg0 the arg 0
	 * @return the context
	 * @throws NamingException the naming exception
	 */
	public Context createSubcontext(Name arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Creates the subcontext.
	 *
	 * @param arg0 the arg 0
	 * @return the context
	 * @throws NamingException the naming exception
	 */
	public Context createSubcontext(String arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Destroy subcontext.
	 *
	 * @param arg0 the arg 0
	 * @throws NamingException the naming exception
	 */
	public void destroySubcontext(Name arg0) throws NamingException {


	}

	
	/**
	 * Destroy subcontext.
	 *
	 * @param arg0 the arg 0
	 * @throws NamingException the naming exception
	 */
	public void destroySubcontext(String arg0) throws NamingException {


	}

	
	/**
	 * Gets the environment.
	 *
	 * @return the environment
	 * @throws NamingException the naming exception
	 */
	public Hashtable<?, ?> getEnvironment() throws NamingException {

		return null;
	}

	
	/**
	 * Gets the name in namespace.
	 *
	 * @return the name in namespace
	 * @throws NamingException the naming exception
	 */
	public String getNameInNamespace() throws NamingException {

		return null;
	}

	
	/**
	 * Gets the name parser.
	 *
	 * @param arg0 the arg 0
	 * @return the name parser
	 * @throws NamingException the naming exception
	 */
	public NameParser getNameParser(Name arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Gets the name parser.
	 *
	 * @param arg0 the arg 0
	 * @return the name parser
	 * @throws NamingException the naming exception
	 */
	public NameParser getNameParser(String arg0) throws NamingException {

		return null;
	}

	
	/**
	 * List.
	 *
	 * @param arg0 the arg 0
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * List.
	 *
	 * @param arg0 the arg 0
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * List bindings.
	 *
	 * @param arg0 the arg 0
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * List bindings.
	 *
	 * @param arg0 the arg 0
	 * @return the naming enumeration
	 * @throws NamingException the naming exception
	 */
	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {

		return null;
	}

	
	/**
	 * Lookup.
	 *
	 * @param arg0 the arg 0
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object lookup(Name arg0) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	/**
	 * Lookup.
	 *
	 * @param arg0 the arg 0
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object lookup(String arg0) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	/**
	 * Lookup link.
	 *
	 * @param arg0 the arg 0
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object lookupLink(Name arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Lookup link.
	 *
	 * @param arg0 the arg 0
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object lookupLink(String arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Rebind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void rebind(Name arg0, Object arg1) throws NamingException {


	}

	
	/**
	 * Rebind.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void rebind(String arg0, Object arg1) throws NamingException {


	}

	
	/**
	 * Removes the from environment.
	 *
	 * @param arg0 the arg 0
	 * @return the object
	 * @throws NamingException the naming exception
	 */
	public Object removeFromEnvironment(String arg0) throws NamingException {

		return null;
	}

	
	/**
	 * Rename.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void rename(Name arg0, Name arg1) throws NamingException {


	}

	
	/**
	 * Rename.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @throws NamingException the naming exception
	 */
	public void rename(String arg0, String arg1) throws NamingException {


	}

	
	/**
	 * Unbind.
	 *
	 * @param arg0 the arg 0
	 * @throws NamingException the naming exception
	 */
	public void unbind(Name arg0) throws NamingException {


	}

	
	/**
	 * Unbind.
	 *
	 * @param arg0 the arg 0
	 * @throws NamingException the naming exception
	 */
	public void unbind(String arg0) throws NamingException {


	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the new attributes
	 */
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * Adds the attribute.
	 *
	 * @param attribute the attribute
	 */
	public void addAttribute(Attribute attribute) {
		this.attributes.put(attribute);
	}
	
	/**
	 * Adds the attribute.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void addAttribute(String name, String value) {
		this.attributes.put(name, value);
	}



}
