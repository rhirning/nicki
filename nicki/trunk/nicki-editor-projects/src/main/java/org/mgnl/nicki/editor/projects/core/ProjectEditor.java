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
package org.mgnl.nicki.editor.projects.core;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
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
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
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

	public ProjectEditor() {
		super();
		setUseSystemContext(true);
		setUseWelcomeDialog(DataHelper.booleanOf(Config.getProperty("nicki.projects.useWelcomeDialog", "false")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() throws DynamicObjectException {
		ProjectFilter projectFilter = new ProjectFilter(getNickiContext().getUser());
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.projects.basedn"), projectFilter);
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
					Person member = getNickiContext().loadObject(Person.class, value);
					if (member != null) {
						namingValue = member.getNamingValue();
					}
				}
				dynamicObject.initNew(dynamicObject.getParentPath(), namingValue);
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
