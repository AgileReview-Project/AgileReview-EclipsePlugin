/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.storage.xml.test;

import org.agilereview.storage.xml.SourceFolderManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.rules.ExternalResource;

/**
 * JUnit Rule for a temporary {@link IProject} that represents an AgileReview Source Folder. Should be created in each test method by createProject when it should be used.
 * @author Peter Reuter (09.07.2012)
 */
public class TmpAgileReviewSourceFolder extends ExternalResource {
    
    /**
     * Name of the Project
     */
    private final String name;
    /**
     * Project reference
     */
    private IProject arSourceFolder;
    
    /**
     * Creates a new object instance, but does not create the project in the test workspace. This should be done manually by createProject()
     * @param name the project should be named with
     * @author Malte Brunnlieb (09.07.2012)
     */
    public TmpAgileReviewSourceFolder(String name) {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}<br> <br> Will delete the temporary folder from test workspace
     * @see org.junit.rules.ExternalResource#after()
     * @author Malte Brunnlieb (09.07.2012)
     */
    @Override
    protected void after() {
        try {
            if (arSourceFolder != null && arSourceFolder.getProject() != null) {
                arSourceFolder.getProject().delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        super.after();
    }
    
    /**
     * Creates the project in the current test workspace
     * @throws CoreException if there occurs an eclipse internal problem while creating the project
     * @author Malte Brunnlieb (09.07.2012)
     */
    public void createProject() throws CoreException {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        
        if (!project.exists()) {
            project.create(new NullProgressMonitor());
        }
        while (!project.exists()) {
        }
        
        if (!project.isOpen()) {
            project.open(new NullProgressMonitor());
        }
        while (!project.isOpen()) {
        }
        
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { SourceFolderManager.AGILEREVIEW_ACTIVE_NATURE });
        project.setDescription(description, new NullProgressMonitor());
        
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
    }
    
    /**
     * Returns the temporary created java project instance
     * @return the created java project
     * @author Malte Brunnlieb (09.07.2012)
     */
    public IProject getProject() {
        return arSourceFolder;
    }
}
