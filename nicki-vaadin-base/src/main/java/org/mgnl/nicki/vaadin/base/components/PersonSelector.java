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
package org.mgnl.nicki.vaadin.base.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.objects.SearchResultEntry;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.query.LdapSearchHandler;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class PersonSelector extends CustomComponent {
	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button closeButton;
	@AutoGenerated
	private Button selectButton;
	@AutoGenerated
	private Table searchResult;
	@AutoGenerated
	private Button searchButton;
	@AutoGenerated
	private TextField filter;
	public static final String USER_BASE = "nicki.users.basedn";
	public static final String USER_BASE_DEFAULT = "ou=users,o=utopia";
    private static String[] visibleCols = new String[] { "name"};

	PersonSelectHandler personSelectHandler;
	
	private LdapContext context;

	public PersonSelector() {
		buildMainLayout();
		filter.focus();
		setCompositionRoot(mainLayout);

		closeButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				personSelectHandler.closePersonSelector();
			}
		});

		filter.addListener(new FieldEvents.TextChangeListener() {
			
			public void textChange(TextChangeEvent event) {
				searchResult.setContainerDataSource(getBeanItems(searchPerson((String) event.getText())));
				searchResult.setVisibleColumns(visibleCols);
			}
		});
		
		searchButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				searchResult.setContainerDataSource(getBeanItems(searchPerson((String) filter.getValue())));
				searchResult.setVisibleColumns(visibleCols);
			}
		});

		selectButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				select();
			}
		});
		
	}
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public void init(LdapContext ctx, PersonSelectHandler handler) {
		this.context = ctx;
		this.personSelectHandler = handler;
	}

	private void select() {
		Person entry = (Person) searchResult.getValue();
		if (entry != null) {
			this.personSelectHandler.setSelectedPerson(entry);
			this.personSelectHandler.closePersonSelector();
		}
	}

	private Container getBeanItems(List<SearchResultEntry> resultSet) {
	    // Create a container for such beans
	    BeanItemContainer<Person> persons =
	        new BeanItemContainer<Person>(Person.class);
	    
	    for (Iterator<SearchResultEntry> iterator = resultSet.iterator(); iterator.hasNext();) {
	    	SearchResultEntry entry = iterator.next();
	    	Person person = context.loadObject(Person.class, entry.getDn());
			persons.addBean(person);
		}
		return persons;
	}

	public List<SearchResultEntry> searchPerson(String searchString) {
		if (StringUtils.isEmpty(searchString)) {
			searchString = "EMPTY";
			searchResult.setSelectable(false);
		} else {
			searchResult.setSelectable(true);
		}
		LdapQuery query = new LdapQuery(Config.getProperty(USER_BASE, USER_BASE_DEFAULT));
		query.addResultAttribute("fullName","fullName");
		query.addResultAttribute("cn","userId");
		query.addSearchValue("objectclass", "Person");
		query.addSearchValue("fullName", searchString + "*");
		return getSearchResult(query);
	}


	private List<SearchResultEntry> getSearchResult(LdapQuery query) {
		try {
			LdapSearchHandler handler = new LdapSearchHandler(context, query);
			context.search(handler);
			return handler.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<SearchResultEntry>();
	}


	
	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// filter
		filter = new TextField();
		filter.setWidth("-1px");
		filter.setHeight("-1px");
		filter.setImmediate(false);
		mainLayout.addComponent(filter, "top:20.0px;left:20.0px;");
		
		// searchButton
		searchButton = new Button();
		searchButton.setWidth("-1px");
		searchButton.setHeight("-1px");
		searchButton.setCaption("Suche");
		searchButton.setImmediate(true);
		mainLayout.addComponent(searchButton, "top:18.0px;left:180.0px;");
		
		// searchResult
		searchResult = new Table();
		searchResult.setWidth("400px");
		searchResult.setHeight("-1px");
		searchResult.setImmediate(false);
		mainLayout.addComponent(searchResult, "top:60.0px;left:20.0px;");
		
		// selectButton
		selectButton = new Button();
		selectButton.setWidth("-1px");
		selectButton.setHeight("-1px");
		selectButton.setCaption("Ausw�hlen");
		selectButton.setImmediate(true);
		mainLayout.addComponent(selectButton, "top:410.0px;left:20.0px;");
		
		// closeButton
		closeButton = new Button();
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		closeButton.setCaption("Schliessen");
		closeButton.setImmediate(true);
		mainLayout.addComponent(closeButton, "top:410.0px;left:140.0px;");
		
		return mainLayout;
	}

}
