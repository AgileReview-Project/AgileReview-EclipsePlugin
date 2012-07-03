/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.tools;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * This class provides access to the core preferences of AgileReview
 * @author Malte Brunnlieb (03.07.2012)
 */
public class Preferences {
    
    /**
     * Preference query interface of the core plugin
     */
    private final IPreferencesService props;
    /**
     * Scopes in which will be searched when accessing preferences
     */
    private final IScopeContext[] scopes;
    
    /**
     * Creates a new instance for the AgileReview preferences
     * @author Malte Brunnlieb (03.07.2012)
     */
    public Preferences() {
        scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        props = Platform.getPreferencesService();
    }
    
    /**
     * Returns the preferences value for the given key from the AgileReview core plug-in
     * @param key of the preferences
     * @return AgileReview core value for the given key
     * @author Malte Brunnlieb (03.07.2012)
     */
    public String get(String key) {
        return props.getString("org.agilereview.core", key, "", scopes);
    }
}
