
package org.mgnl.nicki.scheduler;

import lombok.Data;


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

/**
 * The Class JobConfig.
 */
@Data
public class JobConfig {
	
	/** The active. */
	private String active;
	
	/** The rule. */
	private String rule;
	
	/** The name. */
	private String name;
	
	/** The group. */
	private String group;
	
	/** The job class name. */
	private String jobClassName;
	
	/** The cron schedule. */
	private String cronSchedule;
}
