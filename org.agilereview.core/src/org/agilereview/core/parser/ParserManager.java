/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.parser;

import java.util.HashMap;

import org.agilereview.core.external.definition.IEditorParser;
import org.eclipse.ui.IEditorPart;

/**
 * 
 * @author Malte Brunnlieb (04.12.2012)
 */
public class ParserManager {
    
    private HashMap<IEditorPart, IEditorParser> parserMap = new HashMap<IEditorPart, IEditorParser>();
    
    public ParserManager() {
        
    }
    
}
