package org.agilereview.core.preferences;

import org.agilereview.core.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Class used to initialize default preference values.
 * @author Peter Reuter (21.06.2012)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		// TODO move preferences names to special class with static fields!
		preferences.put("author", System.getProperty("user.name"));
		preferences.put("open_reviews", "");
	}

}
