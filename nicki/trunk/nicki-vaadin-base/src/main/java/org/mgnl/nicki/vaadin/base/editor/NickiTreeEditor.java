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
package org.mgnl.nicki.vaadin.base.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.command.DeleteCommand;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.components.NewClassEditor;
import org.mgnl.nicki.vaadin.base.components.SimpleNewClassEditor;
import org.mgnl.nicki.vaadin.base.data.TreeContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Tree.ExpandEvent;

@SuppressWarnings("serial")
public class NickiTreeEditor extends CustomComponent {
	private static final Logger LOG = LoggerFactory.getLogger(NickiTreeEditor.class);

	public enum CREATE {
		ALLOW, DENY
	};

	public enum DELETE {
		ALLOW, DENY
	};

	public enum RENAME {
		ALLOW, DENY
	};

	private NickiSelect selector;
	private DynamicObject selectedObject = null;
	private ClassEditor viewer;
	private TreeContainer treeContainer;
	private String messageKeyBase;
	private DataProvider treeDataProvider;
	private String treeTitle;

	private Map<Class<? extends DynamicObject>, List<Class<? extends DynamicObject>>> children
		= new HashMap<Class<? extends DynamicObject>, List<Class<? extends DynamicObject>>>();

	private Map<Class<? extends DynamicObject>, Map<Action, Class<? extends DynamicObject>>> actions = new HashMap<Class<? extends DynamicObject>, Map<Action, Class<? extends DynamicObject>>>();
	private Map<Class<? extends DynamicObject>, Action[]> actionsList = new HashMap<Class<? extends DynamicObject>, Action[]>();
	private Map<Class<? extends DynamicObject>, Action[]> rootActionsList = new HashMap<Class<? extends DynamicObject>, Action[]>();
	private Map<Action, Class<? extends DynamicObject>> deleteActions = new HashMap<Action, Class<? extends DynamicObject>>();
	private Map<Action, Class<? extends DynamicObject>> renameActions = new HashMap<Action, Class<? extends DynamicObject>>();
	private Action refreshAction;

	private List<Class<? extends DynamicObject>> allowCreate = new ArrayList<Class<? extends DynamicObject>>();
	private List<Class<? extends DynamicObject>> allowDelete = new ArrayList<Class<? extends DynamicObject>>();
	private List<Class<? extends DynamicObject>> allowRename = new ArrayList<Class<? extends DynamicObject>>();
	private Map<Class<? extends DynamicObject>, List<TreeAction>> treeActions = new HashMap<Class<? extends DynamicObject>, List<TreeAction>>();

	private Map<Action, TreeAction> treeActionMap = new HashMap<Action, TreeAction>();

	private Map<Class<? extends DynamicObject>, ClassEditor> classEditors = new HashMap<Class<? extends DynamicObject>, ClassEditor>();
	private Map<Class<? extends DynamicObject>, NewClassEditor> newClassEditors = new HashMap<Class<? extends DynamicObject>, NewClassEditor>();
	private NickiContext context;
	private NickiApplication application;
	private HorizontalSplitPanel hsplit;
	private NickiTreeEditor nickiEditor;

	public NickiTreeEditor(NickiApplication application, NickiContext ctx) {
		this.nickiEditor = this;
		this.application = application;
		this.context = ctx;
		this.hsplit = new HorizontalSplitPanel();
		hsplit.setSplitPosition(200, Unit.PIXELS);
		setCompositionRoot(hsplit);
	}

