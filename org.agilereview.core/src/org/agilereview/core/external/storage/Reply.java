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

import org.agilereview.core.Activator;

/**
 * A class that is used to store replies that were added to a comment. 
 * @author Peter Reuter (19.02.2012)
 */
public class Reply implements PropertyChangeListener {
	
	/**
	 * Name of the property which stores the name of the author
	 */
	private static final String AUTHOR_PROPERTYNAME = "author";
	
	/**
	 * Unique ID of this {@link Reply}
	 */
	private String id;
	/**
	 * The author of the reply 
	 */ //TODO maybe seperate author object for sync with color?
	private String author = Activator.getDefault().getPreferenceStore().getString(AUTHOR_PROPERTYNAME).equals("") ? Activator.getDefault().getPreferenceStore().getDefaultString(AUTHOR_PROPERTYNAME) : Activator.getDefault().getPreferenceStore().getString(AUTHOR_PROPERTYNAME);
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
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	
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
	 * @param id The ID of the reply
	 * @param parent The parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
	 */
	public Reply(String id, Object parent) {
		this.id = id;
		this.parent = parent;
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
	 * @return a {@link List} of {@link Reply}s that were added to this {@link Comment}
	 */
	public List<Reply> getReplies() {
		return replies;
	}
	
	/**
	 * Sets the {@link Reply}s of this {@link Reply}.<br><strong>Note: </strong>This method is intended to be used only use while deriving {@link Reply}s from external storage! 
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
	 * Adds a {@link Reply} to this {@link Reply} if and only if it was not added before.
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
	 * Deletes the given {@link Reply} from this {@link Reply}.
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
	 * Deletes the {@link Reply} with the given index from this {@link Reply}.
	 * @param index the index of the reply which is to delete
	 */
	public void deleteReply(int index) {
		deleteReply(this.replies.get(index));
	}

	/**
	 * @return the parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}.
	 * @author Peter Reuter (28.04.2012)
	 */
	public Object getParent() {
		return this.parent;
	}
	
	/**
	 * Adds a {@link PropertyChangeListener} that is notified on {@link PropertyChangeEvent}s to this {@link Reply}.
	 * @param listener
	 * @author Peter Reuter (28.04.2012)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	/**
	 * Removes a {@link PropertyChangeListener} from this {@link Reply}.
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
