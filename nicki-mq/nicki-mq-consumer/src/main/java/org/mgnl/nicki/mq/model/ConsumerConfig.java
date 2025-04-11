
package org.mgnl.nicki.mq.model;

/*-
 * #%L
 * nicki-mq
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


import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsumerConfig.
 */
public class ConsumerConfig {
	
	/** The consumers. */
	private List<Consumer> consumers;

	/**
	 * Gets the consumers.
	 *
	 * @return the consumers
	 */
	public List<Consumer> getConsumers() {
		return consumers;
	}

	/**
	 * Sets the consumers.
	 *
	 * @param consumers the new consumers
	 */
	public void setConsumers(List<Consumer> consumers) {
		this.consumers = consumers;
	}

}
