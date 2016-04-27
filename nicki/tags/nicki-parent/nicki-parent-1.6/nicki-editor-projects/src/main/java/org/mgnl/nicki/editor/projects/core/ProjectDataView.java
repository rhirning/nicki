package org.mgnl.nicki.editor.projects.core;

import org.mgnl.nicki.dynamic.objects.objects.Project;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ProjectDataView extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private NativeSelect projectDeputy;
	@AutoGenerated
	private TextArea projectDescription;
	@AutoGenerated
	private TextField projectDirectory;
	@AutoGenerated
	private TextField projectName;
	
	private Project project;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param project 
	 */
	public ProjectDataView(Project project) {
		this.project = project;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		setWidth("100%");
		setHeight(600, UNITS_PIXELS);

		// TODO add user code here
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// projectName
		projectName = new TextField();
		projectName.setWidth("400px");
		projectName.setHeight("-1px");
		projectName.setCaption("Projektname");
		projectName.setReadOnly(true);
		projectName.setImmediate(false);
		mainLayout.addComponent(projectName, "top:20.0px;left:20.0px;");
		
		// projectDirectory
		projectDirectory = new TextField();
		projectDirectory.setWidth("400px");
		projectDirectory.setHeight("-1px");
		projectDirectory.setCaption("Verzeichnis");
		projectDirectory.setReadOnly(true);
		projectDirectory.setImmediate(false);
		mainLayout.addComponent(projectDirectory, "top:60.0px;left:20.0px;");
		
		// projectDescription
		projectDescription = new TextArea();
		projectDescription.setWidth("400px");
		projectDescription.setHeight("-1px");
		projectDescription.setCaption("Projektbeschreibung");
		projectDescription.setImmediate(true);
		mainLayout.addComponent(projectDescription, "top:100.0px;left:20.0px;");
		
		// projectDeputy
		projectDeputy = new NativeSelect();
		projectDeputy.setWidth("400px");
		projectDeputy.setHeight("-1px");
		projectDeputy.setCaption("Stellvertreter");
		projectDeputy.setImmediate(true);
		mainLayout.addComponent(projectDeputy, "top:220.0px;left:20.0px;");
		
		return mainLayout;
	}

}