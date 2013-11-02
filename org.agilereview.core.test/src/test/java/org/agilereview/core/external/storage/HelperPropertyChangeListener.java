package org.agilereview.core.external.storage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Helper class to check for correct changes of properties. 
 * @author Peter Reuter (03.06.2012)
 */
public class HelperPropertyChangeListener implements PropertyChangeListener {

	/**
	 * Indicates whether the property specified by propertyName was changed. 
	 */
	private boolean propertyChanged = false;
	/**
	 * Name of the property for which changes should occur. 
	 */
	private String propertyName = "";
	
	/**
	 * A property change listener thats listens for changes of a single property.
	 * @param propertyName The property whose changes are listened for.
	 * @author Peter Reuter (03.06.2012)
	 */
	public HelperPropertyChangeListener(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(propertyName)) {
			this.propertyChanged = true;
		}
	}
	
	/**
	 * @return A boolean value indicating whether the observed property was changed or not.
	 * @author Peter Reuter (03.06.2012)
	 */
	public boolean getPropertyChanged() {
		return propertyChanged;
	}
}
