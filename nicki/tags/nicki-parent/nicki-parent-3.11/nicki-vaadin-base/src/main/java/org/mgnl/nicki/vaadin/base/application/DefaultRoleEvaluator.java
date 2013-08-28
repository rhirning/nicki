package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public class DefaultRoleEvaluator implements AccessRoleEvaluator{

	@Override
	public boolean hasRole(Person person, String group) {
		return person.isMemberOf(group);
	}

}
