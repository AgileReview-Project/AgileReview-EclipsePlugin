/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.editorparser.itexteditor.control;

import java.util.HashMap;
import java.util.Map;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.editorparser.itexteditor.Activator;
import org.agilereview.editorparser.itexteditor.exception.NoDocumentFoundException;
import org.agilereview.editorparser.itexteditor.prefs.FileSupportPreferencesFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Extension interface for the {@link IEditorParser} extension point and manager class for all created editor parsers of this plug-in
 * @author Malte Brunnlieb (11.11.2012)
 */
public class EditorParserExtension implements IEditorParser {
    
    private HashMap<IEditorPart, TagParser> parserMap = new HashMap<IEditorPart, TagParser>();
    private HashMap<IEditorPart, AnnotationManager> annotationManagerMap = new HashMap<IEditorPart, AnnotationManager>();
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTagsToEditorSelection(org.eclipse.ui.IEditorPart, java.lang.String, java.lang.String[])
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void addTagsToEditorSelection(IEditorPart editor, String tagId, String[] multiLineCommentTags) {
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            if (!parserMap.containsKey(editor)) {
                addInstance(editor);
            }
            try {
                parserMap.get(editor).addTagsInDocument(tagId);
                annotationManagerMap.get(editor).addAnnotation(tagId, parserMap.get(editor).getPosition(tagId));
            } catch (BadLocationException e) {
                ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: Invalid comment position.", e, Activator.PLUGIN_ID);
            } catch (CoreException e) {
                ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: Internal eclipse exception.", e, Activator.PLUGIN_ID);
            }
        } else {
            ExceptionHandler.warnUser("The comment could not be added to the document as the underlying file could not be retreived.");
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTagsInEditor(org.eclipse.ui.IEditorPart, java.lang.String, java.lang.String[])
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeTagsInEditor(IEditorPart editor, String tagId, String[] multiLineCommentTags) {
        IFile file = (IFile) editor.getAdapter(IFile.class);
        if (file != null) {
            if (!parserMap.containsKey(editor)) {
                addInstance(editor);
            }
            try {
                parserMap.get(editor).removeTagsInDocument(tagId);
                annotationManagerMap.get(editor).deleteAnnotation(tagId);
            } catch (BadLocationException e) {
                ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: Invalid comment position.", e, Activator.PLUGIN_ID);
            } catch (CoreException e) {
                ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: Internal eclipse exception.", e, Activator.PLUGIN_ID);
            }
        } else {
            ExceptionHandler.warnUser("The comment could not be removed from document as the underlying file could not be retreived.");
        }
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
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeAllInstances()
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeAllInstances() {
        parserMap.clear();
        for (AnnotationManager am : annotationManagerMap.values()) {
            am.clearAnnotations();
        }
        annotationManagerMap.clear();
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeParser(org.eclipse.ui.IEditorPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void removeParser(IEditorPart editor) {
        parserMap.remove(editor);
        AnnotationManager am = annotationManagerMap.remove(editor);
        am.clearAnnotations();
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addInstance(org.eclipse.ui.IEditorPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void addInstance(IEditorPart editor) {
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            if (!parserMap.containsKey(editor)) {
                Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
                String[] tags = fileendingToCommentTagsMap.get(file.getFullPath().getFileExtension());
                try {
                    parserMap.put(editor, new TagParser((ITextEditor) editor, tags));
                    annotationManagerMap.put(editor, new AnnotationManager(editor));
                } catch (NoDocumentFoundException e) {
                    ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: No document found for the current editor.", e,
                            Activator.PLUGIN_ID);
                } catch (CoreException e) {
                    ExceptionHandler.logAndNotifyUser("Parsing error of the ITextEditor parser: Internal eclipse exception.", e, Activator.PLUGIN_ID);
                }
            }
        } else {
            ExceptionHandler.warnUser("The comment could not be added to the document as the underlying file could not be retreived.");
        }
    }
    
}
