
package org.mgnl.nicki.core.data;

/*-
 * #%L
 * nicki-core
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


import lombok.extern.slf4j.Slf4j;


/**
 * The Class OctetString.
 */
@Slf4j
public class OctetString {
	
	/** The octet string. */
	byte octetString[];

	/**
	 * Instantiates a new octet string.
	 *
	 * @param octet the octet
	 */
	public OctetString(byte octet[]) {
		this.octetString = octet;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return octetString;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		try {
			String result = "";
			for (int i = 0; i < octetString.length; i++) {
				result += Integer.toString((octetString[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
			return result;
		} catch (Exception e) {
			log.error("Error", e);
		}
		return null;
	}

}
