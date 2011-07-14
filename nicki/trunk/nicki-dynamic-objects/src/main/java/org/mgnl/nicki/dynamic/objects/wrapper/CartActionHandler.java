/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mgnl.nicki.dynamic.objects.wrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.mgnl.nicki.ldap.context.NickiContext;

/**
 *
 * @author cna
 */
public class CartActionHandler extends BasicActionHandler {

	private static final String ACTIONNAME = "cart-action";

	private static enum ACTION_TYPE {
		ADD("add"),
		MODIFY("modify"),
		DELETE("delete");
		
		private String value;

		private ACTION_TYPE(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}	
	}

	public CartActionHandler(NickiContext ctx, String target) {
		super(ctx, ACTIONNAME, target, false);
	}

	public void add(String item) {
		putItem(ACTION_TYPE.ADD, item);
	}

	public void addAttributes(String item, Map<String, String> attributes) {
		putAttributes(ACTION_TYPE.ADD, item, attributes);
	}
	
	public void modify(String item) {
		putItem(ACTION_TYPE.MODIFY, item);
	}
	
	public void modifyAttributes(String item, Map<String, String> attributes) {
		putAttributes(ACTION_TYPE.MODIFY, item, attributes);
	}

	public void delete(String item) {
		putItem(ACTION_TYPE.DELETE, item);
	}

	public void deleteAttributes(String item, Map<String, String> attributes) {
		putAttributes(ACTION_TYPE.DELETE, item, attributes);
	}

	@Override
	protected String getName() {
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyyMMdd-HH-mm-ss-SSS");
		return ACTIONNAME + sdf.format(new Date());
	}

	private void putItem(ACTION_TYPE action, String item) {
		setStringParam(action.getValue(), item);
	}

	private void putAttributes(ACTION_TYPE type, String item, Map<String, String> attributes) {
		for (String key : attributes.keySet()) {
			setStringParam(type.getValue() + "|" + item + "|" + key, attributes.get(key));
		}
	}
}
