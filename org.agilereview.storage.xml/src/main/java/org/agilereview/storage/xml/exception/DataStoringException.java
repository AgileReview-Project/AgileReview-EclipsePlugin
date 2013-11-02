package org.agilereview.storage.xml.exception;

public class DataStoringException extends Exception {
	
	public DataStoringException(Exception e) {
		super(e);
	}
	
	public DataStoringException(String msg) {
		super(msg);
	}

}
