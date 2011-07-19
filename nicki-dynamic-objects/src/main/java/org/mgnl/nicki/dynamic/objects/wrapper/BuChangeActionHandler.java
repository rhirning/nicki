/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.wrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.mgnl.nicki.ldap.context.NickiContext;

/**
 *
 * @author cna
 */
public class BuChangeActionHandler extends BasicActionHandler {
    	private static final String ACTIONNAME = "bu-change-action";
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public BuChangeActionHandler(NickiContext ctx, String target) {
		super(ctx, ACTIONNAME, target, false);
	}
        
        public void setTransferDate(Date date) {
            setStringParam("TRANSFERDATE", sdf.format(date));
        }

        public void setOrgaChangeDate(Date date) {
            setStringParam("ORGACHANGEDATE", sdf.format(date));            
        }
        
        public void setInitiator(String ldapDn) {
            setDnParam("INITIATOR", DN_FORMAT.LDAP, ldapDn);
        }
        
        public void setDestination(String ldapDn) {
            setDnParam("DESTINATION", DN_FORMAT.LDAP, ldapDn);            
        }
        
}
