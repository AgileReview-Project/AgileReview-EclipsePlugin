/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import java.util.List;

import org.agilereview.core.external.storage.Comment;

/**
 * Interface that has to be implemeted by AgileReview storage clients.
 * @author Peter Reuter (19.02.2012)
 */
public interface IStorageClient {
	
	/**
	 * Returns the name of the StorageClient (should not be null)
	 * @return the name of the StorageClient
	 * @author Malte Brunnlieb (24.03.2012)
	 */
	public String getName();
	
	/**
	 * This method returns all comments currently loaded
	 * @return a list of comments
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	public List<Comment> getAllComments();
	
}
