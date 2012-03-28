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

/**
 * A class that is used to store replies that were added to a comment. 
 * @author Peter Reuter (19.02.2012)
 */
public class Reply {
	
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
	 * A list of replies that were made to the comment
	 */
	private ArrayList<Reply> replies = new ArrayList<Reply>(0);
	
	/**
	 * Constructor that should be used if a reply is reconstructed from storage
	 * @param author the author of the reply
	 * @param creationDate the date when the reply was create initially
	 * @param modificationDate date when the reply was create lastly
	 * @param text the text of the reply
	 */
	public Reply(String author, Calendar creationDate, Calendar modificationDate,
			String text) {
		this.author = author;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.text = text;
	}
	
	/**
	 * Constructor that should be used if a new reply is created. 
	 */
	public Reply() {
		//TODO take author from properties
		this.author = "";
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
		this.modificationDate = Calendar.getInstance();
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
