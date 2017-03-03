import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mgnl.nicki.verify.annotations.JavaRules;
import org.mgnl.nicki.verify.annotations.MissingAttribute;

import test.TestRule;

public class RulesTest {

	@Test
	public void test() throws MissingAttribute {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "Thing");
		List<String> errors = JavaRules.evaluate(data, TestRule.class);
		assertNotEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
	
	@Test
	public void test2() throws MissingAttribute, ClassNotFoundException {
		Map<String, Object> data = new HashMap<>();
		data.put("userId", "Thing");
		List<String> errors = JavaRules.evaluate(data, "test.TestRule");
		assertNotEquals("Anzahl Fehler", 0, errors.size());
		System.out.println(errors);
	}
}
