/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.core;

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


public class JNDIWrapper implements DirContext {
	
	private Attributes attributes = new BasicAttributes(true);

	public JNDIWrapper() {
		super();
	}

	
	public void bind(Name arg0, Object arg1, Attributes arg2)
			throws NamingException {
	}

	
	public void bind(String arg0, Object arg1, Attributes arg2)
			throws NamingException {
	}

	
	public DirContext createSubcontext(Name arg0, Attributes arg1)
			throws NamingException {
		return null;
	}

	
	public DirContext createSubcontext(String arg0, Attributes arg1)
			throws NamingException {
		return null;
	}

	
	public Attributes getAttributes(Name name) throws NamingException {
		return getAttributes(name.toString());
	}

	
	public Attributes getAttributes(String name) throws NamingException {
		if (!name.equals("")) {
			throw new NameNotFoundException();
		}
		return attributes;
	}

	
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

	
	public Attributes getAttributes(Name name, String[] ids)
			throws NamingException {
		return getAttributes(name.toString(), ids);
	}

	
	public DirContext getSchema(Name arg0) throws NamingException {

		return null;
	}

	
	public DirContext getSchema(String arg0) throws NamingException {

		return null;
	}

	
	public DirContext getSchemaClassDefinition(Name arg0)
			throws NamingException {

		return null;
	}

	
	public DirContext getSchemaClassDefinition(String arg0)
			throws NamingException {

		return null;
	}

	
	public void modifyAttributes(Name arg0, ModificationItem[] arg1)
			throws NamingException {


	}

	
	public void modifyAttributes(String arg0, ModificationItem[] arg1)
			throws NamingException {


	}

	
	public void modifyAttributes(Name arg0, int arg1, Attributes arg2)
			throws NamingException {


	}

	
	public void modifyAttributes(String arg0, int arg1, Attributes arg2)
			throws NamingException {


	}

	
	public void rebind(Name arg0, Object arg1, Attributes arg2)
			throws NamingException {


	}

	
	public void rebind(String arg0, Object arg1, Attributes arg2)
			throws NamingException {


	}

	
	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1)
			throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1)
			throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1,
			String[] arg2) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1,
			String[] arg2) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
			SearchControls arg2) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(String arg0, String arg1,
			SearchControls arg2) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(Name arg0, String arg1,
			Object[] arg2, SearchControls arg3) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<SearchResult> search(String arg0, String arg1,
			Object[] arg2, SearchControls arg3) throws NamingException {

		return null;
	}

	
	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException {

		return null;
	}

	
	public void bind(Name arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	public void bind(String arg0, Object arg1) throws NamingException {


	}

	
	public void close() throws NamingException {


	}

	
	public Name composeName(Name arg0, Name arg1) throws NamingException {

		return null;
	}

	
	public String composeName(String arg0, String arg1) throws NamingException {

		return null;
	}

	
	public Context createSubcontext(Name arg0) throws NamingException {

		return null;
	}

	
	public Context createSubcontext(String arg0) throws NamingException {

		return null;
	}

	
	public void destroySubcontext(Name arg0) throws NamingException {


	}

	
	public void destroySubcontext(String arg0) throws NamingException {


	}

	
	public Hashtable<?, ?> getEnvironment() throws NamingException {

		return null;
	}

	
	public String getNameInNamespace() throws NamingException {

		return null;
	}

	
	public NameParser getNameParser(Name arg0) throws NamingException {

		return null;
	}

	
	public NameParser getNameParser(String arg0) throws NamingException {

		return null;
	}

	
	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {

		return null;
	}

	
	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {

		return null;
	}

	
	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {

		return null;
	}

	
	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {

		return null;
	}

	
	public Object lookup(Name arg0) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	public Object lookup(String arg0) throws NamingException {
		throw new OperationNotSupportedException();
	}

	
	public Object lookupLink(Name arg0) throws NamingException {

		return null;
	}

	
	public Object lookupLink(String arg0) throws NamingException {

		return null;
	}

	
	public void rebind(Name arg0, Object arg1) throws NamingException {


	}

	
	public void rebind(String arg0, Object arg1) throws NamingException {


	}

	
	public Object removeFromEnvironment(String arg0) throws NamingException {

		return null;
	}

	
	public void rename(Name arg0, Name arg1) throws NamingException {


	}

	
	public void rename(String arg0, String arg1) throws NamingException {


	}

	
	public void unbind(Name arg0) throws NamingException {


	}

	
	public void unbind(String arg0) throws NamingException {


	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.put(attribute);
	}
	
	public void addAttribute(String name, String value) {
		this.attributes.put(name, value);
	}



}
