package org.mgnl.nicki.shop.catalog;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface AttributeContent {

	<T extends Object > T getContent(Class<?> T, Person user, Person person);

}
