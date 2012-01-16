package org.agilereview.core.external.storage;

import java.util.Date;

/**
 * A class that is used to store replies that were added to a comment.
 */
public class Reply {
	
	/**
	 * The author of the reply 
	 */
	private String author = ""; //TODO maybe seperate author object for sync with color?
	/**
	 * The date when the reply was create initially 
	 */
	private Date creationDate = new Date();
	/**
	 * The date when the reply was modified lastly 
	 */
	private Date modificationDate = creationDate;
	/**
	 * The text of the reply 
	 */
	private String text = "";
	
	/**
	 * Constructor that should be used if a reply is reconstructed from storage
	 * @param author the author of the reply
	 * @param creationDate the date when the reply was create initially
	 * @param modificationDate date when the reply was create lastly
	 * @param text the text of the reply
	 */
	public Reply(String author, Date creationDate, Date modificationDate,
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
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * @return the date when the reply was modified lastly
	 */
	public Date getModificationDate() {
		return modificationDate;
	}
	
	/**
	 * Resets the modification date. Should be used if an attribute was changed
	 * e.g. in setters. 
	 */
	private void resetModificationDate() {
		this.modificationDate = new Date();
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
	
}
