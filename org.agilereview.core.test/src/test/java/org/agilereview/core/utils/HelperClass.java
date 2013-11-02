/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * HelperClass which delivers elementary functionality for testing methods
 * @author Malte Brunnlieb (19.02.2012)
 */
public class HelperClass {

	/**
	 * Creates an {@link IFile} representation for the given relative path according to this project
	 * @param path relative path according to this project
	 * @return an {@link IFile} representation of the given file path
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	public static IFile getIFile(String path) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("org.agilereview.core_Test");
		if (project == null)
			throw new AssertionError("Project org.agilereview.core_Test could not be found!");
		return project.getFile(path);
	}
}
