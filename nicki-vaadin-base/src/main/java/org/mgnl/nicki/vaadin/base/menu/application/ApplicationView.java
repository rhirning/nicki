package org.mgnl.nicki.vaadin.base.menu.application;

import java.util.List;
import java.util.Map;

public class ApplicationView {
	private String title;
	private String view;
	private List<String> groups;
	private List<String> roles;
	private Map<String, String> configuration;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
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
	public Map<String, String> getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

}
