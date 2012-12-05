/**
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.Activator;
import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.core.parser.NullParser;
import org.agilereview.core.preferences.dataprocessing.FileSupportPreferencesFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Controller for the {@link IEditorParser} extension point.
 * @author Malte Brunnlieb (13.07.2012)
 */
public class EditorParserController extends AbstractController<IEditorParser> implements IPartListener, IPerspectiveListener {
    
    /**
     * ExtensionPoint id for extensions implementing {@link IEditorParser}
     */
    public static final String IEDITORPARSER_ID = "org.agilereview.core.EditorParser";
    /**
     * A mapping of supported editor classes to the parser extension supporting the editor type
     */
    private static final HashMap<Class<?>, String> classToExtensionMap = new HashMap<Class<?>, String>();
    /**
     * A mapping of editor parser plug-in IDs to currently instantiated {@link IEditorParser} objects
     */
    private static final HashMap<String, IEditorParser> editorParserMap = new HashMap<String, IEditorParser>();
    /**
     * Flag indicating whether all issues for initialization are finished
     */
    private volatile boolean initialized = false;
    /**
     * This boolean value holds if the AgileReview perspective is currently open
     */
    private boolean perspectiveOpen;
    
    /**
     * Creates a new instance of the {@link EditorParserController}
     * @author Malte Brunnlieb (13.07.2012)
     */
    EditorParserController() {
        super(IEDITORPARSER_ID);
        perspectiveOpen = "org.agilereview.perspective".equals(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective()
                .getId());
        checkForNewClients();
    }
    
    /**
     * Adds comment tags to the current selection of the currently opened editor. The tags will capture the tagId as information.
     * @param tagId id for identifying the connected comment
     * @param editor {@link IEditorPart} in which the tags should be added
     * @author Malte Brunnlieb (04.12.2012)
     */
    public void addTagsToEditorSelection(IEditorPart editor, String tagId) {
        IEditorParser parser = getParser(editor.getClass());
        Map<String, String[]> fileSupportMap = FileSupportPreferencesFactory.createFileSupportMap();
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            String[] fileendings = fileSupportMap.get(file.getFileExtension());
            if (fileendings != null) {
                parser.addTagsToEditorSelection(editor, tagId, fileendings);
            } else {
                // No annotations supported TODO: perhaps notify with "remember my answer"
            }
        } else {
            ExceptionHandler.warnUser("Please save the current editor as a file in order to have annotation support.");
        }
    }
    
    /**
     * Removes all tags according to the given tagId
     * @param tagId id of the comment to be removed
     * @param editor {@link IEditorPart} in which the tags with the given id should be removed
     * @author Malte Brunnlieb (04.12.2012)
     */
    public void removeTags(IEditorPart editor, String tagId) {
        IEditorParser parser = getParser(editor.getClass());
        Map<String, String[]> fileSupportMap = FileSupportPreferencesFactory.createFileSupportMap();
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        if (file != null) {
            String[] fileendings = fileSupportMap.get(file.getFileExtension());
            if (fileendings != null) {
                parser.removeTagsInEditor(editor, tagId, fileendings);
            } else {
                // No annotations supported TODO: perhaps notify with "remember my answer"
            }
        } else {
            ExceptionHandler.warnUser("Please save the current editor as a file in order to have annotation support.");
        }
    }
    
    /**
     * Evaluates whether the editor class is supported by any {@link IEditorParser} and returns it if possible.
     * @param editorClass class of the editor to be parsed
     * @return the {@link IEditorParser} supporting the given editor class,<br><code>null</code> if there is none TODO return null parser
     * @author Malte Brunnlieb (15.07.2012)
     */
    private IEditorParser getParser(Class<?> editorClass) {
        while (!initialized) {
        }
        synchronized (classToExtensionMap) {
            for (Class<?> clazz : classToExtensionMap.keySet()) {
                if (clazz.isAssignableFrom(editorClass)) {
                    try {
                        String plugInID = classToExtensionMap.get(clazz);
                        if (!editorParserMap.containsKey(plugInID)) {
                            editorParserMap.put(plugInID, createNewExtension(classToExtensionMap.get(clazz)));
                        }
                        return editorParserMap.get(plugInID);
                    } catch (CoreException e) {
                        ExceptionHandler.logAndNotifyUser("An error occurred while creating the EditorParser " + classToExtensionMap.get(clazz), e,
                                Activator.PLUGIN_ID);
                        e.printStackTrace();
                    }
                }
            }
            return new NullParser();
        }
    }
    
    /**
     * Synchronize the map of {@link IEditorParser} extensions and their supported editor classes
     * @see org.agilereview.core.controller.extension.AbstractController#doAfterCheckForClients()
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    protected void doAfterCheckForClients() {
        synchronized (classToExtensionMap) {
            Set<String> extensions = getAvailableExtensions();
            classToExtensionMap.clear();
            for (String extension : extensions) {
                Class<?> clazz = getClass(extension, "editor");
                if (clazz != null) {
                    classToExtensionMap.put(clazz, extension);
                }
            }
        }
        initialized = true;
    }
    
    /**
     * Loads the class referenced by the attribute name of the given extension
     * @param id ID of the extension
     * @param attrName attribute name which contains a class declaration
     * @return the {@link Class} declared in the given attribute or <code>null</code> if the class could not be loaded
     * @author Malte Brunnlieb (15.07.2012)
     */
    private Class<?> getClass(String id, String attrName) {
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(IEDITORPARSER_ID);
        for (IConfigurationElement e : config) {
            if (e.getAttribute("id").equals(id)) {
                try {
                    return Platform.getBundle(e.getDeclaringExtension().getContributor().getName()).loadClass(e.getAttribute(attrName));
                } catch (InvalidRegistryObjectException e1) {
                    ExceptionHandler.logAndNotifyUser("An exception occurred when accessing the EditorParser plugin " + id, e1, Activator.PLUGIN_ID);
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    ExceptionHandler.logAndNotifyUser("The editor class the EditorParser plugin " + id + " should support, cannot be loaded. "
                            + "Most likely this is a problem of the EditorParser plugin.", e1, Activator.PLUGIN_ID);
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
    
    //################################################
    //############## IPartListener ###################
    //################################################
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        if (perspectiveOpen && part instanceof IEditorPart) {
            IEditorParser parser = getParser(part.getClass());
            try {
                parser.addInstance((IEditorPart) part);
            } catch (Throwable e) {
                ExceptionHandler.logAndNotifyUser("An unknown exception was thrown by an editor plugin while adding a parser instance. "
                        + "Try to reopen the editor. If the error occurs regularily please consider to write a bug report.", e, Activator.PLUGIN_ID);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        if (perspectiveOpen && part instanceof IEditorPart) {
            IEditorParser parser = getParser(part.getClass());
            try {
                parser.removeParser((IEditorPart) part);
            } catch (Throwable e) {
                ExceptionHandler.logAndNotifyUser("An unknown exception was thrown by an editor plugin while removing a parser instance. "
                        + "If the error occurs regularily please consider to write a bug report.", e, Activator.PLUGIN_ID);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
    }
    
    //################################################
    //########### IPerspectiveListener ###############
    //################################################
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
        if ("org.agilereview.perspective".equals(perspective)) {
            perspectiveOpen = true;
            IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor != null) {
                IEditorParser parser = getParser(editor.getClass());
                parser.addInstance(editor);
            }
        } else {
            perspectiveOpen = false;
            for (IEditorParser parser : editorParserMap.values()) {
                parser.removeAllInstances();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
     * @author Malte Brunnlieb (04.12.2012)
     */
    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
    }
}
