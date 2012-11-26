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

import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.core.external.exception.EditorCurrentlyNotOpenException;
import org.agilereview.core.external.exception.FileNotSupportedException;
import org.agilereview.core.external.exception.UnknownException;
import org.agilereview.editorparser.itexteditor.prefs.FileSupportPreferencesFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Extension interface for the {@link IEditorParser} extension point and manager class for all created editor parsers of this plug-in
 * @author Malte Brunnlieb (11.11.2012)
 */
public class EditorParserExtension implements IEditorParser {
    
    private HashMap<IFile, TagParser> parserMap = new HashMap<IFile, TagParser>();
    
    /*
     * (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTags(org.eclipse.core.resources.IFile, int, int, java.lang.String)
     * @author Malte Brunnlieb (22.11.2012)
     */
    @Override
    public void addTags(IFile file, int startLine, int endLine, String tagId) throws FileNotSupportedException, EditorCurrentlyNotOpenException,
            UnknownException {
        try {
            if (file == null || !file.exists()) throw new FileNotSupportedException();
            
            if (!parserMap.containsKey(file)) {
                IEditorPart editor = getOpenEditor(file).getEditor(true);
                if (editor != null) {
                    if (editor instanceof ITextEditor) {
                        Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
                        if (fileendingToCommentTagsMap.containsKey(file.getFileExtension())) {
                            String[] tags = fileendingToCommentTagsMap.get(file.getFileExtension());
                            if (tags == null) {
                                throw new FileNotSupportedException();
                            }
                            parserMap.put(file, new TagParser((ITextEditor) editor, tags));
                        } else {
                            throw new FileNotSupportedException();
                        }
                    } else {
                        throw new UnknownException();
                    }
                } else {
                    throw new EditorCurrentlyNotOpenException();
                }
            }
            parserMap.get(file).addTagsInDocument(tagId, startLine, endLine);
        } catch (FileNotSupportedException e) {
            throw e;
        } catch (EditorCurrentlyNotOpenException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnknownException();
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#addTagsToCurrentEditorSelection(java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void addTagsToCurrentEditorSelection(String tagId) throws FileNotSupportedException, UnknownException, EditorCurrentlyNotOpenException {
        try {
            IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor != null) {
                IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
                if (file != null) {
                    if (!parserMap.containsKey(file)) {
                        if (editor instanceof ITextEditor) {
                            Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
                            if (fileendingToCommentTagsMap.containsKey(file.getFileExtension())) {
                                String[] tags = fileendingToCommentTagsMap.get(file.getFullPath().getFileExtension());
                                if (tags == null) {
                                    throw new FileNotSupportedException();
                                }
                                parserMap.put(file, new TagParser((ITextEditor) editor, tags));
                            } else {
                                throw new FileNotSupportedException();
                            }
                        } else {
                            throw new UnknownException();
                        }
                    }
                    parserMap.get(file).addTagsInDocument(tagId);
                } else {
                    // else should not occur
                    throw new UnknownException();
                }
            } else {
                throw new EditorCurrentlyNotOpenException();
            }
        } catch (FileNotSupportedException e) {
            throw e;
        } catch (EditorCurrentlyNotOpenException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnknownException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTags(org.agilereview.core.external.definition.IFile, java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void removeTags(IFile file, String tagId) throws FileNotSupportedException, EditorCurrentlyNotOpenException, UnknownException {
        try {
            if (file == null || !file.exists()) throw new FileNotSupportedException();
            
            if (!parserMap.containsKey(file)) {
                IEditorPart editor = getOpenEditor(file).getEditor(true);
                if (editor != null) {
                    if (editor instanceof ITextEditor) {
                        Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
                        if (fileendingToCommentTagsMap.containsKey(file.getFileExtension())) {
                            String[] tags = fileendingToCommentTagsMap.get(file.getFileExtension());
                            if (tags == null) {
                                throw new FileNotSupportedException();
                            }
                            parserMap.put(file, new TagParser((ITextEditor) editor, tags));
                        } else {
                            throw new FileNotSupportedException();
                        }
                    } else {
                        throw new UnknownException();
                    }
                } else {
                    throw new EditorCurrentlyNotOpenException();
                }
            }
            parserMap.get(file).removeTagsInDocument(tagId);
        } catch (FileNotSupportedException e) {
            throw e;
        } catch (EditorCurrentlyNotOpenException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnknownException();
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IEditorParser#removeTagsInCurrentEditor(java.lang.String)
     * @author Malte Brunnlieb (11.11.2012)
     */
    @Override
    public void removeTagsInCurrentEditor(String tagId) throws FileNotSupportedException, EditorCurrentlyNotOpenException, UnknownException {
        try {
            IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor != null) {
                IFile file = (IFile) editor.getAdapter(IFile.class);
                if (file != null) {
                    if (!parserMap.containsKey(file)) {
                        if (editor instanceof ITextEditor) {
                            Map<String, String[]> fileendingToCommentTagsMap = FileSupportPreferencesFactory.createFileSupportMap();
                            if (fileendingToCommentTagsMap.containsKey(file.getFileExtension())) {
                                String[] tags = fileendingToCommentTagsMap.get(file.getFullPath().getFileExtension());
                                if (tags == null) {
                                    throw new FileNotSupportedException();
                                }
                                parserMap.put(file, new TagParser((ITextEditor) editor, tags));
                            } else {
                                throw new FileNotSupportedException();
                            }
                        } else {
                            throw new UnknownException();
                        }
                    }
                    parserMap.get(file).removeTagsInDocument(tagId);
                } else {
                    // else should not occur
                    throw new UnknownException();
                }
            } else {
                throw new EditorCurrentlyNotOpenException();
            }
        } catch (FileNotSupportedException e) {
            throw e;
        } catch (EditorCurrentlyNotOpenException e) {
            throw e;
        } catch (Throwable e) {
            throw new UnknownException();
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
                if (editorFile != null && editorFile.getFullPath().toOSString().equals(file.getFullPath().toOSString())) {
                    return editor;
                }
            } catch (PartInitException e) {
                // suppress as the editor has to be restarted manually, so we can manipulate the file directly
            }
        }
        return null;
    }
    
}
