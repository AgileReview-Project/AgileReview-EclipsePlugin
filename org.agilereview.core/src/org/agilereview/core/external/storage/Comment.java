/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.core.resources.IFile;

/**
 * A class that stores data and a list of replies of a comment.
 * @author Peter Reuter (19.02.2012)
 */
public class Comment {
	
	/**
	 * The id of the comment that is retrieved from an IStorageClient
	 */
	private final String id;
	/**
	 * The IFile underlying the editor in which the comment was added
	 */
	private IFile commentedFile;
	/**
	 * The review which the comment is related to
	 */
	private Review review = null;
	/**
	 * The author of the comment
	 */
	private String author = ""; //TODO maybe seperate author object for sync with color?
	/**
	 * The date when the comment was created initially
	 */
	private Calendar creationDate = Calendar.getInstance();
	/**
	 * The date when the comment was created lastly
	 */
	private Calendar modificationDate = creationDate;
	/**
	 * The person the comment is addressed to
	 */
	private String recipient = "";
	/**
	 * The status of the comment
	 */
	private int status = 0;
	/**
	 * The priority of the comment
	 */
	private int priority = 0;
	/**
	 * The text of the comment
	 */
	private String text = "";
	/**
	 * A list of replies that were made to the comment
	 */
	private ArrayList<Reply> replies = new ArrayList<Reply>(0);
	
	/**
	 * Constructor that should be used if a new comment is created.
	 * @param id the ID of the comment retrieved from the current IStorageClient
	 * @param commentedFile the IFile underlying the editor in which the comment was added
	 */
	public Comment(String id, IFile commentedFile) {
		this.id = id;
		this.commentedFile = commentedFile;
		
		//TODO take author and review from properties
		this.author = "";
		this.review = null;
	}
	
	/**
	 * Constructor that should be used if a comment is reconstructed from storage
	 * @param id the ID of the comment retrieved from the current IStorageClient
	 * @param author the author of the comment
	 * @param commentedFile the IFile underlying the editor in which the comment was added
	 * @param review the review the comment is related to
	 * @param creationDate the date when the comment was initially created
	 * @param modificationDate the date when the comment was lastly modified
	 * @param recipient the person the comment is addressed to
	 * @param status the current status of the comment
	 * @param priority the priority of the comment
	 * @param text the text of the comment
	 * @param replies a list of replies that were made to the comment
	 */
	public Comment(String id, String author, IFile commentedFile, Review review, Calendar creationDate, Calendar modificationDate, String recipient,
			int status, int priority, String text, ArrayList<Reply> replies) {
		this.id = id;
		this.author = author;
		this.commentedFile = commentedFile;
		this.review = review;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.recipient = recipient;
		this.status = status;
		this.priority = priority;
		this.text = text;
		this.replies = replies;
	}
	
	/**
	 * @return the ID that was retrieved from the IStorageClient
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the author of the comment
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @return the IFile underlying the editor in which the comment was added
	 */
	public IFile getCommentedFile() {
		return this.commentedFile;
	}
	
	/**
	 * Reset the reference to the commented file e.g. if the comment was repositioned
	 * @param commentedFile the new location
	 */
	public void setCommentedFile(IFile commentedFile) {
		this.commentedFile = commentedFile;
		resetModificationDate();
	}
	
	/**
	 * @return the review the comment is related to
	 */
	public Review getReview() {
		return this.review;
	}
	
	/**
	 * @return the date when the comment was created initially
	 */
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	/**
	 * @return the date when the comment was modified lastly
	 */
	public Calendar getModificationDate() {
		return modificationDate;
	}
	
	/**
	 * Resets the modification date. Should be used if an attribute was changed e.g. in setters.
	 */
	private void resetModificationDate() {
		this.modificationDate = Calendar.getInstance();
	}
	
	/**
	 * @return the person this comment is addressed to
	 */
	public String getRecipient() {
		return recipient;
	}
	
	/**
	 * @param recipient the person this comment is now addressed to
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
		resetModificationDate();
	}
	
	/**
	 * @return the current status of the comment
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @param status the new status of the comment
	 */
	public void setStatus(int status) {
		this.status = status;
		resetModificationDate();
	}
	
	/**
	 * @return the current priority of the comment
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * @param priority the new priority of the comment
	 */
	public void setPriority(int priority) {
		this.priority = priority;
		resetModificationDate();
	}
	
	/**
	 * @return the current text of the comment
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param text the new text of the comment
	 */
	public void setText(String text) {
		this.text = text;
		resetModificationDate();
	}
	
	/**
	 * @return a list of replies that were added to this comment
	 */
	public ArrayList<Reply> getReplies() {
		return replies;
	}
	
	/**
	 * Adds at the end of the list of replies.
	 * @param reply the reply which is to add
	 */
	public void addReply(Reply reply) {
		this.replies.add(reply);
		resetModificationDate();
	}
	
	/**
	 * Deletes the given reply from the list of replies.
	 * @param reply the reply which is to delete
	 */
	public void deleteReply(Reply reply) {
		this.replies.remove(reply);
		resetModificationDate();
	}
	
	/**
	 * Deletes the reply with the given index from the list of replies
	 * @param index the index of the reply which is to delete
	 */
	public void deleteReply(int index) {
		this.replies.remove(index);
		resetModificationDate();
	}
	
}
