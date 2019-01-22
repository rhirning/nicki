package org.mgnl.nicki.db.dynamic.objects;

import java.util.Date;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@Table(name = "DYNAMIC_OBJECTS")
public class DefaultSyncEntry implements SyncEntry, SyncIdGenerator {
	
	@Attribute(name = "UNIQUE_ID", autogen=true, primaryKey=true)
	private Long uniqueId;
	
	@Attribute(name = "ENTRY_TYPE")
	private String type;
	
	@Attribute(name = "ID")
	private String id;

	@Attribute(name = "FROM_TIME", type=DataType.TIMESTAMP)
	private Date from;

	@Attribute(name = "TO_TIME", type=DataType.TIMESTAMP)
	private Date to;
	
	@Attribute(name = "ATTRIBUTE")
	private String attribute;
	
	@Attribute(name = "CONTENT")
	private String content;

	@Override
	public String getId(DynamicObject dynamicObject) {
		return dynamicObject.getNamingValue();
	}
	
	
}
