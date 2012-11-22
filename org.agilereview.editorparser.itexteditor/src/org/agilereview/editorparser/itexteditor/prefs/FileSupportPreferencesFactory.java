/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.prefs;

import java.util.HashMap;
import java.util.Map;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.google.gson.Gson;

/**
 * This factory implementation reads the eclipse preferences for file support of AgileReview and brings all information in the object representation
 * of {@link FileSupportEntry}s
 * @author Malte Brunnlieb (20.11.2012)
 */
public class FileSupportPreferencesFactory {
    
    /**
     * Representation of one entry for file support of multi line comments.
     * @author Malte Brunnlieb (20.11.2012)
     */
    public class FileSupportEntry {
        /**
         * A two dimensional array containing first the start comment tag and second the end comment tag
         */
        public String[] commentTags;
        /**
         * All file endings of files which support the comment tags in {@link FileSupportEntry#commentTags}
         */
        public String[] fileendings;
    }
    
    /**
     * Creates a mapping of file endings to comment tags from the eclipse preferences store
     * @return a mapping of file endings to comment tags
     * @author Malte Brunnlieb (22.11.2012)
     */
    public static Map<String, String[]> createFileSupportMap() {
        Map<String, String[]> result = new HashMap<String, String[]>();
        FileSupportEntry[] entries = load();
        for (FileSupportEntry entry : entries) {
            for (String fileending : entry.fileendings) {
                result.put(fileending, entry.commentTags);
            }
        }
        return result;
    }
    
    /**
     * Factory method which loads the current eclipse preferences into object representation
     * @return an array of {@link FileSupportEntry}s
     * @author Malte Brunnlieb (20.11.2012)
     */
    private static FileSupportEntry[] load() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.SUPPORTED_FILES,
                AgileReviewPreferences.SUPPORTED_FILES, scopes);
        Gson gson = new Gson();
        return gson.fromJson(pref, FileSupportEntry[].class);
    }
}
