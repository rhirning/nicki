package org.mgnl.nicki.editor.projects.core;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.NickiEditor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class ProjectViewer extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private TabSheet tabSheet;
	private Project project;

	private ClassEditor dataView = new ProjectDataView();

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public ProjectViewer() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// Project main data
		tabSheet.addTab(dataView, I18n.getText("nicki.editor.projects.tab.data.title"), Icon.USERS.getResource());
	}
	
	@Override
	public void setDynamicObject(NickiEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.project = (Project) dynamicObject;
		dataView.setDynamicObject(nickiEditor, dynamicObject);
	}


	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// tabSheet
		tabSheet = new TabSheet();
		tabSheet.setWidth("100.0%");
		tabSheet.setHeight("100.0%");
		tabSheet.setImmediate(false);
		mainLayout.addComponent(tabSheet,
				"top:20.0px;right:20.0px;bottom:20.0px;left:20.0px;");
		
		return mainLayout;
	}


}
