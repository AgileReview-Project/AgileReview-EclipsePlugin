/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.preferences;

import java.util.ArrayList;

/**
 * Policy manager for accessing preferences from outside the plug-in
 * @author Malte Brunnlieb (03.06.2012)
 */
public class PreferencesPolicy {
    
    /**
     * A list of all preferences keys which are accessible from outside the plug-in
     */
    private static ArrayList<String> accessibleOutside = new ArrayList<String>();
    
    static {
        accessibleOutside.add(PreferencesManager.COMMENT_PRIORITIES);
        accessibleOutside.add(PreferencesManager.COMMENT_STATUS);
    }
    
    /**
     * Checks whether the given key is accessible from outside of the core plug-in
     * @param key preferences key which should be checked for accessibility
     * @return true, if the key is accessible<br>false, otherwise
     * @author Malte Brunnlieb (03.06.2012)
     */
    public static boolean isAccessibleOutside(String key) {
        return accessibleOutside.contains(key);
    }
}
