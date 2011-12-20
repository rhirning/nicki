/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.projects.directories;

public interface NewDirectoryHandler {

	void createNewDirectory(String value);

	void closeNewDirectoryWindow();

}
