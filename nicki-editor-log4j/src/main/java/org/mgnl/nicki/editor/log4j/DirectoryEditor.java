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
package org.mgnl.nicki.editor.log4j;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.mgnl.nicki.core.data.FileEntry;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;
import org.mgnl.nicki.vaadin.base.helper.ContainerHelper;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DirectoryEditor extends CustomComponent implements ClassEditor {
	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private HorizontalSplitPanel horizontalSplitPanel;

	@AutoGenerated
	private VerticalLayout propertiesLayout;

	@AutoGenerated
	private VerticalLayout tableLayout;

	@AutoGenerated
	private Table table;

	private FileEntry fileEntry;


	public void setDynamicObject(NickiTreeEditor nickiEditor,
			TreeData dynamicObject) {
		this.fileEntry = (FileEntry) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		if (this.fileEntry != null && this.fileEntry.getFile() != null) {
			File list[] = this.fileEntry.getFile().listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});
			if (list != null && list.length > 0) {
				Collection<File> files = Arrays.asList(list);
				
				BeanItemContainer<File> container = new BeanItemContainer<>(File.class, files);
				table.setContainerDataSource(container);
				table.setVisibleColumns("name");
				table.setSelectable(true);
				table.setImmediate(true);
				table.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						File itemId = (File) event.getProperty().getValue();
						showItem(itemId);
					}
				});
			}
		}
	}

	protected void showItem(final File file) {
		clearItem();
		VerticalLayout topLayout = new VerticalLayout();
		propertiesLayout.addComponent(topLayout);
		Table propertiesTable = new Table();
		propertiesTable.setWidth("100%");
		propertiesTable.setHeight("100%");
		String[] propertiesNames = {"name", "path", "size", "lastModified", "mod"};
		Container propertiesContainer = ContainerHelper.getDataContainer(new FileWrapper(file), propertiesNames , null);
		propertiesTable.setContainerDataSource(propertiesContainer);
		propertiesTable.setPageLength(propertiesContainer.size());
		topLayout.addComponent(propertiesTable);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		topLayout.addComponent(buttonLayout);

		Button viewButton = new Button("View");
		buttonLayout.addComponent(viewButton);

		Button downloadButton = new Button("Download");
		buttonLayout.addComponent(downloadButton);
		FileResource fileResource = new FileResource(file);
		StreamResource streamResource = new StreamResource(fileResource, file.getName());
		streamResource.setCacheTime(0);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadButton);
		
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setHeight("100%");
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setWidth("100%");
		panelLayout.setHeight("100%");
		panel.setContent(panelLayout);
		
		final TextArea textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setHeight("100%");
		panelLayout.addComponent(textArea);
		panelLayout.setExpandRatio(textArea, 1.f);
		
		
		propertiesLayout.addComponent(panel);
		propertiesLayout.setExpandRatio(panel, 1.f);
		viewButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					textArea.setValue(FileUtils.readFileToString(file));
				} catch (ReadOnlyException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		

	}

	protected void clearItem() {
		this.propertiesLayout.removeAllComponents();
	}

	public void save() throws DynamicObjectException, NamingException {
	}


	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// horizontalSplitPanel
		horizontalSplitPanel = buildHorizontalSplitPanel();
		mainLayout.addComponent(horizontalSplitPanel);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalSplitPanel buildHorizontalSplitPanel() {
		// common part: create layout
		horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setImmediate(false);
		horizontalSplitPanel.setWidth("100.0%");
		horizontalSplitPanel.setHeight("100.0%");
		
		// tableLayout
		tableLayout = buildTableLayout();
		horizontalSplitPanel.addComponent(tableLayout);
		
		// propertiesLayout
		propertiesLayout = buildPropertiesLayout();
		horizontalSplitPanel.addComponent(propertiesLayout);
		
		return horizontalSplitPanel;
	}

	@AutoGenerated
	private VerticalLayout buildTableLayout() {
		// common part: create layout
		tableLayout = new VerticalLayout();
		tableLayout.setImmediate(false);
		tableLayout.setWidth("100.0%");
		tableLayout.setHeight("100.0%");
		tableLayout.setMargin(false);
		
		// table_1
		table = new Table();
		table.setImmediate(false);
		table.setWidth("100.0%");
		table.setHeight("100.0%");
		tableLayout.addComponent(table);
		tableLayout.setExpandRatio(table, 1.0f);
		
		return tableLayout;
	}

	@AutoGenerated
	private VerticalLayout buildPropertiesLayout() {
		// common part: create layout
		propertiesLayout = new VerticalLayout();
		propertiesLayout.setImmediate(false);
		propertiesLayout.setWidth("100.0%");
		propertiesLayout.setHeight("100.0%");
		propertiesLayout.setMargin(false);

		
		return propertiesLayout;
	}

}
