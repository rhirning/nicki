
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


public class Consumer {
	private String name;
	private String base;
	private String destination;
	private String listener;
	private String selector;
	private boolean start;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getListener() {
		return listener;
	}
	public void setListener(String listener) {
		this.listener = listener;
	}
	public String getSelector() {
		return selector;
	}
	public void setSelector(String selector) {
		this.selector = selector;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Consumer:[");
		sb.append("name=").append(name);
		sb.append(", start=").append(start);
		sb.append(", base=").append(base);
		sb.append(", destination=").append(destination);
		sb.append(", listener=").append(listener);
		sb.append(", selector=").append(selector);
		sb.append("]");
		return sb.toString();
	}

}
