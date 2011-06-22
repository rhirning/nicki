package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Directory;
import org.mgnl.nicki.dynamic.objects.objects.Member;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.editor.projects.directories.DirectoryEditor;
import org.mgnl.nicki.editor.projects.members.MemberEditor;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectViewer;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ProjectEditor extends NickiApplication {

	@Override
	public Component getEditor() {
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.projects.basedn"), new ProjectFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, null, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Project.class);
		editor.configureClass(Project.class, Icon.DOCUMENT, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Member.class, Directory.class);
		editor.configureClass(Member.class, Icon.USER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.DENY);
		editor.configureClass(Directory.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY);
		editor.setClassEditor(Project.class, new ProjectViewer());
		editor.setClassEditor(Member.class, new MemberEditor());
		editor.setClassEditor(Directory.class, new DirectoryEditor());
		editor.setNewClassEditor(Member.class, new DynamicObjectViewer(new SyncListener(editor)));
		editor.initActions();
		return editor;
	}
	
	private class SyncListener implements DynamicObjectValueChangeListener, Serializable {
		private TreeEditor editor;
		public SyncListener(TreeEditor editor) {
			this.editor = editor;
		}

		public void valueChange(DynamicObject dynamicObject, String name,
				List<Object> values) {
		}

		public void valueChange(DynamicObject dynamicObject,
				String attributeName, String value) {
			if (StringUtils.equals(attributeName, "member")) {
				String namingValue = "";
				if (StringUtils.isNotEmpty(value)) {
					Person member = (Person) getNickiContext().loadObject(value);
					if (member != null) {
						namingValue = member.getNamingValue();
					}
				}
				dynamicObject.init(dynamicObject.getParentPath(), namingValue);
			}
		}

		public boolean acceptAttribute(String name) {
			if (StringUtils.equals(name, "member")) {
				return true;
			}
			return false;
		}

		public void close(Component component) {
			component.getWindow().getParent().removeWindow(component.getWindow());
		}

		public void refresh(DynamicObject dynamicObject) {
			editor.refresh(dynamicObject);
		}
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.projects";
	}
	
}
