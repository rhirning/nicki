
package test;

/*-
 * #%L
 * nicki-verify
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.verify.annotations.Attribute;
import org.mgnl.nicki.verify.classes.ReferenceVerifyException;
import org.mgnl.nicki.verify.classes.ReferencedError;
import org.mgnl.nicki.verify.classes.ReferencedError.TYPE;
import org.mgnl.nicki.verify.annotations.VerifyRule;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRule.
 */
public class TestRule {
	
	/** The user id. */
	@Attribute
	private String userId;
	
	/**
	 * Attribute available test.
	 */
	@VerifyRule
	public void attributeAvailableTest() {
		System.out.println("attributeAvailableTest");
		System.out.println("userId=" + userId);
		
	}
	
	/**
	 * Attribute test.
	 *
	 * @throws ReferenceVerifyException the reference verify exception
	 */
	@VerifyRule
	public void attributeTest() throws ReferenceVerifyException {
		System.out.println("attributeTest");
		if (! StringUtils.equals(userId, "rhirning")) {
			throw new ReferenceVerifyException(new ReferencedError(TYPE.ERROR, "userId", "userId is wrong"));
		}
		
	}
	
	/**
	 * Error list test.
	 *
	 * @throws ReferenceVerifyException the reference verify exception
	 */
	@VerifyRule
	public void errorListTest() throws ReferenceVerifyException {
		System.out.println("errorListTest");
		List<ReferencedError> errors = new ArrayList<>();
		if (! StringUtils.equals(userId, "rhirning")) {
			errors.add(new ReferencedError(TYPE.ERROR, "userId", "userId is wrong"));
		}
		if (errors.size() > 0) {
			throw new ReferenceVerifyException(errors);
		}
		
	}
	
}
