package org.agilereview.editorparser.itexteditor.control;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.editorparser.itexteditor.prefs.AuthorPreferencesPojo.AuthorTag;
import org.agilereview.editorparser.itexteditor.prefs.AuthorReservationPreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

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
     * @return Color which was reserved<br>default comment color otherwise
     */
    public Color addReservation(String author) {
        reservationPrefs.addReservation(author);
        return getColor(author);
    }
    
    /**
     * The correspondent color for the author will be returned. If no color for reservation is available any more, the default comment color will be
     * returned.
     * @param author for which the color should be returned
     * @return the color which is registered for the given author<br>the default comment color, if no reservation was possible any more
     */
    public Color getColor(String author) {
        String prop;
        AuthorTag authorTag = reservationPrefs.getAuthorTag(author);
        if (isMultiColorEnabled() && authorTag != null) {
            prop = getColor(authorTag);
        } else {
            IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
            prop = Platform.getPreferencesService().getString("org.agilereview.core", AgileReviewPreferences.AUTHOR_COLOR_DEFAULT, "", scopes);
        }
        String[] rgb = prop.split(",");
        return new Color(PlatformUI.getWorkbench().getDisplay(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }
    
    /**
     * Returns the current color for the given {@link AuthorTag}
     * @param author {@link AuthorTag} whose color should be returned
     * @return the current color for the given {@link AuthorTag} in R,G,B format
     * @author Malte Brunnlieb (20.11.2012)
     */
    private String getColor(AuthorTag author) {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        return Platform.getPreferencesService().getString("org.agilereview.core", "org.agilereview.preferences.author_color_" + author.toString(),
                AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, scopes);
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