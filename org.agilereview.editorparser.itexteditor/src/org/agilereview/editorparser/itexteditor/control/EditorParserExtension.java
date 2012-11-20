/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.control;

import org.agilereview.core.external.definition.IEditorParser;
import org.eclipse.core.resources.IFile;

/**
 * 
 * @author Malte Brunnlieb (11.11.2012)
 */
public class EditorParserExtension implements IEditorParser {
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTags(org.agilereview.core.external.definition.IFile, int, int, java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void addTags(IFile file, int startLine, int endLine, String tagId) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTagsToCurrentEditorSelection(java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void addTagsToCurrentEditorSelection(String tagId) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTags(org.agilereview.core.external.definition.IFile, java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void removeTags(IFile file, String tagId) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTagsInCurrentEditor(java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void removeTagsInCurrentEditor(String tagId) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#clearAllTags()
     * @author Malte Brunnlieb (19.11.2012)
     */
    @Override
    public void clearAllTags() {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#reparse()
     * @author Malte Brunnlieb (19.11.2012)
     */
    @Override
    public void reparse() {
        // TODO Auto-generated method stub
        
    }
    
}
