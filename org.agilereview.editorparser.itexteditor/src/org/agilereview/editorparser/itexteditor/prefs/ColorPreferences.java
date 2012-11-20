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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.google.gson.Gson;

/**
 * This class represents the current eclipse preferences and helps to transform the R,G,B values to java colors.
 * @author Malte Brunnlieb (20.11.2012)
 */
public class ColorPreferences {
    
    /**
     * Enumeration of all available authors
     * @author Malte Brunnlieb (20.11.2012)
     */
    @SuppressWarnings("javadoc")
    public enum Author {
        IDEUser, Author2, Author3, Author4, Author5, Author6, Author7, Author8, Author9, Author10
    }
    
    /**
     * The color of the IDEUser (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String IDEUser;
    /**
     * The color of author 2 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author2;
    /**
     * The color of author 3 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author3;
    /**
     * The color of author 4 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author4;
    /**
     * The color of author 5 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author5;
    /**
     * The color of author 6 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author6;
    /**
     * The color of author 7 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author7;
    /**
     * The color of author 8 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author8;
    /**
     * The color of author 9 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author9;
    /**
     * The color of author 10 (injected by JSON, format 'R,G,B')
     */
    @SuppressWarnings("unused")
    private String Author10;
    
    /**
     * Only for access restriction
     * @author Malte Brunnlieb (20.11.2012)
     */
    private ColorPreferences() {
    }
    
    /**
     * Creates a new synchronized instance of the author color preferences of eclipse
     * @return a synchronized instance of {@link ColorPreferences}
     * @author Malte Brunnlieb (20.11.2012)
     */
    public static ColorPreferences create() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        String pref = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLORS,
                AgileReviewPreferences.AUTHOR_COLORS, scopes);
        Gson gson = new Gson();
        return gson.fromJson(pref, ColorPreferences.class);
    }
    
    /**
     * Returns the current color for the given {@link Author}
     * @param author {@link Author} whose color should be returned
     * @return the current color for the given {@link Author}
     * @author Malte Brunnlieb (20.11.2012)
     */
    public Color getColor(Author author) {
        try {
            String prefString = (String) this.getClass().getField(author.toString()).get(this);
            String[] rgb = prefString.split(",");
            return new Color(Display.getDefault(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
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
}
