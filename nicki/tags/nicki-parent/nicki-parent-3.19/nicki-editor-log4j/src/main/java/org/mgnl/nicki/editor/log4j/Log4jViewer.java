package org.mgnl.nicki.editor.log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.slf4j.LoggerFactory;


import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

public class Log4jViewer extends CustomComponent {
	private static final long serialVersionUID = 6677098857979852467L;
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Log4jViewer.class);
	private Panel canvas;
	private Table table;

    /**
     * The root appender.
     */
    private static final String ROOT = "Root";
    /**
     * All the log levels.
     */
    private static final String[] LEVELS = new String[]{
        Level.OFF.toString(),
        Level.FATAL.toString(),
        Level.ERROR.toString(),
        Level.WARN.toString(),
        Level.INFO.toString(),
        Level.DEBUG.toString(),
        Level.ALL.toString() };

    
	public Log4jViewer(NickiApplication application) {
		canvas = new Panel();
		canvas.setSizeFull();
		table = new Table();
		table.setSizeFull();
		canvas.setContent(table);
		table.addContainerProperty("title", String.class, "");
		table.setColumnHeader("title", I18n.getText(application.getI18nBase() + ".column.title"));
		table.addContainerProperty("inherited", String.class, "");
		table.setColumnHeader("inherited", I18n.getText(application.getI18nBase() + ".column.inherited"));
		table.addContainerProperty("comboBox", ComboBox.class, null);
		table.setColumnHeader("comboBox", I18n.getText(application.getI18nBase() + ".column.level"));
		table.addContainerProperty("saveButton", Button.class, null);
		table.setColumnHeader("saveButton", "");
		fillTable();
		setCompositionRoot(canvas);

		// TODO add user code here
	}
	

    public void fillTable() {
    	LOG.debug("fill Table()");
    	table.removeAllItems();
    	addLoggerItem(Logger.getRootLogger());
    	
        for (Logger logger : getLoggers()) {
        	LOG.debug("Logger added: " + logger.getName());
        	addLoggerItem(logger);
		}
	}


	@SuppressWarnings("unchecked")
	private Item addLoggerItem(Logger logger) {
		String loggerName = (logger.getName().equals("") ? ROOT : logger.getName());
		String inherited = logger.getLevel() == null?"*":"";
		String currentLevel = logger.getEffectiveLevel().toString();
		
		Item item = table.addItem(loggerName);
		item.getItemProperty("title").setValue(loggerName);
		item.getItemProperty("inherited").setValue(inherited);
		
		ComboBox comboBox = new ComboBox();
		for (String level : LEVELS) {
			comboBox.addItem(level);
		}
		comboBox.select(currentLevel);
		item.getItemProperty("comboBox").setValue(comboBox);
		
		Button saveButton = new Button("Save");
		saveButton.addClickListener(new ClickSaveListener(this,loggerName, comboBox));
		item.getItemProperty("saveButton").setValue(saveButton);
		
		return item;
	}


    private List<Logger> getLoggers()
    {
        @SuppressWarnings("unchecked")
		Enumeration<Logger> enm = LogManager.getCurrentLoggers();

        List<Logger> list = new ArrayList<Logger>();

        // Add all current loggers to the list
        while (enm.hasMoreElements())
        {
            list.add(enm.nextElement());
        }
        
        Collections.sort(list, new Comparator<Logger>() {

			@Override
			public int compare(Logger o1, Logger o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

        return list;
    }

}