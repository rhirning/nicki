
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;


/**
 * The Class SimpleTemplate.
 */
@XmlRootElement
@Slf4j
public class SimpleTemplate implements EngineTemplate {
	
	/** The name. */
	private String name;
	
	/** The data. */
	private String data;
	
	/** The file path. */
	private String filePath;
	
	/** The part. */
	private Map<String, String> part;
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String getData() {
		return this.data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * Gets the part.
	 *
	 * @return the part
	 */
	public Map<String, String> getPart() {
		return this.part;
	}

	/**
	 * Sets the part.
	 *
	 * @param part the part
	 */
	public void setPart(Map<String, String> part) {
		this.part = part;
	}
	
	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(String key) {
		return this.part.containsKey(key);
	}

	/**
	 * Gets the part.
	 *
	 * @param key the key
	 * @return the part
	 */
	public String getPart(String key) {
		return part.get(key);
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	@Override
	public byte[] getFile() {
		if (getFilePath() != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				IOUtils.copy(getClass().getResourceAsStream(getFilePath()), out);
			} catch (IOException e) {
				log.error("Error reading file", e);
			}
			return out.toByteArray();
		}
		return null;
	}

	/**
	 * Gets the file path.
	 *
	 * @return the file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the file path.
	 *
	 * @param filePath the new file path
	 */
	@XmlAttribute
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
