package org.mgnl.nicki.vaadin.base.menu.application;

import java.util.List;

public class ApplicationChapter {
	private String chapter;
	private List<ApplicationView> views;
	private List<String> groups;
	private List<String> roles;
	
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

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}


}
