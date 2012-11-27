/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.prefs;

/**
 * A POJO representing author preferences. This POJO might be used for author colors and their reservation preferences.
 * @author Malte Brunnlieb (21.11.2012)
 */
public class AuthorPreferencesPojo {
    
    /**
     * Enumeration of all available authors
     * @author Malte Brunnlieb (20.11.2012)
     */
    @SuppressWarnings("javadoc")
    public enum AuthorTag {
        IDEUser, Author2, Author3, Author4, Author5, Author6, Author7, Author8, Author9, Author10
    }
    
    /**
     * IDEUser (injected by JSON)
     */
    public String IDEUser;
    /**
     * Author 2 (injected by JSON)
     */
    public String Author2;
    /**
     * Author 3 (injected by JSON)
     */
    public String Author3;
    /**
     * Author 4 (injected by JSON)
     */
    public String Author4;
    /**
     * Author 5 (injected by JSON)
     */
    public String Author5;
    /**
     * Author 6 (injected by JSON)
     */
    public String Author6;
    /**
     * Author 7 (injected by JSON)
     */
    public String Author7;
    /**
     * Author 8 (injected by JSON)
     */
    public String Author8;
    /**
     * Author 9 (injected by JSON)
     */
    public String Author9;
    /**
     * Author 10 (injected by JSON)
     */
    public String Author10;
}
