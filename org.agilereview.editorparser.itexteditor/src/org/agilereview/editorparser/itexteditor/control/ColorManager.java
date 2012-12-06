package org.agilereview.editorparser.itexteditor.control;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.editorparser.itexteditor.prefs.AuthorReservationPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * The ColorManager is responsible for a consistent global management of the annotation color scheme, such that up to 10 different annotation colors
 * for different authors are displayed. Any further annotation will be displayed by a default color.
 * @author Malte Brunnlieb (20.11.12)
 */
public class ColorManager {
    
    /**
     * Author color reservation preferences
     */
    private AuthorReservationPreferences reservationPrefs = new AuthorReservationPreferences();
    
    /**
     * Reserves a new color for a given author when some is available and the author has not been registered yet.
     * @param author for which a new customized color should be reserved
     */
    public void addReservation(String author) {
        reservationPrefs.addReservation(author);
    }
    
    /**
     * Checks whether the given author has reserved his own color in the current scheme.
     * @param author which should be checked for a reservation
     * @return true, if the given author has reserved a customized color<br>false, otherwise
     */
    public boolean hasCustomizedColor(String author) {
        return reservationPrefs.getAuthorTag(author) != null;
    }
    
    /**
     * Checks whether a single or multiple annotation colors are to be used.
     * @return true if multiple colors are to be used<br>false otherwise
     */
    public boolean isMultiColorEnabled() {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        return Platform.getPreferencesService().getBoolean("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLORS_ENABLED, false, scopes);
    }
}