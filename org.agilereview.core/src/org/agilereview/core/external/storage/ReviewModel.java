/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.util.ArrayList;

/**
 * A class that holds references to all currently available reviews.  
 * @author Peter Reuter (19.02.2012)
 */
public class ReviewModel {
	
	/**
	 * The instance of the global review model. 
	 */
	private ReviewModel instance = new ReviewModel();
	/**
	 * List of review available in the global review model. 
	 */
	private ArrayList<Review> reviews = new ArrayList<Review>(0);
	
	/**
	 * @return The instance of the global review model.
	 */
	public ReviewModel getInstance() {
		return this.instance;
	}
	
	/**
	 * Adds a review to the global review model.
	 * @param review The review which will be added.
	 */
	public void addReview(Review review) {
		reviews.add(review);
	}

	/**
	 * Removes a review from the global review model.
	 * @param review The review which will be removed.
	 */
	public void removeReview(Review review) {
		reviews.remove(review);
	}
	
	/**
	 * Clears the global review model by removing all reviews from it. 
	 */
	public void clear() {
		reviews.clear();
	}
	
}
