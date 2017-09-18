
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.dynamic.objects.objects.EngineTemplate;

@XmlRootElement
public class SimpleTemplate implements EngineTemplate {
	
	private String name;
	private String data;
	private String filePath;
	private Map<String, String> part;
	
	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public Map<String, String> getPart() {
		return this.part;
	}

	public void setPart(Map<String, String> part) {
		this.part = part;
	}
	public boolean containsKey(String key) {
		return this.part.containsKey(key);
	}

	public String getPart(String key) {
		return part.get(key);
	}
	public String getName() {
		return name;
	}
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public byte[] getFile() {
		if (getFilePath() != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				IOUtils.copy(getClass().getResourceAsStream(getFilePath()), out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return out.toByteArray();
		}
		return null;
	}

	public String getFilePath() {
		return filePath;
	}

	@XmlAttribute
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
