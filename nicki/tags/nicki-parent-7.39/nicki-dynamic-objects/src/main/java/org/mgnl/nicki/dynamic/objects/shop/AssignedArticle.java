/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.shop;

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
