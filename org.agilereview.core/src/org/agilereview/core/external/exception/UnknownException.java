/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.exception;

/**
 * This exception should only be thrown in cases which are marked as illegal by construction or framework definition. In general this exception should
 * never occur, but it provides more failure identification possibilities.
 * @author Malte Brunnlieb (26.11.2012)
 */
public class UnknownException extends Exception {
    
    /**
     * Generated UID
     */
    private static final long serialVersionUID = -2592789689996301437L;
    
}
