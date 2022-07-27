package org.mgnl.nicki.db.verify;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BeanVerifyError extends Exception {
	private static final long serialVersionUID = 4965092676208331051L;

	private List<String> errors;
	
	public BeanVerifyError(List<String> errors) {
		this.errors = errors;
	}
}
