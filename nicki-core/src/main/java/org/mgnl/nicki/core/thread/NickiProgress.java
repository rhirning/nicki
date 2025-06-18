
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
 * The Interface NickiProgress.
 */
public interface NickiProgress {

	/**
	 * Progressed.
	 *
	 * @param newCurrent the new current
	 * @param newDetails the new details
	 */
	void progressed(int newCurrent, String newDetails);

	/**
	 * Finish.
	 */
	void finish();

	/**
	 * Inits the.
	 *
	 * @param progressRunner the progress runner
	 */
	void init(ProgressRunner progressRunner);

}
