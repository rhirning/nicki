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
