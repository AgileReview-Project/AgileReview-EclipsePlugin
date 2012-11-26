/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.storage.xml.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Peter Reuter (15.10.2012)
 */
public class AgileReviewActiveNature implements IProjectNature {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 * @author Peter Reuter (15.10.2012)
	 */
	@Override
	public void configure() throws CoreException {}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 * @author Peter Reuter (15.10.2012)
	 */
	@Override
	public void deconfigure() throws CoreException {}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 * @author Peter Reuter (15.10.2012)
	 */
	@Override
	public IProject getProject() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 * @author Peter Reuter (15.10.2012)
	 */
	@Override
	public void setProject(IProject project) {}

}
