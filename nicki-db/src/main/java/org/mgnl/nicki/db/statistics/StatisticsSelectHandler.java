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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.handler.SelectHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StatisticsSelectHandler implements SelectHandler, StatisticsResult {
	private DBContext dbContext;
	private StatisticsDefinition definition;
	private @Getter Map<String, String> result = new HashMap<>();
	private @Getter List<Map<String, String>> resultList = new ArrayList<>();
	private Map<String, Variable> inputVariables = new HashMap<>();
	private Map<String, Variable> outputVariables = new HashMap<>();
	private String searchStatement;
	
	public StatisticsSelectHandler(DBContext dbContext, StatisticsDefinition definition, Map<String, String> values) throws ParseException, MissingDataException {
		this.dbContext = dbContext;
		this.definition = definition;
		if (definition.getInput() != null) {
			for (Variable inputVariable : definition.getInput()) {
				inputVariables.put(inputVariable.getName(), inputVariable);
			}
		}
		if (definition.getOutput() != null) {
			for (Variable outputVariable : definition.getOutput()) {
				outputVariables.put(outputVariable.getName(), outputVariable);
			}
		}
		this.searchStatement = parseQuery(definition.getQuery(), definition.getInput(), values);
		log.debug(searchStatement);
	}

	@Override
	public String getSearchStatement() {
		return this.searchStatement;
	}

	/**
	 * Replace all variables with the values: ${variableName}
	 * @param query String
	 * @param variables 
	 * @return
	 * @throws ParseException 
	 * @throws MissingDataException 
	 */
	private String parseQuery(String query, List<Variable> variables, Map<String, String> values) throws ParseException, MissingDataException {
		List<String> missing = new ArrayList<>();
		String result = query;
		// search variables
		String varRegex ="\\$\\{(\\w*)\\}";
		Pattern pattern = Pattern.compile(varRegex);
		
		Matcher m = pattern.matcher(query);
		while (m.find()) {
			String key = m.group(1);
			Variable variable = inputVariables.get(key);
			if (StringUtils.isBlank(values.get(key))) {
				missing.add(key);
			} else {
				result = StringUtils.replace(result, "${" + key + "}", variable.toString(dbContext, values.get(key)));
			}
		}
		
		if (missing.size() > 0) {
			throw new MissingDataException(DataHelper.getAsString(missing, ","));
		}
		
		String contextRegex = "\\$\\{context.table\\((\\w*)\\)\\}";
		pattern = Pattern.compile(contextRegex);
		
		m = pattern.matcher(query);
		while (m.find()) {
			String tableName = m.group(1);
			result = StringUtils.replace(result, "${context.table(" + tableName + ")}", dbContext.getQualifiedName(tableName));
		}
		
		return result;
	}

	@Override
	public void handle(ResultSet resultSet) throws SQLException {
		boolean first = true;
		while (resultSet.next()) {
			Map<String, String> resultMap = new HashMap<>();
			for (Variable variable : definition.getOutput()) {
				if (resultSet.getObject(variable.getName()) != null) {
					try {
						String outputString = variable.toString(resultSet);
						resultMap.put(variable.getName(), outputString);
					} catch (SQLException e) {
						log.error("Error parsing output", e);
					}
				}
			}
			resultList.add(resultMap);
			if (first) {
				result = resultMap;
			}
			first = false;
		}
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

}
