/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.exception;

/**
 * Extension which will be thrown when an extension could not be instantiated or found
 * @author Malte Brunnlieb (12.07.2012)
 */
public class ExtensionCreationException extends Exception {
    
    /**
     * Generated UID
     */
    private static final long serialVersionUID = 4139747071988527208L;
    
    /**
     * Creates a new exception
     * @param message Message to be shown to the user
     * @author Malte Brunnlieb (12.07.2012)
     */
    public ExtensionCreationException(String message) {
        super(message);
    }
    
}
