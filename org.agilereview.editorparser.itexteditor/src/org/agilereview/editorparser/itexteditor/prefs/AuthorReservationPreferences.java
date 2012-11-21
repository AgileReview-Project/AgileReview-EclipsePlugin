/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.prefs;

import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.editorparser.itexteditor.prefs.AuthorPreferencesPojo.AuthorTag;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.google.gson.Gson;

/**
 * This class represents the current eclipse preferences. Furthermore it synchronizes every change in this class with the eclipse preferences.
 * @author Malte Brunnlieb (20.11.2012)
 */
public class AuthorReservationPreferences {
    
    /**
     * Preferences synchronized by JSON
     */
    private AuthorPreferencesPojo pojo;
    
    /**
     * Creates a new synchronized instance for the color reservation preferences
     * @author Malte Brunnlieb (20.11.2012)
     */
    public AuthorReservationPreferences() {
        pojo = loadData();
    }
    
    /**
     * Returns the current locked author on a given {@link AuthorTag} tag
     * @param authorTag {@link AuthorTag} whose reservation string should be returned
     * @return the current author locked on the given {@link AuthorTag} tag or an empty string if the given {@link AuthorTag} tag has no reservation
     * @author Malte Brunnlieb (20.11.2012)
     */
    public String getReservation(AuthorTag authorTag) {
        loadData();
        try {
            return (String) pojo.getClass().getField(authorTag.toString()).get(pojo);
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
     * Returns an ordered list of the current locked reservations.
     * @return a ordered list of the current locked reservations.
     * @author Malte Brunnlieb (21.11.2012)
     */
    public List<String> getReservations() {
        List<String> reservations = new LinkedList<String>();
        for (AuthorTag authorTag : AuthorTag.values()) {
            String author = getReservation(authorTag);
            if (author.trim().isEmpty()) {
                break; // there should be no more reservations by construction
            }
            reservations.add(author);
        }
        return reservations;
    }
    
    /**
     * Removes all reservations except the reservation of the IDEUser
     * @author Malte Brunnlieb (20.11.2012)
     */
    public void clearReservations() {
        for (AuthorTag author : AuthorTag.values()) {
            if (author != AuthorTag.IDEUser) {
                setReservation(author, "");
            }
        }
    }
    
    /**
     * Sets the reservation and propagates the changes into the eclipse preference store
     * @param authorTag {@link AuthorTag} tag which should be set
     * @param author locking author name for the reservation
     * @author Malte Brunnlieb (20.11.2012)
     */
    public void setReservation(AuthorTag authorTag, String author) {
        try {
            pojo.getClass().getField(authorTag.toString()).set(pojo, author.trim());
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
        writeChanges();
    }
    
    /**
     * Adds an reservation if possible and propagates the changes into the eclipse preference store
     * @param author author name for the reservation
     * @author Malte Brunnlieb (20.11.2012)
     */
    public void addReservation(String author) {
        loadData();
        for (AuthorTag authorTag : AuthorTag.values()) {
            if (authorTag != AuthorTag.IDEUser && getReservation(authorTag).trim().isEmpty()) {
                setReservation(authorTag, author);
                break;
            } else if (getReservation(authorTag).trim().equals(author.trim())) {
                break;
            }
        }
    }
    
    /**
     * Returns the current {@link AuthorTag} the given author has been locked or <code>null</code> if there is no reservation for the given author
     * @param author the name of the author
     * @return the {@link AuthorTag}, if a reservation is available for the user<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (21.11.2012)
     */
    public AuthorTag getAuthorTag(String author) {
        loadData();
        for (AuthorTag authorTag : AuthorTag.values()) {
            if (getReservation(authorTag).trim().equals(author.trim())) { return authorTag; }
        }
        return null;
    }
    
    /**
     * Loads the latest preferences from the eclipse preferences store
     * @return a new {@link AuthorPreferencesPojo} which is consistent to the current state of the eclipse preferences store
     * @author Malte Brunnlieb (21.11.2012)
     */
    private AuthorPreferencesPojo loadData() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION,
                AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, scopes);
        Gson gson = new Gson();
        return gson.fromJson(pref, AuthorPreferencesPojo.class);
    }
    
    /**
     * Writes the current entries of the hold {@link AuthorPreferencesPojo} into the eclipse preferences store
     * @author Malte Brunnlieb (21.11.2012)
     */
    private void writeChanges() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(pojo);
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.agilereview.core");
        preferences.put(AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, jsonStr);
    }
    
}
