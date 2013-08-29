package org.mgnl.nicki.editor.log4j;

import java.io.File;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TailViewer extends CustomComponent {

	private static final long serialVersionUID = 7750777204922803240L;
	private VerticalLayout mainLayout;
	private Panel panel;
	private TextField path;
	private HorizontalLayout inputPanel;
	private VerticalLayout panelContent;
	private Tailer tailer = null;
	private TailerListener listener = new TailerListener();

	public TailViewer() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		setSizeFull();
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setSizeFull();
		
		inputPanel = new HorizontalLayout();
		inputPanel.setWidth("100%");
		inputPanel.setHeight("-1px");
		mainLayout.addComponent(inputPanel);
		
		// path
		path = new TextField();
		path.setImmediate(false);
		path.setWidth("100%");
		path.setHeight("-1px");
		inputPanel.addComponent(path);
		
		Button loadButton = new Button("Load");
		loadButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -6478602760185041606L;

			@Override
			public void buttonClick(ClickEvent event) {
				reload();
			}
		});
		inputPanel.addComponent(loadButton);
		
		Button refreshButton = new Button("Update");
		refreshButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7103010719060704358L;

			@Override
			public void buttonClick(ClickEvent event) {
				refresh();
			}
		});
		inputPanel.addComponent(refreshButton);
		
		inputPanel.setExpandRatio(path, 1);
		
		// panel
		panel = new Panel();
		panel.setImmediate(true);
		panel.setSizeFull();
		panelContent = new VerticalLayout();
		panel.setContent(panelContent);
		panelContent.setWidth("100%");
		mainLayout.addComponent(panel);
		mainLayout.setExpandRatio(panel, 1);
		
		return mainLayout;
	}

	protected void refresh() {
		// TODO Auto-generated method stub
		
	}

	protected void reload() {
		panelContent.removeAllComponents();
		if (tailer != null) {
			tailer.stop();
			tailer = null;
		}
		File file = new File(path.getValue()); 
		
		long delayMillis = 1000;
		boolean end = true;
		tailer = Tailer.create(file, listener, delayMillis , end);
	}

	class TailerListener extends TailerListenerAdapter {

		@Override
		public void handle(String line) {
			panelContent.addComponent(new Label(line));
		}
	}
		
}
