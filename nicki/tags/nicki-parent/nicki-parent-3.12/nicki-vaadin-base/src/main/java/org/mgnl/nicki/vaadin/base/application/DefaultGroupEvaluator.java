package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public class DefaultGroupEvaluator implements AccessGroupEvaluator{

	@Override
	public boolean isMemberOf(Person person, String group) {
		// TODO Auto-generated method stub
		return person.isMemberOf(group);
	}

}