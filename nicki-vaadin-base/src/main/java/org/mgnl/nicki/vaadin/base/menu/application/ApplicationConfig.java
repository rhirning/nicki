package org.mgnl.nicki.vaadin.base.menu.application;

import java.util.List;

import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.vaadin.base.application.AccessGroupEvaluator;
import org.mgnl.nicki.vaadin.base.application.AccessRoleEvaluator;
import org.mgnl.nicki.vaadin.base.application.DefaultGroupEvaluator;
import org.mgnl.nicki.vaadin.base.application.DefaultRoleEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);
	private AccessGroupEvaluator accessGroupEvaluator = new DefaultGroupEvaluator();
	private AccessRoleEvaluator accessRoleEvaluator = new DefaultRoleEvaluator();
	private String accessGroupEvaluatorClass;
	private String accessRoleEvaluatorClass;
	private ApplicationView start;
	private List<ApplicationChapter> chapters;

	public List<ApplicationChapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<ApplicationChapter> chapters) {
		this.chapters = chapters;
	}

	public String getAccessGroupEvaluatorClass() {
		return accessGroupEvaluatorClass;
	}

	public void setAccessGroupEvaluatorClass(String accessGroupEvaluatorClass) {
		this.accessGroupEvaluatorClass = accessGroupEvaluatorClass;
		try {
			this.accessGroupEvaluator = Classes.newInstance(accessGroupEvaluatorClass);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOG.error("Invalid AccessGroupEvaluatorClass", e);
		}
	}

	public String getAccessRoleEvaluatorClass() {
		return accessRoleEvaluatorClass;
	}

	public void setAccessRoleEvaluatorClass(String accessRoleEvaluatorClass) {
		this.accessRoleEvaluatorClass = accessRoleEvaluatorClass;
		try {
			this.accessRoleEvaluator = Classes.newInstance(accessRoleEvaluatorClass);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOG.error("Invalid AccessRoleEvaluatorClass", e);
		}
	}

	public boolean isAllowed(Person person, List<String> groups, List<String> roles) {
		if (isEmpty(groups) && isEmpty(roles)) {
			return true;
		}
		if (!isEmpty(groups)) {
			if (accessGroupEvaluator.isMemberOf(person, groups.toArray(new String[0]))) {
				return true;
			}
		}
		if (!isEmpty(roles)) {
			if (accessRoleEvaluator.hasRole(person, groups.toArray(new String[0]))) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmpty(List<String> groups) {
		return groups == null || groups.size() == 0;
	}

	public ApplicationView getStart() {
		return start;
	}

	public void setStart(ApplicationView start) {
		this.start = start;
	}


}
