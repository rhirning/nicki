
package org.mgnl.nicki.dynamic.objects.shop;

/*-
 * #%L
 * nicki-dynamic-objects
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


import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;

@SuppressWarnings("serial")
public class AssignedArticle implements Serializable{
	private String source;
	private String articleId;
	private String specifier;
	private String targetDn;
	private Date start;
	private Date end;
	
	
	public AssignedArticle(String text) {
		super();
		String entry[] = StringUtils.split(text, "#");
		this.source = entry[0];
		this.articleId = entry[1];
		this.specifier = entry[2];
		this.targetDn = entry[3];
		try {
			this.start = DataHelper.dateFromString(entry[4]);
		} catch (ParseException e) {
			this.start = null;
		}
		try {
			this.end = DataHelper.dateFromString(entry[5]);
		} catch (ParseException e) {
			this.start = null;
		}
	}
	
	public String getSource() {
		return source;
	}


	public String getArticleId() {
		return articleId;
	}


	public String getTargetDn() {
		return targetDn;
	}

	public String getSpecifier() {
		return specifier;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}
	
	
}
