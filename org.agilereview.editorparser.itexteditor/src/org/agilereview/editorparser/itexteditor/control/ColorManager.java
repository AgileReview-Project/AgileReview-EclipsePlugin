package org.agilereview.editorparser.itexteditor.control;

import org.agilereview.editorparser.itexteditor.prefs.AuthorColorPreferences;
import org.agilereview.editorparser.itexteditor.prefs.AuthorReservationPreferences;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

/**
 * The ColorManager is responsible for a consistent global management of the annotation color scheme, such that up to 10 different annotation colors
 * for different authors are displayed. Any further annotation will be displayed by a default color.
 * @author Malte Brunnlieb (20.11.12)
 */
public class ColorManager {
    
    private AuthorReservationPreferences reservationPrefs = new AuthorReservationPreferences();
    private AuthorColorPreferences colorPrefs = new AuthorColorPreferences();
    
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
        if (isMultiColorEnabled() && reservationPrefs.getAuthorTag(author) != null) {
            prop = colorPrefs
            prop = PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ANNOTATION_COLORS_AUTHOR[authors.indexOf(author)]);
        } else {
            prop = PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.ANNOTATION_COLOR);
        }
        String[] rgb = prop.split(",");
        return new Color(PlatformUI.getWorkbench().getDisplay(), Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }
    
    /**
     * Returns the author-name which is currently associated with the colorof the given number
     * @param colorNumber number of the color
     * @return author-name of color number
     */
    public String getAuthorName(int colorNumber) {
        String result = "";
        if (colorNumber < authors.size()) {
            result = authors.get(colorNumber);
        }
        return result;
    }
    
    /**
     * Checks whether the given author has reserved his own color in the current scheme.
     * @param author which should be checked for a reservation
     * @return true, if the given author has reserved a customized color<br>false, otherwise
     */
    public boolean hasCustomizedColor(String author) {
        return authors.contains(author);
    }
    
    /**
     * Checks whether a single or multiple annotation colors are to be used.
     * @return true if multiple colors are to be used<br>false otherwise
     */
    public boolean isMultiColorEnabled() {
        return PropertiesManager.getPreferences().getBoolean(PropertiesManager.EXTERNAL_KEYS.ANNOTATION_COLOR_ENABLED);
    }
}