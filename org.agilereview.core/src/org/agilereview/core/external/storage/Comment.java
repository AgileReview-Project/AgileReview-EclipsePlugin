package org.agilereview.core.external.storage;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
	
	public Comment(String id, String author) {
		this.id = id;
		this.author = author;
	}
	
	//TODO take defaults from properties where it seems to be useful
	
	private String id;
	private String author; //TODO maybe seperate author object for sync with color?
	private String wsAbsfilePath; //TODO use IFile object here???
	private Date creationDate = new Date();
	private Date modificationDate = new Date();
	private String recipient;
	private int status = 0;
	private int priority;
	private String text = "";
	private ArrayList<Reply> replies;
	
	public String getId() {
		return id;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
		resetModificationDate();
	}
	
	public String getWsAbsfilePath() {
		return wsAbsfilePath;
	}
	
	public void setWsAbsfilePath(String wsAbsfilePath) {
		this.wsAbsfilePath = wsAbsfilePath;
		resetModificationDate();
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	private void resetModificationDate() {
		this.modificationDate = new Date();
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public void setRecipient(String recipient) {
		this.recipient = recipient;
		resetModificationDate();
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
		resetModificationDate();
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
		resetModificationDate();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		resetModificationDate();
	}
	
	public ArrayList<Reply> getReplies() {
		return replies;
	}
	
	public void addReplies(Reply reply) {
		this.replies.add(reply);
		resetModificationDate();
	}

}
