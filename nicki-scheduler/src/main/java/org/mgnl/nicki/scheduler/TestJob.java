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


import java.util.Date;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * The Class TestJob.
 */
@Slf4j
@Data
public class TestJob implements Job {
	
	/** The job config. */
	private JobConfig jobConfig;
    
    /**
     * Run.
     */
    public void run() {

        // This job simply prints out its jobConfig and the
        // date and time that it is running
        log.info(getClass().getSimpleName() + " says: " + jobConfig + " executing at " + new Date());
    }

}
