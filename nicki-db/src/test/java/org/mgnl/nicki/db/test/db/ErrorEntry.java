package org.mgnl.nicki.db.test.db;

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
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;

import lombok.Data;


/**
 * The Class ErrorEntry.
 */
@Data
@Table(name = "ERROR")
public class ErrorEntry {
	
	/** The id. */
	@Attribute(name = "ID", autogen=true, primaryKey = true)
	private Long id;

	/** The command. */
	@Attribute(name = "COMMAND", length = 64)
	private String command;

	/** The time. */
	@Attribute(name = "MODIFY_TIME", now=true, type=DataType.TIMESTAMP)
	private Date time;
	
	/** The user id. */
	@Attribute(name = "USER_ID", length = 64)
	private String userId;

	/** The code. */
	@Attribute(name = "CODE", length = 64)
	private String code;

	/** The data. */
	@Attribute(name = "DATA")
	private String data;
	
}
