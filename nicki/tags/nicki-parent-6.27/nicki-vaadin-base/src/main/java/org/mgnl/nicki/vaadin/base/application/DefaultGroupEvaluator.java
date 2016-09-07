package org.mgnl.nicki.vaadin.base.application;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public class DefaultGroupEvaluator implements AccessGroupEvaluator{

	@Override
	public boolean isMemberOf(Person person, String... groups) {
		if (groups != null) {
			for (String group : groups) {
				if (person.isMemberOf(group)) {
					return true;
				}
			}
		}
		return false;
	}

}
