/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class TreeDropHandler implements DropHandler {
	private static final Logger LOG = LoggerFactory.getLogger(TreeDropHandler.class);
	private TreeEditor editor;

	public TreeDropHandler(TreeEditor editor) {
		super();
		this.editor = editor;
	}

	public void drop(DragAndDropEvent dropEvent) {

        try {
			DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
//			Container sourceContainer = t.getSourceContainer();
			DynamicObject sourceItemId = (DynamicObject) t.getItemId();
//			Item sourceItem = sourceContainer.getItem(sourceItemId);

			AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent
			.getTargetDetails());
			DynamicObject targetItemId = (DynamicObject) dropData.getItemIdOver();
			dropData.getDropLocation(); // TODO what to do with this?
			
			Class<? extends DynamicObject> sourceClass = sourceItemId.getClass();
			Class<? extends DynamicObject> targetClass = targetItemId.getClass();
			if (!editor.getAllowedChildren(targetClass).contains(sourceClass)
					|| !(dropData.getDropLocation()== VerticalDropLocation.MIDDLE)
					|| editor.isParent(sourceItemId, targetItemId)
			) {
			    String errorMessage = "Bad target";
				Notification.show(errorMessage, Notification.Type.WARNING_MESSAGE);
				return;
			}
			
			editor.moveObject(sourceItemId, targetItemId);

//			String debugMessage = "source: " + sourceItemId.getName() + ", target: " +  targetItemId.getName();
			//editor.getWindow().showNotification(debugMessage, Notification.TYPE_WARNING_MESSAGE);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
        
	}

	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}

}
