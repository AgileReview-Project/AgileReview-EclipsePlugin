/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.control;

import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.editorparser.itexteditor.prefs.FileSupportPreferencesFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Extension interface for the {@link IEditorParser} extension point
 * @author Malte Brunnlieb (11.11.2012)
 */
public class EditorParserExtension implements IEditorParser {
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTags(org.agilereview.core.external.definition.IFile, int, int, java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void addTags(IFile file, int startLine, int endLine, String tagId) {
        if (file == null || !file.exists()) return;
       
        Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
        IEditorPart editor = getOpenEditor(file).getEditor(true);
        if (editor != null && editor instanceof ITextEditor && ) {
            
            new TagParser(editor, , )
        } else {
            //TODO file parsing
        }
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
    
    /**
     * Checks whether the given file is opened in an editor. Is this the case, the editor reference will be return.
     * @param file The file which should be checked to be opened by an editor
     * @return The editorReference, if the file is opened by an editor<br>null, otherwise
     * @author Malte Brunnlieb (22.11.2012)
     */
    private IEditorReference getOpenEditor(IFile file) {
        IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
        for (IEditorReference editor : editors) {
            try {
                IFile editorFile = (IFile) editor.getEditorInput().getAdapter(IFile.class);
                if (editorFile != null && editorFile.getFullPath().toOSString().equals(file.getFullPath().toOSString())) { return editor; }
            } catch (PartInitException e) {
                // suppress as the editor has to be restarted manually, so we can manipulate the file directly
            }
        }
        return null;
    }
    
}
