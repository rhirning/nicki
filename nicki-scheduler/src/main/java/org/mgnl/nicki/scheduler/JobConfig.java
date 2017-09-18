
package org.mgnl.nicki.scheduler;

/*-
 * #%L
 * nicki-scheduler
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


public class JobConfig {
	private boolean active;
	private String name;
	private String group;
	private String JobClassName;
	private String cronSchedule;
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCronSchedule() {
		return cronSchedule;
	}
	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getJobClassName() {
		return JobClassName;
	}
	public void setJobClassName(String jobClassName) {
		JobClassName = jobClassName;
	}

}
