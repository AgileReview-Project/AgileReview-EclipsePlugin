/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.preferences;

import java.io.IOException;
import java.util.Properties;

import org.agilereview.core.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preferences manager for all internal preferences
 * @author Malte Brunnlieb (03.06.2012)
 */
public class PreferencesManager {
    
    /**
     * The preference store of this plugin, so other plugins can query it
     */
    private IPreferenceStore prefs = null;
    /**
     * Default properties
     */
    private Properties defaults = new Properties();
    
    /**
     * Comma separated list of comment status
     */
    public static final String COMMENT_STATUS = "org.agilereview.comment_status";
    /**
     * Comma separated list of comment priorities
     */
    public static final String COMMENT_PRIORITIES = "org.agilereview.comment_priorities";
    /**
     * id of the currently active review
     */
    public static final String ACTIVE_REVIEW_ID = "org.agilereview.active_review_id";
    
    /**
     * Creates a new instance of the Preferences manager
     * @author Malte Brunnlieb (03.06.2012)
     */
    public PreferencesManager() {
        this.prefs = Activator.getDefault().getPreferenceStore();
        loadDefaults();
    }
    
    /**
     * Loads the defaults from the preferences file if not set already in the preferences store
     * @author Malte Brunnlieb (03.06.2012)
     */
    private void loadDefaults() {
        try {
            defaults.load(getClass().getResourceAsStream("/resources/bundle.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the value of the given preferences key
     * @param key identifying preferences key
     * @return the preferences value for the given key or null if the key was not found
     * @author Malte Brunnlieb (03.06.2012)
     */
    public String getValue(String key) {
        if (prefs.contains(key)) {
            return prefs.getString(key);
        } else {
            return defaults.getProperty(key);
        }
    }
}
