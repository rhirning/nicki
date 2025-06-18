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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mgnl.nicki.verify.classes.JavaRules;
import org.mgnl.nicki.verify.classes.MissingAttributeException;
import org.mgnl.nicki.verify.classes.ReferencedError;

import test.TestRule;


/**
 * The Class RulesTest.
 */
public class RulesTest {

	/**
	 * Test.
	 *
	 * @throws MissingAttributeException the missing attribute exception
	 */
	@Test
	public void test() throws MissingAttributeException {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "Thing");
		List<ReferencedError> errors = JavaRules.evaluate(data, TestRule.class);
		assertNotEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
	
	/**
	 * Test 2.
	 *
	 * @throws MissingAttributeException the missing attribute exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@Test
	public void test2() throws MissingAttributeException, ClassNotFoundException {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "rhirning");
		List<ReferencedError> errors = JavaRules.evaluate(data, "test.TestRule");
		assertEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
}
