package org.agilereview.storage.xml.exception;

/**
 * Exception indicating problems while converting Review data
 * @author Peter Reuter (03.11.2013)
 */
public class ConversionException extends Exception {
	
	/**
	 * The default serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param e
	 * @author Peter Reuter (03.11.2013)
	 */
	public ConversionException(Exception e) {
		super(e);
	}

}
