package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public class DefaultRoleEvaluator implements AccessRoleEvaluator{

	@Override
	public boolean hasRole(Person person, String[] roles) {
		for (String role : roles) {
			if (person.hasRole(role)) {
				return true;
			}
		}
		return false;
	}

}
