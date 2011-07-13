package org.mgnl.nicki.vaadin.base.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Tree.ExpandEvent;

@SuppressWarnings("serial")
public class NickiTreeEditor extends AbsoluteLayout {
	
	public enum CREATE {ALLOW, DENY};
	public enum DELETE {ALLOW, DENY};
	public enum RENAME {ALLOW, DENY};
	
	private NickiSelect selector;
	private VerticalLayout selectorLayout;
	private DynamicObject selectedObject = null;
	private ClassEditor viewer;
	private Window newWindow;
	private TreeContainer treeContainer;
	private String messageKeyBase;
	private DataProvider treeDataProvider;
	private String treeTitle;
	
	private Map<Class<?>, List<Class<?>>> children = new HashMap<Class<?>, List<Class<?>>>();
	
	private Map<Class<?>, Map<Action, Class<?>>> actions = new HashMap<Class<?>, Map<Action,Class<?>>>();
	private Map<Class<?>, Action[]> actionsList = new HashMap<Class<?>, Action[]>();
	private Map<Class<?>, Action[]> rootActionsList = new HashMap<Class<?>, Action[]>();
	private Map<Action, Class<?>> deleteActions = new HashMap<Action, Class<?>>();
	private Map<Action, Class<?>> renameActions = new HashMap<Action, Class<?>>();
	private Action refreshAction;
	
	private List<Class<?>> allowCreate = new ArrayList<Class<?>>();
	private List<Class<?>> allowDelete = new ArrayList<Class<?>>();
	private List<Class<?>> allowRename = new ArrayList<Class<?>>();
	private Map<Class<?>, List<TreeAction>> treeActions = new HashMap<Class<?>, List<TreeAction>>();
	
	private Map<Action, TreeAction> treeActionMap = new HashMap<Action, TreeAction>();
	
	private Map<Class<?>, ClassEditor> classEditors = new HashMap<Class<?>, ClassEditor>();
	private Map<Class<?>, NewClassEditor> newClassEditors = new HashMap<Class<?>, NewClassEditor>();
	private NickiContext context;
	private NickiApplication application;
	private HorizontalSplitPanel hsplit;
	private NickiTreeEditor nickiEditor;

	public NickiTreeEditor(NickiApplication application, NickiContext ctx) {
		this.nickiEditor = this;
		this.application = application;
		this.context = ctx;
	}
	
	public void init(NickiSelect select, DataProvider treeDataProvider, String messageKeyBase) {

		this.treeDataProvider = treeDataProvider;
		this.messageKeyBase = messageKeyBase;
		this.treeTitle = I18n.getText(this.messageKeyBase +".tree.title");
		this.treeContainer = new TreeContainer(this.context, this.treeDataProvider, this.treeTitle);
		this.hsplit = new HorizontalSplitPanel();
		hsplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);
		hsplit.setHeight("100%");
		setHeight(800, UNITS_PIXELS);

		selector = select;
		loadTree();
		selector.setHeight("100%");
		selector.setWidth("100%");
//		selector.setDragMode(TreeDragMode.NODE);
//		selector.setDropHandler(new TreeDropHandler(this));

		addComponent(hsplit, "top:0.0px;left:0.0px;");
		
		selectorLayout = new VerticalLayout();
		selectorLayout.setHeight("100%");

		selectorLayout.addComponent(selector.getComponent());

		hsplit.setFirstComponent(selectorLayout);

