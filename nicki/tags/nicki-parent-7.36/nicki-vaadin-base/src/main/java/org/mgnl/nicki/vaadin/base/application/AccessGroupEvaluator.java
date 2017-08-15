package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface AccessGroupEvaluator {

	boolean isMemberOf(Person person, String... groups);
}
