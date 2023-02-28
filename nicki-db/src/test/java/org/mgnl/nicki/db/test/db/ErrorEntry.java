package org.mgnl.nicki.db.test.db;

import java.util.Date;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;

import lombok.Data;

@Data
@Table(name = "ERROR")
public class ErrorEntry {
	
	@Attribute(name = "ID", autogen=true, primaryKey = true)
	private Long id;

	@Attribute(name = "COMMAND", length = 64)
	private String command;

	@Attribute(name = "MODIFY_TIME", now=true, type=DataType.TIMESTAMP)
	private Date time;
	
	@Attribute(name = "USER_ID", length = 64)
	private String userId;

	@Attribute(name = "CODE", length = 64)
	private String code;

	@Attribute(name = "DATA")
	private String data;
	
}
