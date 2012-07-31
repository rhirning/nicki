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
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.command.DeleteCommand;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.components.NewClassEditor;
import org.mgnl.nicki.vaadin.base.components.SimpleNewClassEditor;
import org.mgnl.nicki.vaadin.base.data.TreeContainer;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectViewer;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Tree.ExpandEvent;

@SuppressWarnings("serial")
public class NickiTreeEditor extends AbsoluteLayout {

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
	private Panel panel;
	private VerticalLayout selectorLayout;
	private DynamicObject selectedObject = null;
	private ClassEditor viewer;
	private Window newWindow;
	private TreeContainer treeContainer;
	private String messageKeyBase;
	private DataProvider treeDataProvider;
	private String treeTitle;

	private Map<Class<? extends DynamicObject>, List<Class<? extends DynamicObject>>> children = new HashMap<Class<? extends DynamicObject>, List<Class<? extends DynamicObject>>>();

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
	private float viewerHeight = 800;
	private Slider changeSize;

	public NickiTreeEditor(NickiApplication application, NickiContext ctx) {
		this.nickiEditor = this;
		this.application = application;
		this.context = ctx;
	}

	public void init(NickiSelect select, DataProvider treeDataProvider,
			String messageKeyBase) {

		this.treeDataProvider = treeDataProvider;
		this.messageKeyBase = messageKeyBase;
		this.treeTitle = I18n.getText(this.messageKeyBase + ".tree.title");
		this.treeContainer = new TreeContainer(this.context,
				this.treeDataProvider, this.treeTitle);
		this.hsplit = new HorizontalSplitPanel();
		hsplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);
		hsplit.setHeight("100%");
		// setHeight(900, UNITS_PIXELS);

		selector = select;
		loadTree();
		selector.setHeight("100%");
		selector.setWidth("100%");
		// selector.setDragMode(TreeDragMode.NODE);
		// selector.setDropHandler(new TreeDropHandler(this));

		addComponent(hsplit, "top:0.0px;left:0.0px;");

		selectorLayout = new VerticalLayout();

		changeSize = new Slider();
		changeSize.setImmediate(true);
		changeSize.setMin(800);
		changeSize.setMax(2000);
		changeSize.setWidth("100%");
		changeSize.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				viewerHeight = Float.parseFloat(value.toString());
				if (viewer != null) {
					viewer.setHeight(viewerHeight, UNITS_PIXELS);
				}

			}
		});
		/*
		 * changeSize.addListener(new Button.ClickListener() {
		 * 
		 * @Override public void buttonClick(ClickEvent event) { viewerHeight +=
		 * 100; if (viewer != null) { viewer.setHeight(viewerHeight,
		 * UNITS_PIXELS); } } });
		 */
		selectorLayout.addComponent(selector.getComponent());

		hsplit.setFirstComponent(selectorLayout);
		this.panel = new Panel();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setMargin(false);
		panel.setContent(panelLayout);
		panel.setHeight("100%");
		hsplit.setSecondComponent(panel);

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
							e.printStackTrace();
						}
					}
					panel.removeComponent(viewer);
					viewer = null;
					return;

				}
				if (viewer != null && selectedObject.isModified()) {
					try {
						// TODO: ask save/not save/back
						viewer.save();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				setSelectedObject(selected);
				if (selected == null || isRoot(selected)) {
					if (viewer != null) {
						removeComponent(viewer);
						viewer = null;
					}
				} else {
					if (classEditors.containsKey(selected.getClass())) {
						ClassEditor classEditor = classEditors.get(selected
								.getClass());
						classEditor.setDynamicObject(getEditor(), selected);
						viewer = classEditor;
						viewer.setHeight(viewerHeight, UNITS_PIXELS);
					} else {
						viewer = new DynamicObjectViewer();
						viewer.setDynamicObject(getEditor(), selected);
						viewer.setHeight(viewerHeight, UNITS_PIXELS);
					}
					panel.removeAllComponents();
					panel.addComponent(changeSize);
					panel.addComponent(viewer);
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
							e.printStackTrace();
						}
					}
				} else if (treeActionMap.containsKey(action)) {
					treeActionMap.get(action).execute(getWindow(),
							(DynamicObject) target);
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
						return rootActionsList.get(target.getClass());

					} else {
						return actionsList.get(target.getClass());
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

	protected NickiTreeEditor getEditor() {
		return this;
	}

	public void refresh(DynamicObject object) {
		if (selectedObject != null) {
			selector.unselect(selectedObject);
			setSelectedObject(null);
		}

		panel.removeAllComponents();
		hsplit.setFirstComponent(selectorLayout);
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
					e.printStackTrace();
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
		EnterNameDialog dialog = new EnterNameDialog(messageKeyBase + ".rename");
		dialog.setHandler(handler);
		newWindow = new Window(I18n.getText(messageKeyBase
				+ ".rename.window.title"), dialog);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		this.getWindow().addWindow(newWindow);
	}

	protected void create(DynamicObject parent,
			Class<? extends DynamicObject> classDefinition) {
		treeContainer.loadChildren(parent);
		try {
			addDynamicObject(parent, classDefinition);
		} catch (Exception e) {
			getWindow().showNotification(
					I18n.getText("nicki.editor.create.error", parent.getName(),
							classDefinition.getSimpleName()), e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void loadTree() {
		selector.setContainerDataSource(treeContainer.getTree());
		selector.setItemCaptionPropertyId(TreeContainer.PROPERTY_NAME);
		selector.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
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
					+ getClassName(classDefinition) + ".create");
			editor.init(parent, classDefinition);
		}
		editor.setParent(null);

		newWindow = new Window(I18n.getText(messageKeyBase + "."
				+ getClassName(classDefinition) + ".create.window.title"),
				editor);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		this.getWindow().addWindow(newWindow);
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
			if (this.treeActions.containsKey(classDefinition)) {
				for (TreeAction treeAction : this.treeActions
						.get(classDefinition)) {
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
						try {
							List<? extends DynamicObject> allClasses = getNickiContext()
									.getObjectFactory().findDynamicObjects(
											childClassPattern);
							for (DynamicObject childClass : allClasses) {
								Action childAction = new Action(
										I18n.getText(this.messageKeyBase
												+ ".action."
												+ getClassName(childClass
														.getClass()) + ".new"));
								classActions.add(childAction);
								rootClassActions.add(childAction);
								map.put(childAction, childClass.getClass());
							}
						} catch (InstantiateDynamicObjectException e) {
							e.printStackTrace();
						}
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
		if (viewer != null) {
			removeComponent(viewer);
			viewer = null;
		}
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
