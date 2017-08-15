import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mgnl.nicki.verify.classes.JavaRules;
import org.mgnl.nicki.verify.classes.MissingAttributeException;
import org.mgnl.nicki.verify.classes.ReferencedError;

import test.TestRule;

public class RulesTest {

	@Test
	public void test() throws MissingAttributeException {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "Thing");
		List<ReferencedError> errors = JavaRules.evaluate(data, TestRule.class);
		assertNotEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
	
	@Test
	public void test2() throws MissingAttributeException, ClassNotFoundException {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "rhirning");
		List<ReferencedError> errors = JavaRules.evaluate(data, "test.TestRule");
		assertEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
}
