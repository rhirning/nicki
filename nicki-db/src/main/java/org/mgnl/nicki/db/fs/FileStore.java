package org.mgnl.nicki.db.fs;

import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.data.DataType;

import lombok.Data;

@Table(name = "FILE_STORE")
@Data
public class FileStore {
	@Attribute(name = "ID", primaryKey = true, autogen = true)
	private Long id;

	@Attribute(name = "NAME")
	private String name;

	@Attribute(name = "DATA")
	private String data;

	@Attribute(name = "MODIFY_TIME", now = true, type = DataType.TIMESTAMP)
	private Date modifyTime;
}
