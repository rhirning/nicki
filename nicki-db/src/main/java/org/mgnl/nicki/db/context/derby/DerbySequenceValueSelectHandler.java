
package org.mgnl.nicki.db.context.derby;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 Ralf Hirning
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


import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class DerbySequenceValueSelectHandler.
 */
@Slf4j
public class DerbySequenceValueSelectHandler extends SequenceValueSelectHandler implements SelectHandler {


	/**
	 * Instantiates a new derby sequence value select handler.
	 *
	 * @param sequenceName the sequence name
	 */
	public DerbySequenceValueSelectHandler(String sequenceName) {
		super(sequenceName);
	}

	/**
	 * Gets the search statement.
	 *
	 * @return the search statement
	 */
	public String getSearchStatement() {
		String statement = "VALUES NEXT VALUE FOR " + getSequenceName();
		log.debug(statement);
		return statement;
	}
}

