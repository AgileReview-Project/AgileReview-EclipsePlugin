/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.prefs;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.google.gson.Gson;

/**
 * This class represents the current eclipse preferences. Furthermore it synchronizes every change in this class with the eclipse preferences.
 * @author Malte Brunnlieb (20.11.2012)
 */
public class ColorReservationPreferences {
    
    /**
     * Enumeration of all available authors
     * @author Malte Brunnlieb (20.11.2012)
     */
    @SuppressWarnings("javadoc")
    public enum Author {
        IDEUser, Author2, Author3, Author4, Author5, Author6, Author7, Author8, Author9, Author10
    }
    
    /**
     * The author locked on IDEUser (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String IDEUser;
    /**
     * The author locked on author 2 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author2;
    /**
     * The author locked on author 3 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author3;
    /**
     * The author locked on author 4 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author4;
    /**
     * The author locked on author 5 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author5;
    /**
     * The author locked on author 6 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author6;
    /**
     * The author locked on author 7 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author7;
    /**
     * The author locked on author 8 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author8;
    /**
     * The author locked on author 9 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author9;
    /**
     * The author locked on author 10 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author10;
    
    /**
     * Only for access restriction
     * @author Malte Brunnlieb (20.11.2012)
     */
    private ColorReservationPreferences() {
    }
    
    /**
     * Creates a new synchronized instance of the author color preferences of eclipse
     * @return a synchronized instance of {@link ColorPreferences}
     * @author Malte Brunnlieb (20.11.2012)
     */
    public static ColorReservationPreferences create() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION,
                AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, scopes);
        Gson gson = new Gson();
        return gson.fromJson(pref, ColorReservationPreferences.class);
    }
    
    /**
     * Returns the current locked author on a given {@link Author} tag
     * @param authorTag {@link Author} whose reservation string should be returned
     * @return the current author locked on the given {@link Author} tag or an empty string if the given {@link Author} tag has no reservation
     * @author Malte Brunnlieb (20.11.2012)
     */
    public String getReservation(Author authorTag) {
        try {
            return (String) this.getClass().getField(authorTag.toString()).get(this);
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
     * Removes all reservations except the reservation of the IDEUser
     * @author Malte Brunnlieb (20.11.2012)
     */
    public void clearReservations() {
        for (Author author : Author.values()) {
            if (author != Author.IDEUser) {
                try {
                    this.getClass().getField(author.toString()).set(this, "");
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
            }
        }
        
    }
    
    /**
     * Sets the reservation and propagates the changes into the eclipse preference store
     * @param authorTag {@link Author} tag which should be set
     * @param author locking author name for the reservation
     * @author Malte Brunnlieb (20.11.2012)
     */
    public void setReservation(Author authorTag, String author) {
        try {
            this.getClass().getField(author.toString()).set(this, author);
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
    }
    
    private void writeChanges() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(this);
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION,
                AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, scopes);
    }
    
}
