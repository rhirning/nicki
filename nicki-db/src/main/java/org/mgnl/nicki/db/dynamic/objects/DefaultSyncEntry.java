package org.mgnl.nicki.db.dynamic.objects;

import java.io.Serializable;

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

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The Class DefaultSyncEntry.
 */
@Data @NoArgsConstructor
@Table(name = "DYNAMIC_OBJECTS")
public class DefaultSyncEntry implements SyncEntry, SyncIdGenerator, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4837806177766451088L;

	/** The unique id. */
	@Attribute(name = "UNIQUE_ID", autogen=true, primaryKey=true)
	private Long uniqueId;
	
	/** The type. */
	@Attribute(name = "ENTRY_TYPE")
	private String type;
	
	/** The id. */
	@Attribute(name = "ID")
	private String id;

	/** The from. */
	@Attribute(name = "FROM_TIME", type=DataType.TIMESTAMP)
	private Date from;

	/** The to. */
	@Attribute(name = "TO_TIME", type=DataType.TIMESTAMP)
	private Date to;
	
	/** The attribute. */
	@Attribute(name = "ATTRIBUTE")
	private String attribute;
	
	/** The content. */
	@Attribute(name = "CONTENT")
	private String content;

	/**
	 * Gets the id.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the id
	 */
	@Override
	public String getId(DynamicObject dynamicObject) {
		return dynamicObject.getNamingValue();
	}
	
	
}
