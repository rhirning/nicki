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
package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SequenceValueSelectHandler.class);
	
	private String sequenceName;
	private long result = -1;

	public SequenceValueSelectHandler(String sequenceName) {
		this.sequenceName = sequenceName;
	}


	public String getSearchStatement() {
		String statement = "select " + getSequenceName() + ".nextval from dual";
		LOG.debug(statement);
		return statement;
	}


	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getLong(1);
		}
	}


	public long getResult() {
		return result;
	}


	public String getSequenceName() {
		return sequenceName;
	}
}

