/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.exception;

/**
 * In general this exception will occur if arguments of a function incorrectly are set to <code>null</code>
 * @author Malte Brunnlieb (26.11.2012)
 */
public class NullArgumentException extends Exception {
    
    /**
     * Generated UID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new {@link NullArgumentException} with the given message
     * @param msg Message which states the current problem
     * @author Malte Brunnlieb (26.11.2012)
     */
    public NullArgumentException(String msg) {
        super(msg);
    }
}
