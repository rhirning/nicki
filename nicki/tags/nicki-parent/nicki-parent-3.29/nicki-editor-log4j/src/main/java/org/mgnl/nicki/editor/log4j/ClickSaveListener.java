package org.mgnl.nicki.editor.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

public class ClickSaveListener implements ClickListener {
	private static final long serialVersionUID = 8056214403424949582L;
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ClickSaveListener.class);

    private static final String ROOT = "Root";
    
    Log4jViewer viewer;
	String loggerName;
	ComboBox comboBox;

	public ClickSaveListener(Log4jViewer viewer, String loggerName, ComboBox comboBox) {
		super();
		this.viewer = viewer;
		this.loggerName = loggerName;
		this.comboBox = comboBox;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		changeLogLevel(loggerName, (String) comboBox.getValue());
		viewer.fillTable();
	}

	private synchronized void changeLogLevel(String loggerName, String level) {
        Logger logger = null;

        try
        {
            logger = (ROOT.equalsIgnoreCase(loggerName) ? Logger.getRootLogger() : Logger.getLogger(loggerName));
            logger.setLevel(Level.toLevel(level));
        }
        catch (Throwable e)
        {
            LOG.debug("ERROR Setting LOG4J Logger:" + e);
        }

        Notification.show("LogLevel set for " + (logger.getName().equals("") ? ROOT : logger.getName()));
    }

}