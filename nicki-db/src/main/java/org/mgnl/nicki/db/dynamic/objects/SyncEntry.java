package org.mgnl.nicki.db.dynamic.objects;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Interface SyncEntry.
 */
public interface SyncEntry {

	
	/**
	 * Sets the unique id.
	 *
	 * @param uniqueId the new unique id
	 */
	void setUniqueId(Long uniqueId);
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	void setType(String type);
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	void setId(String id);
	
	/**
	 * Sets the from.
	 *
	 * @param from the new from
	 */
	void setFrom(Date from);
	
	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 */
	void setTo(Date to);
	
	/**
	 * Sets the attribute.
	 *
	 * @param attribute the new attribute
	 */
	void setAttribute(String attribute);
	
	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	void setContent(String content);
	
	
	/**
	 * Gets the unique id.
	 *
	 * @return the unique id
	 */
	Long getUniqueId();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	String getType();
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	Date getFrom();

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	Date getTo();
	
	/**
	 * Gets the attribute.
	 *
	 * @return the attribute
	 */
	String getAttribute();
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	String getContent();
}
