package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface AccessRoleEvaluator {

	boolean hasRole(Person person, String[] roles);
}
