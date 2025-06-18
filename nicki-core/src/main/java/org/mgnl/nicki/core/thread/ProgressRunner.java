
package org.mgnl.nicki.core.thread;


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

/**
 * The Class ProgressRunner.
 */
public abstract class ProgressRunner extends Thread implements Runnable {
	
	/** The title. */
	private String title;
	
	/** The count. */
	private int count;
	
	/** The current. */
	private int current;
	
	/** The details. */
	private String details;
	
	/** The progress. */
	private NickiProgress progress;

	/**
	 * Instantiates a new progress runner.
	 *
	 * @param progress the progress
	 * @param title the title
	 * @param count the count
	 */
	public ProgressRunner(NickiProgress progress, String title, int count) {
		super();
		this.title = title;
		this.count = count;
		this.progress = progress;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
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

        progress.finish();
    }

	/**
	 * Do work.
	 */
	public abstract void doWork();

	/**
	 * Progressed.
	 *
	 * @param newCurrent the new current
	 * @param newDetails the new details
	 */
	public abstract void progressed(int newCurrent, String newDetails);

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the current.
	 *
	 * @return the current
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * Sets the current.
	 *
	 * @param current the new current
	 */
	public void setCurrent(int current) {
		this.current = current;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 *
	 * @param details the new details
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Gets the progress.
	 *
	 * @return the progress
	 */
	public NickiProgress getProgress() {
		return progress;
	}
}
