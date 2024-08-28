package org.mgnl.nicki.db.context.hsqldb;

import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HsqldbSequenceValueSelectHandler extends SequenceValueSelectHandler implements SelectHandler {


	public HsqldbSequenceValueSelectHandler(String sequenceName) {
		super(sequenceName);
	}

	public String getSearchStatement() {
		String statement = "VALUES NEXT VALUE FOR " + getSequenceName();
		log.debug(statement);
		return statement;
	}
}
