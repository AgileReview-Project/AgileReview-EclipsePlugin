/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.definition;

import java.util.List;

import org.agilereview.core.external.storage.Review;

/**
 * Interface for the ExtensionPoint org.agilereview.core.ReviewDataReceiver
 * @author Malte Brunnlieb (28.03.2012)
 */
public interface IReviewDataReceiver {
	
	/**
	 * Will be called whenever new data are provided by {@link IStorageClient}s
	 * @param reviews a list of {@link Review}s
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	public void setReviewData(List<Review> reviews);
	
}
