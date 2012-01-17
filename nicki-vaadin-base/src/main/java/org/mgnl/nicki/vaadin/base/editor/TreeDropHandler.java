/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.ldap.objects.DynamicObject;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class TreeDropHandler implements DropHandler {
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
			dropData.getDropLocation(); // TODO
			
			Class<? extends DynamicObject> sourceClass = sourceItemId.getClass();
			Class<? extends DynamicObject> targetClass = targetItemId.getClass();
			if (!editor.getAllowedChildren(targetClass).contains(sourceClass)
					|| !(dropData.getDropLocation()== VerticalDropLocation.MIDDLE)
					|| editor.isParent(sourceItemId, targetItemId)
			) {
			    String errorMessage = "Bad target";
				editor.getWindow().showNotification(errorMessage, Notification.TYPE_WARNING_MESSAGE);
				return;
			}
			
			editor.moveObject(sourceItemId, targetItemId);

//			String debugMessage = "source: " + sourceItemId.getName() + ", target: " +  targetItemId.getName();
			//editor.getWindow().showNotification(debugMessage, Notification.TYPE_WARNING_MESSAGE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}

}
