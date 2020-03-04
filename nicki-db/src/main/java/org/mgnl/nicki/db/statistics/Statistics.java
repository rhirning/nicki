package org.mgnl.nicki.db.statistics;

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


import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Table(name = "STATISTICS")
public class Statistics {
	
	@Attribute(name = "ID", autogen=true, primaryKey=true)
	private Long id;

	@Attribute(name = "CATEGORY")
	private String category;

	@Attribute(name = "NAME")
	private String name;

	@Attribute(name = "CONTEXT")
	private String context;

	@Attribute(name = "DATA")
	private String data;
	
	private StatisticsDefinition definition;
	
	public StatisticsResult execute(Map<String, String> input) throws StatisticsException, MissingDataException {
		DBContext dbContext = DBContextManager.getContext(context);
		try {
			StatisticsSelectHandler handler = new StatisticsSelectHandler(dbContext, definition, input);
			dbContext.select(handler);
			return handler;
		} catch (SQLException | InitProfileException | ParseException e) {
			throw new StatisticsException(e);
		}
	}
	public void setData(String data) {
		this.data = data;
		try {
			definition = JsonHelper.toBean(StatisticsDefinition.class, data);
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			log.error("Error parsing statistics definition", e);
		}
	}

}
