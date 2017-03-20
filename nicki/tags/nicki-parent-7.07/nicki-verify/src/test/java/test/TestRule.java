package test;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.verify.annotations.Attribute;
import org.mgnl.nicki.verify.classes.ReferenceVerifyException;
import org.mgnl.nicki.verify.classes.ReferencedError;
import org.mgnl.nicki.verify.classes.ReferencedError.TYPE;
import org.mgnl.nicki.verify.annotations.VerifyRule;

public class TestRule {
	@Attribute
	private String userId;
	
	@VerifyRule
	public void attributeAvailableTest() {
		System.out.println("attributeAvailableTest");
		System.out.println("userId=" + userId);
		
	}
	
	@VerifyRule
	public void attributeTest() throws ReferenceVerifyException {
		System.out.println("attributeTest");
		if (! StringUtils.equals(userId, "rhirning")) {
			throw new ReferenceVerifyException(new ReferencedError(TYPE.ERROR, "userId", "userId is wrong"));
		}
		
	}
	
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
