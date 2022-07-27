package org.mgnl.nicki.db.verify;

import org.mgnl.nicki.db.context.DBContext;

public interface BeanUpdater {
	void update(DBContext dbContext, Object bean) throws UpdateBeanException;
}
