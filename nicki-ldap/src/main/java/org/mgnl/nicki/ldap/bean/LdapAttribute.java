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



/**
 * The Interface LdapAttribute.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LdapAttribute {

	/**
	 * Ldap name.
	 *
	 * @return the string
	 */
	String ldapName();
	
	/**
	 * Formatter.
	 *
	 * @return the bean search handler. FORMATTER
	 */
	BeanSearchHandler.FORMATTER formatter() default BeanSearchHandler.FORMATTER.NO;
	
	/**
	 * Checks if is date.
	 *
	 * @return true, if is date
	 */
	boolean isDate() default false;
	
	/**
	 * Checks if is ldap attribute.
	 *
	 * @return true, if is ldap attribute
	 */
	boolean isLdapAttribute() default true;
	
	/**
	 * Dn.
	 *
	 * @return true, if successful
	 */
	boolean dn() default false;
	
	/**
	 * Naming.
	 *
	 * @return true, if successful
	 */
	boolean naming() default false;
	
	/**
	 * Display name.
	 *
	 * @return the string
	 */
	String displayName() default "";
	
	/**
	 * Hidden.
	 *
	 * @return true, if successful
	 */
	boolean hidden() default false;
	
	/**
	 * Format.
	 *
	 * @return the string
	 */
	String format() default "";
}
