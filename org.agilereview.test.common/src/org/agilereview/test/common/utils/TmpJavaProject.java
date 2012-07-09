/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.test.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.junit.rules.ExternalResource;

/**
 * JUnit Rule for a temporary {@link IJavaProject}. Should be created in each test method by createProject when it should be used.
 * @author Malte Brunnlieb (09.07.2012)
 */
public class TmpJavaProject extends ExternalResource {
    
    /**
     * Name of the Project
     */
    private final String name;
    /**
     * Project reference
     */
    private IJavaProject javaProject;
    
    /**
     * Creates a new object instance, but does not create the project in the test workspace. This should be done manually by createProject()
     * @param name the project should be named with
     * @author Malte Brunnlieb (09.07.2012)
     */
    public TmpJavaProject(String name) {
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
            if (javaProject != null && javaProject.getProject() != null) {
                javaProject.getProject().delete(true, new NullProgressMonitor());
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
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, new NullProgressMonitor());
        
        javaProject = JavaCore.create(project);
        createBinFolder();
        defineClassPathEntries();
        createSourceFolder();
        
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
    }
    
    /**
     * Creates the bin folder for a valid java project instance
     * @throws CoreException if an internal exception occurs while creating the bin folder
     * @throws JavaModelException
     * @author Malte Brunnlieb (09.07.2012)
     */
    private void createBinFolder() throws CoreException, JavaModelException {
        IFolder binFolder = javaProject.getProject().getFolder("bin");
        binFolder.create(true, true, new NullProgressMonitor());
        javaProject.setOutputLocation(binFolder.getFullPath(), new NullProgressMonitor());
    }
    
    /**
     * Defines the class path entries for a valid java project
     * @throws JavaModelException if an error occurs while setting the classpath
     * @author Malte Brunnlieb (09.07.2012)
     */
    private void defineClassPathEntries() throws JavaModelException {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
        LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
        for (LibraryLocation element : locations) {
            entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
        }
        //add libs to project class path
        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), new NullProgressMonitor());
    }
    
    /**
     * Creates the source folder for a valid java project
     * @throws CoreException if an error occurs while creating the source folder
     * @author Malte Brunnlieb (09.07.2012)
     */
    private void createSourceFolder() throws CoreException {
        IFolder sourceFolder = javaProject.getProject().getFolder("src");
        sourceFolder.create(true, true, new NullProgressMonitor());
        
        IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
        javaProject.setRawClasspath(newEntries, new NullProgressMonitor());
    }
    
    /**
     * Returns the temporary created java project instance
     * @return the created java project
     * @author Malte Brunnlieb (09.07.2012)
     */
    public IJavaProject getProject() {
        return javaProject;
    }
}
