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
package org.mgnl.nicki.vaadin.base.search;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.Query;
import org.mgnl.nicki.core.data.SearchQueryHandler;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DynamicObjectSearchDialog<T extends DynamicObject> extends CustomComponent {
	private static final Logger LOG = LoggerFactory.getLogger(DynamicObjectSearchDialog.class);

	private VerticalLayout mainLayout;
	private Button searchButton;
	private Table table;
	private BeanItemContainer<T> container;
	private Button saveButton;
	private boolean create;
	private NickiContext context;
	private Class<T> clazz;
	private DynamicObjectSearcher<T> searcher;
	private Map<DynamicAttribute, String> searchDataMap = new HashMap<DynamicAttribute, String>();

	private String baseDn;

	
	public DynamicObjectSearchDialog(NickiContext context, Class<T> clazz, DynamicObjectSearcher<T> searcher, String baseDn) throws InstantiateDynamicObjectException {
		LOG.debug("CLass: " + clazz.getName());
		this.context = context;
		this.clazz = clazz;
		this.searcher = searcher;
		this.baseDn = baseDn;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbstractLayout buildMainLayout() throws InstantiateDynamicObjectException {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setMargin(true);
		AbstractLayout searchLayout = getLayout();
		mainLayout.addComponent(searchLayout);
		DynamicObjectSearchFieldFactory<T> factory = new DynamicObjectSearchFieldFactory<T>(context, searchDataMap);
		factory.addFields(searchLayout, clazz);
		
		searchButton = new Button(I18n.getText("nicki.editor.generic.button.search"));
		searchButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				search();
			}
		});

		mainLayout.addComponent(searchButton);
		table = new Table();
		table.setSelectable(true);
		container = new BeanItemContainer<T>(clazz);
		table.setContainerDataSource(container);
		table.setVisibleColumns(getVisibleColumns());
		table.setColumnHeaders(getColumnHeaders());
		mainLayout.addComponent(table);
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				save();
			}
		});

		mainLayout.addComponent(saveButton);
		
		
		return mainLayout;
	}

	private GridLayout getLayout() {
		GridLayout layout = new GridLayout();
		layout.setColumns(2);
		layout.setWidth("100%");
		layout.setSpacing(true);
		return layout;
	}


	private String[] getColumnHeaders() {

		List<String> columnsHeaders = new ArrayList<String>();
		try {
			DataModel model = context.getDataModel(clazz);
			for (DynamicAttribute dynAttribute : model.getAttributes().values()) {
				if (dynAttribute.isSearchable()) {
					columnsHeaders.add(I18n.getText(dynAttribute.getCaption(), dynAttribute.getName()));
				}
			}
		} catch (Exception e) {
			LOG.error("Error reading datamodel", e);
		}
		return columnsHeaders.toArray(new String[]{});
	}


	private Object[] getVisibleColumns() {

		List<String> columns = new ArrayList<String>();
		try {
			DataModel model = context.getDataModel(clazz);
			for (DynamicAttribute dynAttribute : model.getAttributes().values()) {
				if (dynAttribute.isSearchable()) {
					columns.add(dynAttribute.getName());
				}
			}
		} catch (Exception e) {
			LOG.error("Error reading datamodel", e);
		}
		return columns.toArray(new Object[]{});
	}


	protected void search() {
		container.removeAllItems();
		for (SearchResultEntry entry : searchObjects()) {
			T resultEntry = context.loadObject(clazz, entry.getDn());
			container.addItem(resultEntry);
		}
	}
	


	public List<SearchResultEntry> searchObjects() {
		DataModel model;
		try {
			model = context.getDataModel(clazz);
		} catch (InstantiateDynamicObjectException e) {
			LOG.error("Error reading datamodel", e);
			return new ArrayList<SearchResultEntry>();
		}
		
		Query query = context.getQuery(baseDn);
		for (String objectClass : model.getObjectClasses()) {
			query.addSearchValue("objectclass", objectClass);
		}
		for (DynamicAttribute dynamicAttribute : searchDataMap.keySet()) {
			query.addSearchValue(dynamicAttribute.getExternalName(), searchDataMap.get(dynamicAttribute));
		}
		return getSearchResult(query);
	}


	private List<SearchResultEntry> getSearchResult(Query query) {
		try {
			SearchQueryHandler handler = context.getSearchHandler(query);
			context.search(handler);
			return handler.getResult();
		} catch (Exception e) {
			LOG.error("Error", e);
		}
		return new ArrayList<SearchResultEntry>();
	}

	public void save() {
		searcher.setDynamicObject(clazz, getSelected());
	}

	@SuppressWarnings("unchecked")
	private T getSelected() {
		return (T) table.getValue();
	}


	public boolean isCreate() {
		return create;
	}

}
