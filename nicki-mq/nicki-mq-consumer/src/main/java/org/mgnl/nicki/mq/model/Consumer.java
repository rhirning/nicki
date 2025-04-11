
package org.mgnl.nicki.mq.model;

import lombok.Data;

// TODO: Auto-generated Javadoc
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

/**
 * The Class Consumer.
 */
@Data
public class Consumer {
	
	/** The name. */
	private String name;
	
	/** The base. */
	private String base;
	
	/** The destination. */
	private String destination;
	
	/** The listener. */
	private String listener;
	
	/** The selector. */
	private String selector;
	
	/** The start. */
	private String start;
	
	/** The rule. */
	private String rule;
}
