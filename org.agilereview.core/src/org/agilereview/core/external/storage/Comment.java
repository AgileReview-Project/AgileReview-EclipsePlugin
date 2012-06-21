/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.agilereview.core.external.definition.IStorageClient;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;

/**
 * A class that stores data and a list of replies of a comment.
 * @author Peter Reuter (19.02.2012)
 */
public class Comment implements PropertyChangeListener {
	
	/**
	 * Name of the property which stores the name of the author
	 */// TODO move static fields to externally available class
	private static final String AUTHOR_PROPERTYNAME = "author";
	
	/**
	 * The id of the comment that is retrieved from an {@link IStorageClient}
	 */
	private String id;
	/**
	 * The {@link IFile} underlying the editor in which the comment was added
	 */
	private IFile commentedFile;
	/**
	 * The {@link Review} which the comment is related to
	 */
	private Review review = null;
	/**
	 * The author of the comment
	 */ //TODO maybe seperate author object for sync with color?
	//TODO change propertyname to static field
	private String author = Platform.getPreferencesService().getString("org.agilereview.core", "author", System.getProperty("user.name"), null);
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
	private List<Reply> replies = new ArrayList<Reply>(0);
	/**
	 * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields. 
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	/**
	 * Constructor that should be used if a new comment is created.
	 * @param id the ID of the comment retrieved from the current {@link IStorageClient}
	 * @param commentedFile the {@link IFile} underlying the editor in which the comment was added
	 * @param review The {@link Review} to which the comment belongs to.
	 */
	public Comment(String id, IFile commentedFile, Review review) {
		this.id = id;
		this.commentedFile = commentedFile;
		this.review = review;
	}
	
	/**
	 * Constructor that should be used if a comment is reconstructed from storage.
	 * @param id the ID of the comment retrieved from the current {@link IStorageClient}
	 * @param author the author of the comment
	 * @param commentedFile the {@link IFile} underlying the editor in which the comment was added
	 * @param review the {@link Review} the comment is related to
	 * @param creationDate the date when the comment was initially created
	 * @param modificationDate the date when the comment was lastly modified
	 * @param recipient the person the comment is addressed to
	 * @param status the current status of the comment
	 * @param priority the priority of the comment
	 * @param text the text of the comment
	 */
	public Comment(String id, String author, IFile commentedFile, Review review, Calendar creationDate, Calendar modificationDate, String recipient,
			int status, int priority, String text) {
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
	 * @return the {@link IFile} underlying the editor in which the comment was added
	 */
	public IFile getCommentedFile() {
		return this.commentedFile;
	}
	
	/**
	 * Reset the reference to the commented {@link IFile} e.g. if the comment was repositioned
	 * @param commentedFile the new {@link IFile}
	 */
	public void setCommentedFile(IFile commentedFile) {
		IFile oldValue = this.commentedFile;
		this.commentedFile = commentedFile;
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("commentedFile", oldValue, this.commentedFile);
	}
	
	/**
	 * @return the {@link Review} the comment is related to
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
		Calendar oldValue = this.modificationDate;
		this.modificationDate = Calendar.getInstance();
		propertyChangeSupport.firePropertyChange("modificationDate", oldValue, this.modificationDate);
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
		String oldValue = this.recipient;
		this.recipient = recipient;
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("recipient", oldValue, this.recipient);
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
		int oldValue = this.status;
		this.status = status;
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("status", oldValue, this.status);
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
		int oldValue = this.priority;
		this.priority = priority;
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("priority", oldValue, this.priority);
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
		String oldValue = this.text;
		this.text = text;
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("text", oldValue, this.text);
	}
	
	/**
	 * @return a list of replies that were added to this comment
	 */
	public List<Reply> getReplies() {
		return replies;
	}
	
	/**
	 * Sets the {@link Reply}s of this {@link Comment}.<br><strong>Note: </strong>This method is intended to be used only while deriving {@link Comment}s from external storage! 
	 * @param replies
	 * @author Peter Reuter (28.04.2012)
	 */
	public void setReplies(List<Reply> replies) {
		this.replies = replies;
		for (Reply reply : this.replies) {
			reply.addPropertyChangeListener(this);
		}
	}
	
	/**
	 * Adds a {@link Reply} to this {@link Comment} if and only if it was not added before.
	 * @param reply the reply which is to add
	 */
	public void addReply(Reply reply) {
		if (!this.replies.contains(reply)) {
			List<Reply> oldValue = new ArrayList<Reply>(this.replies);
			this.replies.add(reply);
			reply.addPropertyChangeListener(this);
			resetModificationDate();
			propertyChangeSupport.firePropertyChange("replies", oldValue, this.replies);
		}
	}
	
	/**
	 * Deletes the given {@link Reply} this {@link Reply}.
	 * @param reply the reply which is to delete
	 */
	public void deleteReply(Reply reply) {
		List<Reply> oldValue = new ArrayList<Reply>(this.replies);
		this.replies.remove(reply);
		reply.removePropertyChangeListener(this);
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("replies", oldValue, this.replies);
	}
	
	/**
	 * Deletes the {@link Reply} with the given index from this {@link Comment};
	 * @param index the index of the reply which is to delete
	 */
	public void deleteReply(int index) {
		deleteReply(this.replies.get(index));
	}

	/**
	 * Adds a {@link PropertyChangeListener} that is notified on {@link PropertyChangeEvent}s to this {@link Comment}
	 * @param listener
	 * @author Peter Reuter (28.04.2012)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	/**
	 * Removes a {@link PropertyChangeListener} from this {@link Comment}
	 * @param listener
	 * @author Peter Reuter (28.04.2012)
	 */public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertyChangeSupport.firePropertyChange(evt);
	}
	
}
