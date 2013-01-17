package org.agilereview.core.preferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.Activator;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;

/**
 * Class used to initialize default preference values.
 * @author Peter Reuter (21.06.2012)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    /**
     * States whether all preferences have been initialized
     */
    public static volatile boolean isInitialized = false;
    
    @Override
    public void initializeDefaultPreferences() {
        
        final Properties pref = loadDefaultProperties();
        if (pref == null) return;
        
        IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        
        preferences.put(AgileReviewPreferences.AUTHOR, System.getProperty("user.name"));
        preferences.put(AgileReviewPreferences.OPEN_REVIEWS, "");
        preferences.put(AgileReviewPreferences.AUTHOR_COLOR_ALLOCATION, "{ 'IDEUser':'" + System.getProperty("user.name")
                + "','Author2':'','Author3':'','Author4':'','Author5':'','Author6':'" + "','Author7':'','Author8':'','Author9':'','Author10':'' }");
        
        Display.getDefault().syncExec(new Runnable() {
            
            @Override
            public void run() {
                InstanceScope.INSTANCE.getNode("org.eclipse.ui.editors").put(AgileReviewPreferences.AUTHOR_COLOR_DEFAULT,
                        pref.getProperty(AgileReviewPreferences.AUTHOR_COLOR_DEFAULT));
                for (String key : AgileReviewPreferences.AUTHOR_COLORS) {
                    InstanceScope.INSTANCE.getNode("org.eclipse.ui.editors").put(key, pref.getProperty(key));
                }
            }
        });
        
        @SuppressWarnings("unchecked")
        Enumeration<String> properties = (Enumeration<String>) pref.propertyNames();
        while (properties.hasMoreElements()) {
            String next = properties.nextElement();
            preferences.put(next, pref.getProperty(next));
        }
        
        isInitialized = true;
    }
    
    /**
     * Loads the default properties
     * @return the default properties defined in the resources/bundle.properties
     * @author Malte Brunnlieb (15.10.2012)
     */
    private Properties loadDefaultProperties() {
        Properties pref = new Properties();
        try {
            String path = "resources/bundle.properties";
            InputStream stream = PreferenceInitializer.class.getClassLoader().getResourceAsStream(path);
            pref.load(stream);
        } catch (IOException e) {
            ExceptionHandler.logAndNotifyUser("The default preferences for AgileReview could not be initialized."
                    + "Please try restarting Eclipse and consider to write a bug report.", e, Activator.PLUGIN_ID);
            return null;
        }
        return pref;
    }
}
