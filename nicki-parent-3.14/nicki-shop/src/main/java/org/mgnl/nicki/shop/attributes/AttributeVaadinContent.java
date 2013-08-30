package org.mgnl.nicki.shop.attributes;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface AttributeVaadinContent {

	VaadinComponent getVaadinContent(Person user, Person person);

}
