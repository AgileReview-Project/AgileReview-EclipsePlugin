package org.agilereview.core.external.storage;

import java.util.ArrayList;

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
