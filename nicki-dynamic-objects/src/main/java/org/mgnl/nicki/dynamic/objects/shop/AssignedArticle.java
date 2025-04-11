
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class AssignedArticle.
 */
@SuppressWarnings("serial")
public class AssignedArticle implements Serializable{
	
	/** The source. */
	private String source;
	
	/** The article id. */
	private String articleId;
	
	/** The specifier. */
	private String specifier;
	
	/** The target dn. */
	private String targetDn;
	
	/** The start. */
	private Date start;
	
	/** The end. */
	private Date end;
	
	
	/**
	 * Instantiates a new assigned article.
	 *
	 * @param text the text
	 */
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
	
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}


	/**
	 * Gets the article id.
	 *
	 * @return the article id
	 */
	public String getArticleId() {
		return articleId;
	}


	/**
	 * Gets the target dn.
	 *
	 * @return the target dn
	 */
	public String getTargetDn() {
		return targetDn;
	}

	/**
	 * Gets the specifier.
	 *
	 * @return the specifier
	 */
	public String getSpecifier() {
		return specifier;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}
	
	
}
