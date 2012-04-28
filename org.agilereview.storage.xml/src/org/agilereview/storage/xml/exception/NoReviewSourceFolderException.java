package org.agilereview.storage.xml.exception;

/**
 * @author Malte
 * This exception occurs when functionality needs access to the file system and no review source folder has been defined yet.
 */
public class NoReviewSourceFolderException extends Exception {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -7977566029608457841L;
	
	/**
	 * Constructs a {@link NoReviewSourceFolderException} with the specified detail message.
	 * @param message
	 * @author Peter Reuter (28.04.2012)
	 */
	public NoReviewSourceFolderException(String message) {
		super(message);
	}

}