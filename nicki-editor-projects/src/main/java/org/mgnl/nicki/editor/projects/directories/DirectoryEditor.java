package org.mgnl.nicki.editor.projects.directories;

import java.util.Iterator;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Directory;
import org.mgnl.nicki.dynamic.objects.objects.Member;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiEditor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DirectoryEditor extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private VerticalLayout verticalLayout_3;
	@AutoGenerated
	private Button saveButton;
	@AutoGenerated
	private Label label_dummy;
	@AutoGenerated
	private Panel members;
	@AutoGenerated
	private VerticalLayout verticalLayout_1;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;
	@AutoGenerated
	private Label name;
	private NickiEditor nickiEditor;
	private Directory directory;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	@Deprecated
	public DirectoryEditor(Project project, Directory target, EditDirectoryHandler handler) {
//		this.editDirectoryHandler = handler;
		this.directory = target;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		name.setValue(this.directory.getDisplayName());
		for (Iterator<DynamicObject> iterator = project.getMembers().iterator(); iterator.hasNext();) {
			Member member = (Member) iterator.next();
			MemberComponent comp = new MemberComponent(target, member);
			members.addComponent(comp);
		}
		
	}

	public void setDynamicObject(NickiEditor nickiEditor, DynamicObject dynamicObject) {
		this.nickiEditor = nickiEditor;
		this.directory = (Directory) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		name.setValue(this.directory.getDisplayName());
		for (Iterator<DynamicObject> iterator = getProject().getMembers().iterator(); iterator.hasNext();) {
			Member member = (Member) iterator.next();
			MemberComponent comp = new MemberComponent(directory, member);
			members.addComponent(comp);
		}
		saveButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				save();
			
			}
		});

	}
	
	protected void save() {
		for (Iterator<Component> iterator = members.getComponentIterator(); iterator.hasNext();) {
			Component component= iterator.next();
			if (component instanceof MemberComponent) {
				MemberComponent memberComponent = (MemberComponent) component;
				memberComponent.save();
				try {
					memberComponent.getMember().update();
					getWindow().showNotification(I18n.getText("nicki.editor.save.info"));
				} catch (Exception e) {
					getWindow().showNotification(I18n.getText("nicki.editor.save.error"), 
							e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
				}
			}
		}
		nickiEditor.refresh(getProject());
	}

	public DirectoryEditor() {
	}
	
	private Project getProject() {
		return (Project) nickiEditor.getParent(directory);
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// verticalLayout_3
		verticalLayout_3 = buildVerticalLayout_3();
		mainLayout.addComponent(verticalLayout_3, "top:20.0px;left:20.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_3() {
		// common part: create layout
		verticalLayout_3 = new VerticalLayout();
		verticalLayout_3.setWidth("-1px");
		verticalLayout_3.setHeight("-1px");
		verticalLayout_3.setImmediate(false);
		verticalLayout_3.setMargin(false);
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		verticalLayout_3.addComponent(horizontalLayout_1);
		
		// members
		members = buildMembers();
		verticalLayout_3.addComponent(members);
		
		// label_dummy
		label_dummy = new Label();
		label_dummy.setWidth("-1px");
		label_dummy.setHeight("20px");
		label_dummy.setImmediate(false);
		verticalLayout_3.addComponent(label_dummy);
		
		// closeButton
		saveButton = new Button();
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		saveButton.setCaption("Speichern");
		saveButton.setImmediate(true);
		verticalLayout_3.addComponent(saveButton);
		
		return verticalLayout_3;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("100.0%");
		horizontalLayout_1.setHeight("40px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		
		// name
		name = new Label();
		name.setWidth("-1px");
		name.setHeight("-1px");
		name.setValue("Name");
		name.setImmediate(false);
		horizontalLayout_1.addComponent(name);
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private Panel buildMembers() {
		// common part: create layout
		members = new Panel();
		members.setWidth("600px");
		members.setHeight("-1px");
		members.setCaption("Mitglieder");
		members.setImmediate(false);
		
		// verticalLayout_1
		verticalLayout_1 = new VerticalLayout();
		verticalLayout_1.setWidth("100.0%");
		verticalLayout_1.setHeight("-1px");
		verticalLayout_1.setImmediate(false);
		verticalLayout_1.setMargin(false);
		members.setContent(verticalLayout_1);
		
		return members;
	}

}
