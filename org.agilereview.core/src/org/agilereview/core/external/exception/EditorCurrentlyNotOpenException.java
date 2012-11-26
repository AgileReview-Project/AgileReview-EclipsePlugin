/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.exception;

/**
 * In general this exception will occur if comment tags are added via the interface for adding new tags to an open editor, but the intended editor is
 * not open
 * @author Malte Brunnlieb (26.11.2012)
 */
public class EditorCurrentlyNotOpenException extends Exception {
    
    /**
     * Generated UID
     */
    private static final long serialVersionUID = 4576101794869014370L;
}
