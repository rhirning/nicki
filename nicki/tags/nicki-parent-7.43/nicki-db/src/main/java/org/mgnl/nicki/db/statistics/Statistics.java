/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.db.statistics;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table(name = "STATISTICS")
public class Statistics {
	private static final Logger LOG = LoggerFactory.getLogger(Statistics.class);
	
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
	
	public Map<String, String> execute(Map<String, String> input) throws StatisticsException, MissingDataException {
		DBContext dbContext = DBContextManager.getContext(context);
		try {
			StatisticsSelectHandler handler = new StatisticsSelectHandler(dbContext, definition, input);
			dbContext.select(handler);
			return handler.getResult();
		} catch (SQLException | InitProfileException | ParseException e) {
			throw new StatisticsException(e);
		}
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		try {
			definition = JsonHelper.toBean(StatisticsDefinition.class, data);
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			LOG.error("Error parsing statistics definition", e);
		}
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public StatisticsDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(StatisticsDefinition definition) {
		this.definition = definition;
	}

}
