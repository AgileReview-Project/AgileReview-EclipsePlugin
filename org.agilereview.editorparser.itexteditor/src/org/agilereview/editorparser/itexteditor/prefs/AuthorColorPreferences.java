/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.prefs;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.editorparser.itexteditor.prefs.AuthorPreferencesPojo.AuthorTag;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.google.gson.Gson;

/**
 * This class represents the current eclipse preferences and helps to transform the R,G,B values to java colors.
 * @author Malte Brunnlieb (20.11.2012)
 */
public class AuthorColorPreferences {
    
    /**
     * Preferences synchronized by JSON
     */
    private AuthorPreferencesPojo pojo;
    
    /**
     * Creates a new synchronized instance of the current color preferences
     * @author Malte Brunnlieb (20.11.2012)
     */
    public AuthorColorPreferences() {
        pojo = loadData();
    }
    
    /**
     * Returns the current color for the given {@link AuthorTag}
     * @param author {@link AuthorTag} whose color should be returned
     * @return the current color for the given {@link AuthorTag} in R,G,B format
     * @author Malte Brunnlieb (20.11.2012)
     */
    public String getColor(AuthorTag author) {
        loadData();
        try {
            return (String) pojo.getClass().getField(author.toString()).get(pojo);
        } catch (IllegalArgumentException e) {
            // invalid case by construction
            e.printStackTrace();
        } catch (SecurityException e) {
            // invalid case by construction
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // invalid case by construction
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // invalid case by construction
            e.printStackTrace();
        }
        return null; //invalid case by construction
    }
    
    /**
     * Loads the latest preferences from the eclipse preferences store
     * @return a new {@link AuthorPreferencesPojo} which is consistent to the current state of the eclipse preferences store
     * @author Malte Brunnlieb (21.11.2012)
     */
    private AuthorPreferencesPojo loadData() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLORS,
                AgileReviewPreferences.AUTHOR_COLORS, scopes);
        Gson gson = new Gson();
        return gson.fromJson(pref, AuthorPreferencesPojo.class);
    }
    
}
