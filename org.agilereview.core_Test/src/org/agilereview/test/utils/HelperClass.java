package org.agilereview.test.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class HelperClass {
	
	public static IFile getIFile(String path) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("org.agilereview.core_Test");
		if(project == null) throw new AssertionError("Project org.agilereview.core_Test could not be found!", null);
		return project.getFile("resources/Test.txt");
	}
}
