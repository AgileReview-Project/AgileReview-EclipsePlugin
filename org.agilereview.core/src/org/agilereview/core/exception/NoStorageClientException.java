/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.exception;

/**
 * 
 * @author Malte Brunnlieb (24.03.2012)
 */
public class NoStorageClientException extends Exception {
	
	/**
	 * Generated version UID
	 */
	private static final long serialVersionUID = 2452264158515257109L;
	
	/**
	 * Creates a new {@link NoStorageClientException} with the given message.
	 * @param msg Message for this Exception
	 * @author Malte Brunnlieb (24.03.2012)
	 */
	public NoStorageClientException(String msg) {
		super(msg);
	}
	
}
