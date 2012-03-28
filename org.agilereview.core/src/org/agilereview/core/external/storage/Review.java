/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that stores review data and a list of comments belonging to the review.
 * @author Peter Reuter (19.02.2012)
 */
public class Review {
	
	/**
	 * The unique name of the review entered by the user
	 */
	private final String id;
	/**
	 * The status of the review
	 */
	private int status = 0;
	/**
	 * A reference to e.g. a bug tracker
	 */
	private String reference = null;
	/**
	 * The person that is in charge for the review
	 */
	private String responsibility = "";
	/**
	 * A list of comments that belong to this review
	 */
	private List<Comment> comments = new ArrayList<Comment>(0);
	/**
	 * A flag indicating whether the review is closed or open
	 */
	private boolean isOpen = true;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructor that should be used if a new review is created.
	 * @param id a unique name for the review entered by the user
	 */
	public Review(String id) {
		propertyChangeSupport = new PropertyChangeSupport(this);
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
	public Review(String id, int status, String reference, String responsibility, List<Comment> comments) {
		this(id);
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
		int oldValue = this.status;
		this.status = status;
		propertyChangeSupport.firePropertyChange("status", oldValue, this.status);
	}
	
	/**
	 * @return the current reference of the review
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * @param reference the new reference of the review
	 */
	public void setReference(String reference) {
		String oldValue = this.reference;
		this.reference = reference;
		propertyChangeSupport.firePropertyChange("reference", oldValue, this.reference);
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
		String oldValue = responsibility;
		this.responsibility = responsibility;
		propertyChangeSupport.firePropertyChange("responsibility", oldValue, this.responsibility);
	}
	
	/**
	 * @return a list of comments belonging to this review
	 */
	public List<Comment> getComments() {
		return comments;
	}
	
	/**
	 * @param comment the comment that is to be added to the list of comments
	 */
	public void addComment(Comment comment) {
		ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
		this.comments.add(comment);
		propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
	}
	
	/**
	 * @param comment the comment that is to be removed from the list of comments
	 */
	public void deleteComment(Comment comment) {
		ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
		this.comments.remove(comment);
		propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
	}
	
	/**
	 * @param index the index of the comment that is to be removed from the list of comments
	 */
	public void deleteComment(int index) {
		ArrayList<Comment> oldValue = new ArrayList<Comment>(this.comments);
		this.comments.remove(index);
		propertyChangeSupport.firePropertyChange("comments", oldValue, this.comments);
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
		boolean oldValue = this.isOpen;
		this.isOpen = isOpen;
		propertyChangeSupport.firePropertyChange("isOpen", oldValue, this.isOpen);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public String toString() {
		return id;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
	
}
