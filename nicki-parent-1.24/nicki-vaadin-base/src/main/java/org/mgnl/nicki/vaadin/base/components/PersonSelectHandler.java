package org.mgnl.nicki.vaadin.base.components;

import java.io.Serializable;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface PersonSelectHandler extends Serializable {

	void closePersonSelector();

	void setSelectedPerson(Person entry);

}
