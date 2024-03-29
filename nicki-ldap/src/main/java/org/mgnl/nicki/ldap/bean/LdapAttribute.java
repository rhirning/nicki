package org.mgnl.nicki.ldap.bean;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 - 2024 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LdapAttribute {

	String ldapName();
	BeanSearchHandler.FORMATTER formatter() default BeanSearchHandler.FORMATTER.NO;
	boolean isDate() default false;
	boolean isLdapAttribute() default true;
	boolean dn() default false;
	boolean naming() default false;
	String displayName() default "";
	boolean hidden() default false;
	String format() default "";
}
