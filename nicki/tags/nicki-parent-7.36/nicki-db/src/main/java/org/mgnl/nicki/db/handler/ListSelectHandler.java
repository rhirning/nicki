package org.mgnl.nicki.db.handler;

import java.util.List;

public interface ListSelectHandler<T> extends SelectHandler {
	List<T> getResults();

}
