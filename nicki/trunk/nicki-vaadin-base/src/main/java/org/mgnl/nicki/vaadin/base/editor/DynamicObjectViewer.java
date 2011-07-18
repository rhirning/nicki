package org.mgnl.nicki.vaadin.base.editor;


import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.components.NewClassEditor;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DynamicObjectViewer extends CustomComponent implements NewClassEditor, ClassEditor {

	private AbsoluteLayout mainLayout;
	private DynamicObject dynamicObject;
	private Button saveButton;
	private boolean create;
	private DynamicObjectValueChangeListener listener = null;
	private DynamicObject parent = null;

	@Deprecated
	public DynamicObjectViewer(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public DynamicObjectViewer() {
	}

	@Override
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	public DynamicObjectViewer(DynamicObjectValueChangeListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void init(DynamicObject parent, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		this.parent = parent;
		this.dynamicObject = parent.getContext().getObjectFactory().getDynamicObject(classDefinition, parent.getPath(), "");
		this.create = true;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		VerticalLayout layout = new VerticalLayout();
		mainLayout.addComponent(layout, "top:20.0px;left:20.0px;");
		
		layout.addComponent(getLayout());
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				save();
			}
		});

		layout.addComponent(saveButton);
		return mainLayout;
	}
	
	protected Layout getLayout() {
		VerticalLayout layout = new VerticalLayout();
		DynamicObjectFieldFactory factory = new DynamicObjectFieldFactory(listener);
		factory.addFields(layout, dynamicObject, create);
		return layout;
	}

	protected void save() {
		try {
			if (create) {
				dynamicObject.create();
			} else {
				dynamicObject.update();
			}
			if (listener != null) {
				listener.close(this);
				listener.refresh(this.parent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCreate() {
		return create;
	}
}
