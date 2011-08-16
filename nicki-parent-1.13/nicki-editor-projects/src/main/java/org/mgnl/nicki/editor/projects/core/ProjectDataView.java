package org.mgnl.nicki.editor.projects.core;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;
import org.mgnl.nicki.vaadin.base.fields.AttributeSelectObjectField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextAreaField;
import org.mgnl.nicki.vaadin.base.fields.AttributeTextField;
import org.mgnl.nicki.vaadin.base.fields.DynamicAttributeField;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ProjectDataView extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Select projectDeputy;
	@AutoGenerated
	private Layout projectDescription;
	@AutoGenerated
	private TextField projectDirectory;
	@AutoGenerated
	private TextField projectName;
	
	private DynamicAttributeField projectNameField;
	private DynamicAttributeField projectDirectoryField;
	private DynamicAttributeField projectDescriptionField;
	private DynamicAttributeField deputyField;
	private Button saveButton;
	
	private Project project;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param project 
	 */
	public ProjectDataView() {
	}

	@Override
	public void setDynamicObject(NickiTreeEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.project = (Project) dynamicObject;
		this.projectNameField = new AttributeTextField("name", project, null);
		this.projectDirectoryField = new AttributeTextField("projectdirectory", project, null);
		this.projectDescriptionField = new AttributeTextAreaField("description", project, null);
		this.deputyField = new AttributeSelectObjectField("deputy", project, null);
		
		buildMainLayout();
		setCompositionRoot(mainLayout);
		setWidth("100%");
		setHeight(400, UNITS_PIXELS);
		if (project.isProjectDeputyLeader(nickiEditor.getNickiContext().getUser())) {
			this.projectDeputy.setReadOnly(true);
		}
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// projectName
		projectName = (TextField) this.projectNameField.getComponent(true);
		projectName.setWidth("400px");
		projectName.setCaption("Projektname");
		mainLayout.addComponent(projectName, "top:20.0px;left:20.0px;");
		
		// projectDirectory
		projectDirectory = (TextField) this.projectDirectoryField.getComponent(true);
		projectDirectory.setWidth("400px");
		projectDirectory.setCaption("Verzeichnis");
		mainLayout.addComponent(projectDirectory, "top:60.0px;left:20.0px;");
		
		// projectDescription
		projectDescription = (Layout) this.projectDescriptionField.getComponent(false);;
//		projectDescription.setWidth("400px");
		projectDescription.setCaption("Projektbeschreibung");
		mainLayout.addComponent(projectDescription, "top:100.0px;left:20.0px;");
		
		// projectDeputy
		projectDeputy = (Select) this.deputyField.getComponent(false);
		projectDeputy.setWidth("400px");
		projectDeputy.setHeight("-1px");
		projectDeputy.setCaption("Stellvertreter");
		projectDeputy.setImmediate(true);
		mainLayout.addComponent(projectDeputy, "top:220.0px;left:20.0px;");
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					project.update();
					getWindow().showNotification(I18n.getText("nicki.editor.save.info"));
				} catch (DynamicObjectException e) {
					getWindow().showNotification(I18n.getText("nicki.editor.save.error"), 
							e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		mainLayout.addComponent(saveButton, "top:260.0px;left:20.0px;");

		
		return mainLayout;
	}

}
