/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.properties;

import org.agilereview.core.preferences.PreferencesManager;
import org.agilereview.core.preferences.PreferencesPolicy;

/**
 * The {@link PreferencesInterface} enables access to internal preferences of AgileReview
 * @author Malte Brunnlieb (28.04.2012)
 */
public class PreferencesInterface {
    
    /**
     * {@link PreferencesManager} instance for all requests
     */
    private PreferencesManager manager = new PreferencesManager();
    
    /**
     * Comma separated list of comment status
     */
    public static final String COMMENT_STATUS = PreferencesManager.COMMENT_STATUS;
    /**
     * Comma separated list of comment priorities
     */
    public static final String COMMENT_PRIORITIES = PreferencesManager.COMMENT_PRIORITIES;
    /**
     * id of the currently active review
     */
    public static final String ACTIVE_REVIEW_ID = PreferencesManager.ACTIVE_REVIEW_ID;
    
    /**
     * Returns the current preferences value for the given key. If the given key is not available or accessible null will be returned.
     * @param key preferences key
     * @return the current value for the given key or null if the key is not available or accessible
     * @author Malte Brunnlieb (03.06.2012)
     */
    public String getValue(String key) {
        if (PreferencesPolicy.isAccessibleOutside(key)) {
            return manager.getValue(key);
        } else {
            return null;
        }
    }
}
