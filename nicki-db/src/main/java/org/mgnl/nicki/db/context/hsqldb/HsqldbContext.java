package org.mgnl.nicki.db.context.hsqldb;

import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;

public class HsqldbContext extends BaseDBContext implements DBContext {
	@Override
	public Long getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new HsqldbSequenceValueSelectHandler(getQualifiedName(sequenceName));
		select(handler);
		return handler.getResult();
	}
}
