package org.mgnl.nicki.db.statistics;

import java.util.List;

public class StatisticsDefinition {
	
	private String query;

	private List<Variable> input;

	private List<Variable> output;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<Variable> getInput() {
		return input;
	}
	public void setInput(List<Variable> input) {
		this.input = input;
	}
	public List<Variable> getOutput() {
		return output;
	}
	public void setOutput(List<Variable> output) {
		this.output = output;
	}
	
}
