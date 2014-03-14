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
package org.mgnl.nicki.editor.jcr;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.editor.jcr.PropertyWrapper.PROPERTY_TYPE;
import org.mgnl.nicki.jcr.objects.JcrDynamicObject;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class NodeViewer extends CustomComponent implements ClassEditor {
	private static final Logger LOG = LoggerFactory.getLogger(NodeViewer.class);
	
	static final Action ACTION_NEW = new Action("New");
    static final Action ACTION_EDIT = new Action("Edit");
    static final Action[] ACTIONS_NEW = new Action[] { ACTION_NEW };
    static final Action[] ACTIONS_EDIT = new Action[] { ACTION_EDIT };


	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Table table;
	@AutoGenerated
	private Button saveButton;
	
	private Window editor;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	private HttpServletRequest request;
	private HttpServletResponse response;
	private JcrDynamicObject node;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public NodeViewer() {
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.node = (JcrDynamicObject) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		initTable();
		table.setSelectable(true);

		table.addActionHandler(new Action.Handler() {
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (ACTION_NEW == action) {
					addProperty();
	            } else if (ACTION_EDIT == action) {
	                editProperty(target);
	            }
			
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {
				if (target != null) {
					return ACTIONS_EDIT;
				} else {
					return ACTIONS_NEW;
				}
			}
		});

		saveButton.addClickListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					save();
				} catch (Exception e) {
					LOG.error("Error", e);
				}
			}
		});

	}
	


	private void initTable() {
		try {
			//properties.setContainerDataSource(new NodeContainer(node));
			table.setContainerDataSource(getBeanItems(this.node));
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
	}

	protected void editProperty(Object target) {
		editor = null;
		editor = new Window("New Property", new PropertyEditor(this, (PropertyWrapper) target)); 
		editor.setModal(true);
		getUI().addWindow(editor);
	}

	protected void addProperty() {
		
		editor = null;
		editor = new Window("New Property", new PropertyEditor(this, null)); 
		editor.setModal(true);
		getUI().addWindow(editor);
		
	}

	private Container getBeanItems(JcrDynamicObject dynamicObject) throws RepositoryException {
	    // Create a container for such beans
	    BeanItemContainer<PropertyWrapper> properties =
	        new BeanItemContainer<PropertyWrapper>(PropertyWrapper.class);
	    
	    for (PropertyIterator iterator = dynamicObject.getNode().getProperties(); iterator.hasNext();) {
	    	Property entry = (Property) iterator.next();
	    	properties.addBean(new PropertyWrapper(entry));
		}
		return properties;
	}

	public void save() throws DynamicObjectException, NamingException {
		this.node.update();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// saveButton
		saveButton = new Button();
		saveButton.setCaption("Save");
		saveButton.setImmediate(true);
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		mainLayout.addComponent(saveButton, "top:400.0px;left:20.0px;");
		
		// table
		table = new Table();
		table.setImmediate(false);
		table.setWidth("100%");
		table.setHeight("-1px");
		mainLayout.addComponent(table, "top:20.0px;left:20.0px;");
		
		return mainLayout;
	}

	public void create(String name, PROPERTY_TYPE type, String value) {
		try {
			type.setProperty(this.node.getNode(), name, value);
			initTable();
			editor.close();
			editor = null;
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

}
