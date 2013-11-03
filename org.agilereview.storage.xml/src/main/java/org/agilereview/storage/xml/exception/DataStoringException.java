package org.agilereview.storage.xml.exception;

/**
 * Exception indicating problems while storing Review data
 * @author Peter Reuter (03.11.2013)
 */
public class DataStoringException extends Exception {
	
	/**
	 * The default serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param e
	 * @author Peter Reuter (03.11.2013)
	 */
	public DataStoringException(Exception e) {
		super(e);
	}
	
	/**
	 * @param msg
	 * @author Peter Reuter (03.11.2013)
	 */
	public DataStoringException(String msg) {
		super(msg);
	}

}
