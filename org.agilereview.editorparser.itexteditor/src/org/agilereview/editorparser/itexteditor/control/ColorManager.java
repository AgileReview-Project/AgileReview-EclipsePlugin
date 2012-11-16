package org.agilereview.editorparser.itexteditor.control;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

import de.tukl.cs.softech.agilereview.tools.PropertiesManager;
import de.tukl.cs.softech.agilereview.views.ViewControl;
import de.tukl.cs.softech.agilereview.views.commenttable.CommentTableView;

/**
 * The ColorManager is responsible for a consistent global management of the annotation color scheme, such that up to 10 different annotation colors
 * for different authors are displayed. Any further annotation will be displayed by a default color.
 * @author Malte Brunnlieb (AgileReview Team)
 */
public class ColorManager {
    
    /**
     * Map of the first authors having customized colors
     */
    private static ArrayList<String> authors = new ArrayList<String>();
    
    /**
     * Resets the current color scheme
     */
    public static void resetColorScheme() {
        authors.clear();
        authors.add(PropertiesManager.getPreferences().getString(PropertiesManager.EXTERNAL_KEYS.AUTHOR_NAME));
        
        if (ViewControl.isOpen(CommentTableView.class)) {
            CommentTableView.getInstance().cleanEditorReferences();
            CommentTableView.getInstance().resetEditorReferences();
        }
    }
    
    /**
     * Sets the given author to the IDE user using every time the same color (the first specified color); If the new IDE user has been registered, his
     * position will be swapped with the old IDE user
     * @param author
     */
    public static void changeIDEUser(String author) {
        if (authors.isEmpty()) {
            authors.add(author);
        } else if (!author.equals(authors.get(0))) {
            //if the new IDE user was registered as an author, swap his position with the IDE user position (0)
            String oldAuthor = authors.remove(0);
            int i = authors.indexOf(author);
            if (i >= 0) {
                authors.remove(i);
                authors.add(i, oldAuthor);
            }
            authors.add(0, author);
        }
        
        if (ViewControl.isOpen(CommentTableView.class)) {
            CommentTableView.getInstance().cleanEditorReferences();
            CommentTableView.getInstance().resetEditorReferences();
        }
    }
    
    /**
     * Reserves a new color for a given author when some is available and the author has not been registered yet.
     * @param author for which a new customized color should be reserved
     * @return Color which was reserved<br>default comment color otherwise
     */
    public static Color addReservation(String author) {
        //add the author which was intended to be added
        if (!authors.contains(author) && authors.size() < 10) {
            authors.add(author);
        }
        return getColor(author);
    }
    
    /**
     * The correspondent color for the author will be returned. If no color for reservation is available any more, the default comment color will be
     * returned.
     * @param author for which the color should be returned
     * @return the color which is registered for the given author<br>the default comment color, if no reservation was possible any more
     */
    public static Color getColor(String author) {
        String prop;
        if (isMultiColorEnabled() && authors.contains(author)) {
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
    public static String getAuthorName(int colorNumber) {
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
    public static boolean hasCustomizedColor(String author) {
        return authors.contains(author);
    }
    
    /**
     * Checks whether a single or multiple annotation colors are to be used.
     * @return true if multiple colors are to be used<br>false otherwise
     */
    public static boolean isMultiColorEnabled() {
        return PropertiesManager.getPreferences().getBoolean(PropertiesManager.EXTERNAL_KEYS.ANNOTATION_COLOR_ENABLED);
    }
    
    /**
     * Returns the index of the given author. The index represents a unique id which will be incremented with each registered author until it reaches
     * 9. If the author has not been registered for a customized color -1 will be returned.
     * @param author for which the index should be returned
     * @return index of the given author if there is a reservation for the author<br> -1, otherwise
     */
    public static int getIndexOf(String author) {
        return authors.indexOf(author);
    }
}