package org.mgnl.nicki.vaadin.base.components;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Downloader {


	public static void showDownload(String title,  StreamResource source) {
		FileDownloader fileDownloader = null;
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		Button downloadButton = new Button("Download");
		downloadButton.setHeight("-1px");
		downloadButton.setWidth("-1px");
		fileDownloader = new FileDownloader(source);
		if (fileDownloader != null) {
			fileDownloader.extend(downloadButton);
		}
		layout.addComponent(downloadButton);
		Window window = new Window(title, layout);
		window.setHeight("200px");
		window.setWidth("200px");
		window.setModal(true);
		UI.getCurrent().addWindow(window);
	}
	
}
