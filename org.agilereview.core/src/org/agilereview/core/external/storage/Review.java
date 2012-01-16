package org.agilereview.core.external.storage;

import java.net.URI;
import java.util.ArrayList;

/**
 * A class that stores review data and a list of comments belonging to the review.
 */
public class Review {
	
	/**
	 * The unique name of the review entered by the user 
	 */
	private String id;
	/**
	 * The status of the review 
	 */
	private int status = 0;
	/**
	 * A reference to e.g. a bug tracker 
	 */
	private URI reference = null;
	/**
	 * The person that is in charge for the review 
	 */
	private String responsibility = "";
	/**
	 * A list of comments that belong to this review 
	 */
	ArrayList<Comment> comments = new ArrayList<Comment>(0);
	/**
	 * A flag indicating whether the review is closed or open 
	 */
	private boolean isOpen = true;
	
	/**
	 * Constructor that should be used if a new review is created.
	 * @param id a unique name for the review entered by the user
	 */
	public Review(String id) {
		this.id = id;
	}
	
	/**
	 * Constructor that should be used if a review is reconstructed from storage
	 * @param id the id of the review
	 * @param status the status of the review
	 * @param reference e.g. a reference to a bug tracker
	 * @param responsibility the person that is in charge for this review
	 * @param comments the list of comments belonging to this review
	 */
	public Review(String id, int status, URI reference, String responsibility,
			ArrayList<Comment> comments) {
		this.id = id;
		this.status = status;
		this.reference = reference;
		this.responsibility = responsibility;
		this.comments = comments;
	}

	/**
	 * @return the ID of the review
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the current status of the review
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @param status the new status of the review
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return the current reference of the review
	 */
	public URI getReference() {
		return reference;
	}
	
	/**
	 * @param reference the new reference of the review
	 */
	public void setReference(URI reference) {
		this.reference = reference;
	}
	
	/**
	 * @return the person that currently is in charge for this review
	 */
	public String getResponsibility() {
		return responsibility;
	}
	
	/**
	 * @param responsibility the person that now is in charge for this review
	 */
	public void setResponsibility(String responsibility) {
		this.responsibility = responsibility;
	}
	
	/**
	 * @return a list of comments belonging to this review
	 */
	public ArrayList<Comment> getComments() {
		return comments;
	}
	
	/**
	 * @param comment the comment that is to be added to the list of comments
	 */
	public void addComment(Comment comment) {
		this.comments.add(comment);
	}
	
	/**
	 * @param comment the comment that is to be removed from the list of comments
	 */
	public void deleteComment(Comment comment) {
		this.comments.remove(comment);
	}
	
	/**
	 * @param index the index of the comment that is to be removed from the list of comments
	 */
	public void deleteComment(int index) {
		this.comments.remove(index);
	}
	
	/**
	 * @return true if the review is open, false otherwise
	 */
	public boolean getIsOpen() {
		return this.isOpen;
	}
	
	/**
	 * @param isOpen the new state of the review
	 */
	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
}
