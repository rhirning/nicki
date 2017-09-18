
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerbySequenceValueSelectHandler extends SequenceValueSelectHandler implements SelectHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DerbySequenceValueSelectHandler.class);

	public DerbySequenceValueSelectHandler(String sequenceName) {
		super(sequenceName);
	}

	public String getSearchStatement() {
		String statement = "VALUES NEXT VALUE FOR " + getSequenceName();
		LOG.debug(statement);
		return statement;
	}
}

