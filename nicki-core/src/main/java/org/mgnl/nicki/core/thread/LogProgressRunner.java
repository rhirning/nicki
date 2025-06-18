
package org.mgnl.nicki.core.thread;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * The Class LogProgressRunner.
 */
/*-
 * #%L
 * nicki-vaadin-base
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
@Slf4j
public abstract class LogProgressRunner extends ProgressRunner implements Runnable {

	/**
	 * Instantiates a new log progress runner.
	 *
	 * @param progress the progress
	 * @param title the title
	 * @param count the count
	 */
	public LogProgressRunner(NickiProgress progress, String title, int count) {
		super(progress, title, count);
	}


    /**
     * Run.
     */
    @Override
    public void run() {
    	doWork();
        
        // Show the "all done" for a while
        try {
            sleep(2000); // Sleep for 2 seconds
        } catch (InterruptedException e) {}

        getProgress().finish();
    }


	/**
	 * Progressed.
	 *
	 * @param newCurrent the new current
	 * @param newDetails the new details
	 */
	public void progressed(int newCurrent, String newDetails) {
		this.setCurrent(newCurrent);
		this.setDetails(newDetails);
		float value = Float.valueOf(getCurrent()) / getCount();
		if (StringUtils.isNotBlank(newDetails)) {
			log.info(this.toString() + ": Details: " + getDetails());
		}
		if (getCurrent() < getCount())
			log.info(this.toString() + ": " + ((int) (value * 100)) + "% ("+ getCurrent() + "/" + getCount() + ")");
		else
			log.info(this.toString() + ": finished");
	}
}
