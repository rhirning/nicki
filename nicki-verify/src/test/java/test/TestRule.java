package test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.verify.VerifyException;
import org.mgnl.nicki.verify.annotations.Attribute;
import org.mgnl.nicki.verify.annotations.Verify;
import org.mgnl.nicki.verify.annotations.VerifyRule;

@Verify({@Attribute(name="userId", type=String.class)  })
public class TestRule {

	@VerifyRule
	public void attributeAvailableTest(Map<String, Object> data) {
		System.out.println("attributeAvailableTest");
		
	}
	
	@VerifyRule
	public void attributeTest(Map<String, Object> data) throws VerifyException {
		System.out.println("attributeTest");
		String userId = (String) data.get("userId");
		if (! StringUtils.equals(userId, "rhirning")) {
			throw new VerifyException("userId is wrong");
		}
		
	}
	
	@VerifyRule
	public void errorListTest(Map<String, Object> data) throws VerifyException {
		System.out.println("errorListTest");
		List<String> errors = new ArrayList<>();
		String userId = (String) data.get("userId");
		if (! StringUtils.equals(userId, "rhirning")) {
			errors.add("userId is wrong");
		}
		if (errors.size() > 0) {
			throw new VerifyException(errors);
		}
		
	}
	
	

}