	public void init(NickiSelect select, DataProvider treeDataProvider,
			String messageKeyBase) {

		this.treeDataProvider = treeDataProvider;
		this.messageKeyBase = messageKeyBase;
		this.treeTitle = I18n.getText(this.messageKeyBase + ".tree.title");
		this.treeContainer = new TreeContainer(this.context,
				this.treeDataProvider, this.treeTitle);
		selector = select;
		loadTree();
		
		Component selectorComponent = selector.getComponent();
		selectorComponent.setSizeFull();

		hsplit.setFirstComponent(selectorComponent);

		selector.setImmediate(true);
		selector.setSelectable(true);
		selector.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				DynamicObject selected = (DynamicObject) selector.getValue();
				if (selected == null) {
					if (viewer != null && selectedObject.isModified()) {
						try {
							// TODO: ask save/not save/back
							viewer.save();
						} catch (Exception e) {
							LOG.error("Error", e);
						}
					}
					hideClassEditor();
					return;

				}
				if (viewer != null && selectedObject.isModified()) {
					try {
						// TODO: ask save/not save/back
						viewer.save();
					} catch (Exception e) {
						LOG.error("Error", e);
					}
				}
				setSelectedObject(selected);
				if (selected == null || isRoot(selected)) {
					hideClassEditor();
				} else {
					for (Class<? extends DynamicObject> clazz : classEditors.keySet()) {
						if (clazz.isAssignableFrom(selected.getClass())) {
							ClassEditor classEditor = classEditors.get(clazz);
							showClassEditor(classEditor, selected);
							return;
						}
					}
					showClassEditor(new DynamicObjectViewer(), selected);
					/*
					if (classEditors.containsKey(selected.getClass())) {
						ClassEditor classEditor = classEditors.get(selected
								.getClass());
						showClassEditor(classEditor, selected);
					} else {
						showClassEditor(new DynamicObjectViewer(), selected);
					}
					*/
				}
			}
		});

		selector.addActionHandler(new Action.Handler() {

			public void handleAction(Action action, Object sender, Object target) {
				if (action == refreshAction) {
					refresh((DynamicObject) target);
				}
				if (deleteActions.containsKey(action)) {
					if (target.getClass() == deleteActions.get(action)) {
						getNickiApplication().confirm(
								new DeleteCommand(nickiEditor,
										(DynamicObject) target));
					}
				} else if (renameActions.containsKey(action)) {
					if (target.getClass() == renameActions.get(action)) {
						try {
							if (!isRoot((DynamicObject) target)) {
								renameItem((DynamicObject) target);
							}
						} catch (Exception e) {
							LOG.error("Error", e);
						}
					}
				} else if (treeActionMap.containsKey(action)) {
					treeActionMap.get(action).execute((DynamicObject) target);
				} else if (actions.containsKey(target.getClass())) {
					Map<Action, Class<? extends DynamicObject>> map = actions
							.get(target.getClass());
					if (map.containsKey(action)) {
						create((DynamicObject) target, map.get(action));
					}
				}
			}

			public Action[] getActions(Object target, Object sender) {
				if (target != null) {
					if (isRoot((DynamicObject) target)) {
						for (Class<? extends DynamicObject> clazz : rootActionsList.keySet()) {
							if (clazz.isAssignableFrom(target.getClass())) {
								return rootActionsList.get(clazz);
							}
						}

					} else {
						for (Class<? extends DynamicObject> clazz : actionsList.keySet()) {
							if (clazz.isAssignableFrom(target.getClass())) {
								return actionsList.get(clazz);
							}
						}
					}
				}
				return null;
			}
		});

		selector.addListener(new Tree.ExpandListener() {

			public void nodeExpand(ExpandEvent event) {
				DynamicObject object = (DynamicObject) event.getItemId();
				treeContainer.loadChildren(object);
			}
		});
	}
	
	public void showClassEditor(ClassEditor classEditor, DynamicObject selected) {

		classEditor.setDynamicObject(getEditor(), selected);
		viewer = classEditor;
		hsplit.setSecondComponent(viewer);
		
	}
	
	public void hideClassEditor() {
		if  (viewer != null) {
			hsplit.removeComponent(viewer);
			viewer = null;
		}
	}

	protected NickiTreeEditor getEditor() {
		return this;
	}

	public void refresh(DynamicObject object) {
		if (selectedObject != null) {
			selector.unselect(selectedObject);
			setSelectedObject(null);
		}

		hideClassEditor();
		viewer = null;

		collapse(object);
		reloadChildren(object);
		selector.expandItem(object);
	}

	protected boolean isRoot(DynamicObject dynamicObject) {
		return (dynamicObject == this.treeContainer.getRoot());
	}

	public void setClassIcon(Class<? extends DynamicObject> classDefinition,
			Icon icon) {
		this.treeContainer.setClassIcon(classDefinition, icon);
	}

	public void configureClass(Class<? extends DynamicObject> parentClass,
			Icon icon, CREATE allowCreate, DELETE allowDelete,
			RENAME allowRename, Class<? extends DynamicObject>... childClass) {

		List<? extends DynamicObject> dynamicObjects;
		try {
			dynamicObjects = getNickiContext().getObjectFactory()
					.findDynamicObjects(parentClass);
		} catch (InstantiateDynamicObjectException e) {
			dynamicObjects = new ArrayList<DynamicObject>();
		}

		for (DynamicObject dynamicObject : dynamicObjects) {
			if (icon != null) {
				setClassIcon(dynamicObject.getClass(), icon);
			}
			if (allowCreate == CREATE.ALLOW) {
				this.allowCreate.add(dynamicObject.getClass());
			}
			if (allowDelete == DELETE.ALLOW) {
				this.allowDelete.add(dynamicObject.getClass());
			}
			if (allowRename == RENAME.ALLOW) {
				this.allowRename.add(dynamicObject.getClass());
			}

			List<Class<? extends DynamicObject>> list = children
					.get(dynamicObject.getClass());
			if (list == null) {
				list = new ArrayList<Class<? extends DynamicObject>>();
				children.put(dynamicObject.getClass(), list);
			}
			for (int i = 0; i < childClass.length; i++) {
				try {
					List<? extends DynamicObject> childObjects = getNickiContext()
							.getObjectFactory().findDynamicObjects(
									childClass[i]);
					for (DynamicObject childObject : childObjects) {
						list.add(childObject.getClass());
					}
				} catch (InstantiateDynamicObjectException e) {
					LOG.error("Error", e);
				}
			}
}
	}

	public void addAction(TreeAction treeAction) {
		List<TreeAction> actions = this.treeActions.get(treeAction
				.getTargetClass());
		if (actions == null) {
			actions = new ArrayList<TreeAction>();
			this.treeActions.put(treeAction.getTargetClass(), actions);
		}
		actions.add(treeAction);
	}

	public void setClassEditor(Class<? extends DynamicObject> classdefinition,
			ClassEditor classEditor) {
		this.classEditors.put(classdefinition, classEditor);
	}

	protected void renameItem(DynamicObject target) {
		EnterNameHandler handler = new RenameObjecttEnterNameHandler(this,
				target);
		EnterNameDialog dialog = new EnterNameDialog(messageKeyBase + ".rename",
				I18n.getText(messageKeyBase
						+ ".rename.window.title"));
		dialog.setHandler(handler);
		dialog.setWidth(440, Unit.PIXELS);
		dialog.setHeight(500, Unit.PIXELS);
		dialog.setModal(true);
		UI.getCurrent().addWindow(dialog);
	}

	protected void create(DynamicObject parent,
			Class<? extends DynamicObject> classDefinition) {
		treeContainer.loadChildren(parent);
		try {
			addDynamicObject(parent, classDefinition);
		} catch (Exception e) {
			Notification.show(I18n.getText("nicki.editor.create.error", parent.getName(),
							classDefinition.getSimpleName()), e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

	public void loadTree() {
		selector.setContainerDataSource(treeContainer.getTree());
		selector.setItemCaptionPropertyId(TreeContainer.PROPERTY_NAME);
		selector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		selector.setItemIconPropertyId(TreeContainer.PROPERTY_ICON);
	}

	private void addDynamicObject(DynamicObject parent,
			Class<? extends DynamicObject> classDefinition)
			throws InstantiateDynamicObjectException, DynamicObjectException {
		NewClassEditor editor;
		if (this.newClassEditors.get(classDefinition) != null) {
			editor = this.newClassEditors.get(classDefinition);
			editor.init(parent, classDefinition);
		} else {
			editor = new SimpleNewClassEditor(this, messageKeyBase + "."
					+ getClassName(classDefinition) + ".create",
					I18n.getText(messageKeyBase + "."
							+ getClassName(classDefinition) + ".create.window.title"));
			editor.init(parent, classDefinition);
		}
		if (editor instanceof Window) {
			Window window = (Window) editor;
			window.setWidth(440, Unit.PIXELS);
			window.setHeight(500, Unit.PIXELS);
			window.setModal(true);
			UI.getCurrent().addWindow(window);
		} else {
			// TODO: error!!
		}

	}

	public void setNewClassEditor(
			Class<? extends DynamicObject> classDefinition,
			NewClassEditor newClassEditor) {
		this.newClassEditors.put(classDefinition, newClassEditor);
	}

	protected <T extends DynamicObject> boolean create(DynamicObject parent,
			Class<T> classDefinition, String name)
			throws InstantiateDynamicObjectException, DynamicObjectException {
		T dynamicObject = null;
		dynamicObject = context.createDynamicObject(classDefinition,
				parent.getPath(), name);
		if (dynamicObject != null) {
			treeContainer.addItem(dynamicObject, parent, dynamicObject
					.getModel().childrenAllowed());
			return true;
		}
		return false;
	}

	public void initActions() {
		refresh(treeContainer.getRoot());

		refreshAction = new Action(I18n.getText(this.messageKeyBase
				+ ".action.refresh"));

		for (Class<? extends DynamicObject> classDefinition : this.children
				.keySet()) {
			List<Action> classActions = new ArrayList<Action>();
			List<Action> rootClassActions = new ArrayList<Action>();
			Map<Action, Class<? extends DynamicObject>> map = new HashMap<Action, Class<? extends DynamicObject>>();
			// treeActions
			List<TreeAction> a = getTreeActions(classDefinition);
			if(a != null && !a.isEmpty()) {
				for (TreeAction treeAction : a) {
					Action action = new Action(treeAction.getName());
					classActions.add(action);
					rootClassActions.add(action);
					treeActionMap.put(action, treeAction);
				}
			}
			if (this.children.get(classDefinition) != null) {
				for (Class<? extends DynamicObject> childClassPattern : this.children
						.get(classDefinition)) {
					if (this.allowCreate.contains(childClassPattern)) {
						Action childAction = new Action(
								I18n.getText(this.messageKeyBase
										+ ".action."
										+ getClassName(childClassPattern) + ".new"));
						classActions.add(childAction);
						rootClassActions.add(childAction);
						map.put(childAction, childClassPattern);
					}
				}
			}
			// delete
			if (this.allowDelete.contains(classDefinition)) {
				Action deleteAction = new Action(
						I18n.getText(this.messageKeyBase + ".action.delete"));
				classActions.add(deleteAction);
				this.deleteActions.put(deleteAction, classDefinition);
			}
			// rename
			if (this.allowRename.contains(classDefinition)) {
				Action renameAction = new Action(
						I18n.getText(this.messageKeyBase + ".action.rename"));
				classActions.add(renameAction);
				this.renameActions.put(renameAction, classDefinition);
			}
			// refresh
			rootClassActions.add(refreshAction);
			classActions.add(refreshAction);
			//
			actions.put(classDefinition, map);
			rootActionsList.put(classDefinition,
					rootClassActions.toArray(new Action[] {}));
			actionsList.put(classDefinition,
					classActions.toArray(new Action[] {}));
		}
	}

	private List<TreeAction> getTreeActions(Class<? extends DynamicObject> classDefinition) {
		List<TreeAction> list = new ArrayList<>();
		for (Class<? extends DynamicObject> clazz : this.treeActions.keySet()) {
			if (clazz.isAssignableFrom(classDefinition)) {
				list.addAll(this.treeActions.get(clazz));
			}
		}
		return list;
	}

	public Action[] getActions(Object object) {
		return actionsList.get(object.getClass());
	}

	public DynamicObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(DynamicObject selectedObject) {
		this.selectedObject = selectedObject;
	}

	public void addChild(DynamicObject parent, DynamicObject child) {
		this.treeContainer.addChild(parent, child);
	}

	public DynamicObject getParent(DynamicObject child) {
		return this.treeContainer.getParent(child);
	}

	public void reloadChildren(DynamicObject parent) {
		hideClassEditor();
		parent.unLoadChildren();
		this.treeContainer.removeChildren(parent);
		this.treeContainer.loadChildren(parent);
	}

	public List<Class<? extends DynamicObject>> getAllowedChildren(
			Class<? extends DynamicObject> classDefinition) {
		return this.children.get(classDefinition);
	}

	public boolean isParent(DynamicObject parent, DynamicObject object) {
		return this.treeContainer.isParent(parent, object);
	}

	public void moveObject(DynamicObject object, DynamicObject target)
			throws DynamicObjectException {
		this.treeContainer.setParent(object, target);
	}

	public String getClassName(Class<? extends DynamicObject> classDefinition) {
		return StringUtils.substringAfterLast(classDefinition.getName(), ".");
	}

	public void expandAll() {
		for (Object id : selector.rootItemIds()) {
			selector.expandItemsRecursively(id);
		}
	}

	public void collapse(DynamicObject object) {
		selector.collapseItemsRecursively(object);
	}

	public String getMessageKeyBase() {
		return messageKeyBase;
	}

	public NickiApplication getNickiApplication() {
		return application;
	}

	public NickiContext getNickiContext() {
		return context;
	}

	public NickiSelect getSelector() {
		return selector;
	}
}
