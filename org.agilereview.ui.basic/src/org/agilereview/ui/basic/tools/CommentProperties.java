/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.tools;

import org.agilereview.common.preferences.PreferencesAccessor;
import org.agilereview.core.external.preferences.AgileReviewPreferences;

/**
 * Advanced property access point for comments which provides further functionality
 * @author Malte Brunnlieb (28.04.2012)
 */
public class CommentProperties {
    
    /**
     * Properties value: comment status
     */
    private String[] commentStates;
    /**
     * Properties value: comment properties
     */
    private String[] commentPriorities;
    
    /**
     * Creates a new {@link CommentProperties} instance
     * 
     * @author Malte Brunnlieb (28.04.2012)
     */
    public CommentProperties() {
        loadProperties();
    }
    
    /**
     * Loads all necessary property values
     * @author Malte Brunnlieb (28.04.2012)
     */
    public void loadProperties() {
        PreferencesAccessor pref = new PreferencesAccessor();
        String value = pref.get(AgileReviewPreferences.COMMENT_STATUS);
        commentStates = value.split(",");
        value = pref.get(AgileReviewPreferences.COMMENT_PRIORITIES);
        commentPriorities = value.split(",");
    }
    
    /**
     * Returns a Comment status value defined in the properties according to its id
     * @param ID for the requested Comment status
     * @return the String according to the given ID
     */
    public String getStatusByID(int ID) {
        String status = "Status not found!";
        if (ID < 0 || ID >= this.commentStates.length) {
            throw new RuntimeException(ID + " no valid StatusID!");
        } else {
            status = this.commentStates[ID];
        }
        return status;
    }
    
    /**
     * Returns all possibilities for comment statuses
     * @return array containing all possibilities for statuses
     * @author Thilo Rauch (26.11.2012)
     */
    public String[] getStatuses() {
        return this.commentStates;
    }
    
    /**
     * Returns a Comment priority value defined in the properties according to its id
     * @param ID for the requested Comment priority
     * @return the String according to the given ID
     */
    public String getPriorityByID(int ID) {
        String prio = "Priority not found!";
        if (ID < 0 || ID >= this.commentPriorities.length) {
            throw new RuntimeException(ID + " no valid PrioritiesID!");
        } else {
            prio = this.commentPriorities[ID];
        }
        return prio;
    }
    
    /**
     * Returns all possibilities for comment priorities
     * @return array containing all possibilities for priorities
     * @author Thilo Rauch (26.11.2012)
     */
    public String[] getPriorities() {
        return this.commentPriorities;
    }
}
