/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.exception;

/**
 * 
 * @author Malte Brunnlieb (08.06.2014)
 */
public class FileNotInWorkspaceException extends Exception {
    
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 6658767560429459014L;
    
    /**
     * @param msg error message
     * @author Malte Brunnlieb (08.06.2014)
     */
    public FileNotInWorkspaceException(String msg) {
        super(msg);
    }
    
}
