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

/**
 * A class that is used to store replies that were added to a comment. 
 * @author Peter Reuter (19.02.2012)
 */
public class Reply implements PropertyChangeListener {
	
	/**
	 * Unique ID of this {@link Reply}
	 */
	private String id = "";
	/**
	 * The author of the reply 
	 */
	private String author = ""; //TODO maybe seperate author object for sync with color?
	/**
	 * The date when the reply was create initially 
	 */
	private Calendar creationDate = Calendar.getInstance();
	/**
	 * The date when the reply was modified lastly 
	 */
	private Calendar modificationDate = creationDate;
	/**
	 * The text of the reply 
	 */
	private String text = "";
	/**
	 * A {@link List} of replies that were made to the comment
	 */
	private List<Reply> replies = new ArrayList<Reply>(0);
	/**
	 * The parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
	 */
	private Object parent = null;
	/**
	 * {@link PropertyChangeSupport} of this POJO, used for firing {@link PropertyChangeEvent}s on changes of fields.
	 */
	private PropertyChangeSupport propertyChangeSupport;
	
	
	/**
	 * Constructor that should be used if a reply is reconstructed from storage
	 * @param id the ID of the reply
	 * @param author the author of the reply
	 * @param creationDate the date when the reply was create initially
	 * @param modificationDate date when the reply was create lastly
	 * @param text the text of the reply
	 * @param parent the parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
	 */
	public Reply(String id, String author, Calendar creationDate, Calendar modificationDate,
			String text, Object parent) {
		this.id = id;
		this.author = author;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.text = text;
		this.parent = parent;
	}
	
	/**
	 * Constructor that should be used if a new reply is created. 
	 */
	public Reply() {
		//TODO take author from properties
		this.author = "";
	}
	
	/**
	 * @return the id of the reply
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the author of the reply
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @return the date when the reply was created initially
	 */
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	/**
	 * @return the date when the reply was modified lastly
	 */
	public Calendar getModificationDate() {
		return modificationDate;
	}
	
	/**
	 * Resets the modification date. Should be used if an attribute was changed
	 * e.g. in setters. 
	 */
	private void resetModificationDate() {
		Calendar oldValue = this.modificationDate;
		this.modificationDate = Calendar.getInstance();
		propertyChangeSupport.firePropertyChange("modificationDate", oldValue, this.modificationDate);
	}
	
	/**
	 * @return the current text of the reply
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param text the new text of the reply
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
	 * Sets the {@link Reply}s of this {@link Reply}. <strong>Only use while deriving {@link Reply}s from external storage!</strong> 
	 * @param replies
	 * @author Peter Reuter (28.04.2012)
	 */
	public void setReplies(List<Reply> replies) {
		this.replies = replies;
		for (Reply reply: this.replies) {
			reply.addPropertyChangeListener(this);
		}
	}
	
	/**
	 * Adds at the end of the list of replies.
	 * @param reply the reply which is to add
	 */
	public void addReply(Reply reply) {
		List<Reply> oldValue = new ArrayList<Reply>(this.replies);
		this.replies.add(reply);
		reply.addPropertyChangeListener(this);
		resetModificationDate();
		propertyChangeSupport.firePropertyChange("replies", oldValue, this.replies);
	}
	
	/**
	 * Deletes the given reply from the list of replies.
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
	 * Deletes the reply with the given index from the list of replies
	 * @param index the index of the reply which is to delete
	 */
	public void deleteReply(int index) {
		deleteReply(this.replies.get(index));
	}

	/**
	 * @return the parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
	 * @author Peter Reuter (28.04.2012)
	 */
	public Object getParent() {
		return this.parent;
	}
	
	/**
	 * Adds a {@link PropertyChangeListener} to the list of listeners that are notified on {@link PropertyChangeEvent}s
	 * @param listener
	 * @author Peter Reuter (28.04.2012)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	/**
	 * Removes a {@link PropertyChangeListener} from the list of listeners that are notified on {@link PropertyChangeEvent}s
	 * @param listener
	 * @author Peter Reuter (28.04.2012)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertyChangeSupport.firePropertyChange(evt);
	}
	
}
