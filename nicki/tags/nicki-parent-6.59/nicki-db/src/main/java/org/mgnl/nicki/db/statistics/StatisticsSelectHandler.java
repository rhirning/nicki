package org.mgnl.nicki.db.statistics;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsSelectHandler implements SelectHandler {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsSelectHandler.class);
	private DBContext dbContext;
	private StatisticsDefinition definition;
	private Map<String, String> result = new HashMap<>();
	private Map<String, Variable> inputVariables = new HashMap<>();
	private Map<String, Variable> outputVariables = new HashMap<>();
	private String searchStatement;
	
	public StatisticsSelectHandler(DBContext dbContext, StatisticsDefinition definition, Map<String, String> values) throws ParseException, MissingDataException {
		this.dbContext = dbContext;
		this.definition = definition;
		for (Variable inputVariable : definition.getInput()) {
			inputVariables.put(inputVariable.getName(), inputVariable);
		}
		for (Variable outputVariable : definition.getOutput()) {
			outputVariables.put(outputVariable.getName(), outputVariable);
		}
		this.searchStatement = parseQuery(definition.getQuery(), definition.getInput(), values); 
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
			}
			result = StringUtils.replace(result, "${" + key + "}", variable.toString(dbContext, values.get(key)));
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
		if (resultSet.next()) {
			for (Variable variable : definition.getOutput()) {
				if (resultSet.getObject(variable.getName()) != null) {
					try {
						String outputString = variable.toString(resultSet);
						result.put(variable.getName(), outputString);
					} catch (SQLException e) {
						LOG.error("Error parsing output", e);
					}
				}
			}
		}
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	public Map<String, String> getResult() {
		return result;
	}

}
