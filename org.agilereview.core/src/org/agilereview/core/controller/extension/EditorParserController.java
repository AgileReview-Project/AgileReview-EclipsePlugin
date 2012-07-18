/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.controller.extension;

import java.util.HashMap;
import java.util.Set;

import org.agilereview.core.exception.ExceptionHandler;
import org.agilereview.core.external.definition.IEditorParser;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

/**
 * Controller for the {@link IEditorParser} extension point.
 * @author Malte Brunnlieb (13.07.2012)
 */
public class EditorParserController extends AbstractController<IEditorParser> {
    
    /**
     * ExtensionPoint id for extensions implementing {@link IEditorParser}
     */
    public static final String IEDITORPARSER_ID = "org.agilereview.core.EditorParser";
    /**
     * A mapping of supported editor classes to the parser extension supporting the editor type
     */
    public static final HashMap<Class<?>, String> editorParserMap = new HashMap<Class<?>, String>();
    
    /**
     * Creates a new instance of the {@link EditorParserController}
     * @author Malte Brunnlieb (13.07.2012)
     */
    EditorParserController() {
        super(IEDITORPARSER_ID);
    }
    
    /**
     * Evaluates whether the editor class is supported by any {@link IEditorParser} and returns it if possible.
     * @param editorClass class of the editor to be parsed
     * @return the {@link IEditorParser} supporting the given editor class or <code>null</code> if there is none TODO return null parser
     * @author Malte Brunnlieb (15.07.2012)
     */
    public IEditorParser createParser(Class<?> editorClass) {
        synchronized (editorParserMap) {
            for (Class<?> clazz : editorParserMap.keySet()) {
                if (clazz.isAssignableFrom(editorClass)) { //TODO check if this works
                    try {
                        return createNewExtension(editorParserMap.get(clazz));
                    } catch (CoreException e) {
                        ExceptionHandler.logAndNotifyUser("An error occurred while creating the EditorParser " + editorParserMap.get(clazz), e);
                        e.printStackTrace();
                    }
                }
            }
            return null; //TODO return null parser
        }
    }
    
    /**
     * Synchronize the map of {@link IEditorParser} extensions and their supported editor classes
     * @see org.agilereview.core.controller.extension.AbstractController#doAfterCheckForClients()
     * @author Malte Brunnlieb (15.07.2012)
     */
    @Override
    protected void doAfterCheckForClients() {
        synchronized (editorParserMap) {
            Set<String> extensions = getAvailableExtensions();
            editorParserMap.clear();
            for (String extension : extensions) {
                Class<?> clazz = getClass(extension, "editor");
                if (clazz != null) {
                    editorParserMap.put(clazz, extension);
                }
            }
        }
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
                    ExceptionHandler.logAndNotifyUser("An exception occurred when accessing the EditorParser plug-in " + id, e1);
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    ExceptionHandler.logAndNotifyUser("The editor class the EditorParser plug-in " + id
                            + " should support, cannot be loaded. Most likely this is a problem of the EditorParser plug-in.", e1);
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
    
}
