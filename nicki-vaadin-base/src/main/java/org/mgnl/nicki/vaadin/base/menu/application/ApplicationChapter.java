package org.mgnl.nicki.vaadin.base.menu.application;

import java.util.List;

public class ApplicationChapter {
	private String chapter;
	private List<ApplicationView> views;

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public List<ApplicationView> getViews() {
		return views;
	}

	public void setViews(List<ApplicationView> views) {
		this.views = views;
	}


}
