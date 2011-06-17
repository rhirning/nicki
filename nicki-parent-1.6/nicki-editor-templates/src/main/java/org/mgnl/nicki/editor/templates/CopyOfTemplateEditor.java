package org.mgnl.nicki.editor.templates;

import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;


import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class CopyOfTemplateEditor extends NickiApplication implements TemplateViewerHandler {

	private static final long serialVersionUID = -8245147689512577915L;
    private static String[] visibleCols = new String[] { "name"};
    

	private Action ACTION_DELETE;
	private Action[] TEMPLATE_ACTIONS;
	
	private HorizontalSplitPanel hsplit;
	private Table templateSelector;
	private VerticalLayout selectorLayout;
	private Template selectedTemplate = null;
	private TemplateViewer viewer;
	private Button newTemplateButton;
	private Window newTemplateWindow;


	@SuppressWarnings("serial")
	@Override
	public Component getEditor() {
		initActions();
		
		templateSelector = new Table(null);
		loadTemplates();
		templateSelector.setWidth(200, Sizeable.UNITS_PIXELS);
		hsplit = new HorizontalSplitPanel();
		hsplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);
		selectorLayout = new VerticalLayout();
		selectorLayout.addComponent(templateSelector);
		newTemplateButton = new Button();
		newTemplateButton.setWidth("-1px");
		newTemplateButton.setHeight("-1px");
		newTemplateButton.setCaption("neu");
		newTemplateButton.setImmediate(true);
		newTemplateButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				addTemplate();
			}
		});

		selectorLayout.addComponent(newTemplateButton);
		hsplit.setFirstComponent(selectorLayout);

		templateSelector.setImmediate(true);
		templateSelector.setSelectable(true);
		templateSelector.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				selectedTemplate = (Template) templateSelector.getValue();
				if (selectedTemplate == null) {
					hsplit.removeComponent(viewer);
					viewer = null;
				} else {
					viewer = new TemplateViewer();
					viewer.setDynamicObject(null, selectedTemplate);
					hsplit.setSecondComponent(viewer);
				}
				
			}
		});
		templateSelector.addActionHandler(new Action.Handler() {
			
			public void handleAction(Action action, Object sender, Object target) {
				if (action == ACTION_DELETE) {
					deleteTemplate((Template) target);
					templateSelector.removeItem(target);
				}
			}
			public Action[] getActions(Object target, Object sender) {
				return TEMPLATE_ACTIONS;
			}
		});

		return hsplit;

	}
	
	void loadTemplates() {
		templateSelector.setContainerDataSource(getTemplates());
		templateSelector.setVisibleColumns(visibleCols);

	}
	
	private BeanItemContainer<Template> getTemplates() {
		
	    // Create a container for such beans
	    BeanItemContainer<Template> beans =
	        new BeanItemContainer<Template>(Template.class);
	    
	    List<DynamicObject> loadedTemplates = 
	    	getNickiContext().loadObjects(Config.getProperty("nicki.templates.basedn"), "(objectClass=nickiTemplate)");
	    if (loadedTemplates != null) {
		    for (Iterator<DynamicObject> iterator = loadedTemplates.iterator(); iterator.hasNext();) {
				DynamicObject p = iterator.next();
				if (p instanceof Template) {
					beans.addBean((Template) p);
				}
			}
	    }

	    /*
	    // Add some additional beans to it
	    beans.addBean(new Project("1","Erstes Projekt"));
	    beans.addBean(new Project("2", "Zweites Projekt"));
	    beans.addBean(new Project("3", "Drittes Projekt"));
	    beans.addBean(new Project("4", "Viertes Projekt"));
	    */
	    
		return beans;
	}

	public void save(Template template) throws DynamicObjectException, NamingException {
		if (template.isComplete()) {
			if (!template.isNew()) {
				template.update();
			}
		}
		
	}
	
	private void addTemplate() {
		newTemplateWindow = new Window(I18n.getText(getI18nBase() + ".add.window.title"),
				new EnterNameDialog(new NewTemplateHandler(), getI18nBase() + ".add"));
		newTemplateWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newTemplateWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newTemplateWindow.setModal(true);
		getMainWindow().addWindow(newTemplateWindow);
	}
	
	public class NewTemplateHandler extends EnterNameHandler {
		public boolean setName(String name) {
			
			Template newTemplate = null;
			try {
				newTemplate = createTemplate(Config.getProperty("nicki.template.basedn"), name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (newTemplate != null) {
				templateSelector.addItem(newTemplate);
				return true;
			}
			return false;
		}

		public void closeEnterNameDialog() {
			getMainWindow().removeWindow(newTemplateWindow);
		}


	}

	private Template createTemplate(String parentPath, String name) throws DynamicObjectException, InstantiateDynamicObjectException {
		return (Template) getNickiContext().getObjectFactory().createNewDynamicObject(Template.class, parentPath, name);
	}

	private void initActions() {
		ACTION_DELETE = new Action(I18n.getText(getI18nBase() + ".action.delete"));
		TEMPLATE_ACTIONS = new Action[] { ACTION_DELETE};
	}

	
	private void deleteTemplate(Template target) {
		try {
			target.delete();
		} catch (DynamicObjectException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.templates";
	}


}
