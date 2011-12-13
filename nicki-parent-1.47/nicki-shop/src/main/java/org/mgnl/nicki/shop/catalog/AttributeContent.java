/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;

import org.mgnl.nicki.dynamic.objects.objects.Person;

public interface AttributeContent {

	<T extends Object > T getContent(Class<T> classDefinition, Person user, Person person);

}
