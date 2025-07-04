package org.mgnl.nicki.db.context.hsqldb;

/*-
 * #%L
 * nicki-db
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

import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;


/**
 * The Class HsqldbContext.
 */
public class HsqldbContext extends BaseDBContext implements DBContext {
	
	/**
	 * Gets the sequence number.
	 *
	 * @param sequenceName the sequence name
	 * @return the sequence number
	 * @throws Exception the exception
	 */
	@Override
	public Long getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new HsqldbSequenceValueSelectHandler(getQualifiedName(sequenceName));
		select(handler);
		return handler.getResult();
	}
}
