/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.test.mock.editorparser.external;

import org.agilereview.core.external.definition.IEditorParser;
import org.eclipse.core.resources.IFile;

/**
 * Mock for an {@link IEditorParser} extension
 * @author Malte Brunnlieb (15.07.2012)
 */
public class EditorParserMock implements IEditorParser {
    
    /**
     * Instance of the EditorParser
     */
    public static EditorParserMock instance;
    
    /**
     * Creates a new instance of the {@link EditorParserMock}. This constructor should not be used. It only will set the instance created by the
     * extension point
     * @author Malte Brunnlieb (15.07.2012)
     */
    public EditorParserMock() {
        instance = this;
    }
    
    /**
     * Waits until this extension point has been created
     * @author Malte Brunnlieb (15.07.2012)
     */
    public static void waitUnitCreation() {
        while (instance != null) {
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTags(org.agilereview.core.external.definition.IFile, int, int, java.lang.String)
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    public void addTags(IFile file, int startLine, int endLine, String tagId) {
        // do nothing for the moment
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTagsToCurrentEditorSelection(java.lang.String)
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    public void addTagsToCurrentEditorSelection(String tagId) {
        // do nothing for the moment
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTags(org.agilereview.core.external.definition.IFile, java.lang.String)
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    public void removeTags(IFile file, String tagId) {
        // do nothing for the moment
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTagsInCurrentEditor(java.lang.String)
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    public void removeTagsInCurrentEditor(String tagId) {
        // do nothing for the moment
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#clearAllTags()
     * @author Malte Brunnlieb (20.11.2012)
     */
    @Override
    public void clearAllTags() {
        // do nothing for the moment
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#reparse()
     * @author Malte Brunnlieb (20.11.2012)
     */
    @Override
    public void reparse() {
        // do nothing for the moment
    }
    
}
