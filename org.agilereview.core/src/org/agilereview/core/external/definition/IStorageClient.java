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
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;

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
	 * This method returns all {@link Review} objects currently loaded.
	 * @return a {@link List} of {@link Review} objects.
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	public List<Review> getAllReviews();

	/**
	 * This method adds a new {@link Review} object to the list of {@link Review} objects currently loaded.
	 * @param review
	 * @author Peter Reuter (04.04.2012)
	 */
	public void addReview(Review review);

	/**
	 * @param review the {@link Review} for which a new ID is requested
	 * @return A unique identifier for this {@link Review} as it is required by the {@link IStorageClient}.
	 * @author Peter Reuter (28.04.2012)
	 */
	public String getNewId(Review review);

	/**
	 * @param comment the {@link Comment} for which a new ID is requested
	 * @return A unique identifier for this {@link Comment} as it is required by the {@link IStorageClient}.
	 * @author Peter Reuter (28.04.2012)
	 */
	public String getNewId(Comment comment);

	/**
	 * @param reply the {@link Reply} for which a new ID is requested
	 * @return A unique identifier for this {@link Reply} as it is required by the {@link IStorageClient}.
	 * @author Peter Reuter (28.04.2012)
	 */
	public String getNewId(Reply reply);

}