		selector.setImmediate(true);
		selector.setSelectable(true);
		selector.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				DynamicObject selected = (DynamicObject) selector.getValue();
				if (selected == null) {
					if (viewer != null) {
						removeComponent(viewer);
						viewer = null;
					return;
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
						ClassEditor classEditor = classEditors.get(selected.getClass());
						classEditor.setDynamicObject(getEditor(), selected);
						viewer = classEditor;
					} else {
						viewer = new DynamicObjectViewer();
						viewer.setDynamicObject(getEditor(), selected);
					}
					hsplit.setSecondComponent(viewer);
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
						getNickiApplication().confirm(new DeleteCommand(nickiEditor, (DynamicObject) target));
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
					treeActionMap.get(action).execute(getWindow(), (DynamicObject) target);
				} else if (actions.containsKey(target.getClass())) {
					Map<Action, Class<?>> map = actions.get(target.getClass());
					if (map.containsKey(action)) {
						create((DynamicObject) target, map.get(action));
					}
				}
			}

			public Action[] getActions(Object target, Object sender) {
				if (isRoot((DynamicObject) target)) {
					return rootActionsList.get(target.getClass());
					
				} else {
					return actionsList.get(target.getClass());
				}
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
		hsplit.removeAllComponents();
		hsplit.setFirstComponent(selectorLayout);
		viewer = null;

		collapse(object);
		reloadChildren(object);
		selector.expandItem(object);
	}

	protected boolean isRoot(DynamicObject dynamicObject) {
		return (dynamicObject == this.treeContainer.getRoot());
	}

	public void setClassIcon(Class<?> classDefinition, Icon icon) {
		this.treeContainer.setClassIcon(classDefinition, icon);
	}

	public void configureClass(Class<?> parentClass, Icon icon, CREATE allowCreate, DELETE allowDelete, RENAME allowRename, Class<?> ... childClass) {
		if (icon != null) {
			setClassIcon(parentClass, icon);
		}
		if (allowCreate == CREATE.ALLOW) {
			this.allowCreate.add(parentClass);
		}
		if (allowDelete == DELETE.ALLOW) {
			this.allowDelete.add(parentClass);
		}
		if (allowRename == RENAME.ALLOW) {
			this.allowRename.add(parentClass);
		}
		List<Class<?>> list = children.get(parentClass);
		if (list == null) {
			list = new ArrayList<Class<?>>();
			children.put(parentClass, list);
		}
		for (int i = 0; i < childClass.length; i++) {
			list.add(childClass[i]);
		}
	}
	
	public void addAction(TreeAction treeAction) {
		List<TreeAction> actions = this.treeActions.get(treeAction.getTargetClass());
		if (actions == null) {
			actions = new ArrayList<TreeAction>();
			this.treeActions.put(treeAction.getTargetClass(), actions);
		}
		actions.add(treeAction);
	}
	
	public void setClassEditor(Class<?> classdefinition, ClassEditor classEditor) {
		this.classEditors.put(classdefinition, classEditor);
	}

	protected void renameItem(DynamicObject target) {
		EnterNameHandler handler = new RenameObjecttEnterNameHandler(this, target);
		EnterNameDialog dialog = new EnterNameDialog(messageKeyBase + ".rename");
		dialog.setHandler(handler);
		newWindow = new Window(
				I18n.getText(messageKeyBase + ".rename.window.title"), dialog);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		this.getWindow().addWindow(newWindow);
	}


	protected void create(DynamicObject parent, Class<?> classDefinition) {
		treeContainer.loadChildren(parent);
		try {
			addDynamicObject(parent, classDefinition);
		} catch (Exception e) {
			getWindow().showNotification(
					I18n.getText("nicki.editor.create.error", parent.getName(), 
							classDefinition.getSimpleName()),
					e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void loadTree() {
		selector.setContainerDataSource(treeContainer.getTree());
		selector.setItemCaptionPropertyId(TreeContainer.PROPERTY_NAME);
		selector.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		selector.setItemIconPropertyId(TreeContainer.PROPERTY_ICON);
	}

	private void addDynamicObject(DynamicObject parent, Class<?> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		NewClassEditor editor;
		if (this.newClassEditors .get(classDefinition) != null) {
			editor = this.newClassEditors.get(classDefinition);
			editor.init(parent, classDefinition);
		} else {
			editor = new SimpleNewClassEditor(this,  messageKeyBase + "." + getClassName(classDefinition) + ".create");
			editor.init(parent, classDefinition);
		}
		editor.setParent(null);

		newWindow = new Window(
				I18n.getText(messageKeyBase  + "." + getClassName(classDefinition) + ".create.window.title"), editor);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		this.getWindow().addWindow(newWindow);
	}
	
	public void setNewClassEditor(Class<?> classDefinition, NewClassEditor newClassEditor) {
		this.newClassEditors.put(classDefinition, newClassEditor);
	}


	protected boolean create(DynamicObject parent, Class<?> classDefinition, String name) throws InstantiateDynamicObjectException, DynamicObjectException {
		DynamicObject dynamicObject = null;
		dynamicObject = context.createDynamicObject(
					classDefinition, parent.getPath(), name);
		if (dynamicObject != null) {
			treeContainer.addItem(dynamicObject, parent, dynamicObject.getModel().childrenAllowed());
			return true;
		}
		return false;
	}



	public void initActions() {
		refresh(treeContainer.getRoot());

		refreshAction = new Action(
				I18n.getText(this.messageKeyBase + ".action.refresh"));

		for (Iterator<Class<?>> iterator = this.children.keySet().iterator(); iterator.hasNext();) {
			Class<?> classDefinition = iterator.next();
			List<Action> classActions = new ArrayList<Action>();
			List<Action> rootClassActions = new ArrayList<Action>();
			Map<Action, Class<?>> map = new HashMap<Action, Class<?>>();
			// treeActions
			if (this.treeActions.containsKey(classDefinition)) {
				for (Iterator<TreeAction> iterator2 = this.treeActions.get(classDefinition).iterator(); iterator2
						.hasNext();) {
					TreeAction treeAction = iterator2.next();
					Action action = new Action(treeAction.getName());
					classActions.add(action);
					rootClassActions.add(action);
					treeActionMap.put(action, treeAction);
				}
			}			
			List<Class<?>> children = this.children.get(classDefinition);
			for (Iterator<Class<?>> iterator2 = children.iterator(); iterator2.hasNext();) {
				Class<?> childClass = iterator2.next();
				if (this.allowCreate.contains(childClass)) {
					Action childAction = new Action(
							I18n.getText(this.messageKeyBase + ".action." + getClassName(childClass) + ".new"));
					classActions.add(childAction);
					rootClassActions.add(childAction);
					map.put(childAction, childClass);
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
			rootActionsList.put(classDefinition, rootClassActions.toArray(new Action[]{}));
			actionsList.put(classDefinition, classActions.toArray(new Action[]{}));
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
	
	public List<Class<?>> getAllowedChildren(Class<?> classDefinition) {
		return this.children.get(classDefinition);
	}

	public boolean isParent(DynamicObject parent,
			DynamicObject object) {
		return this.treeContainer.isParent(parent, object);
	}

	public void moveObject(DynamicObject object,
			DynamicObject target) throws DynamicObjectException {
		this.treeContainer.setParent(object, target);
	}

	public String getClassName(Class<?> classDefinition) {
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
