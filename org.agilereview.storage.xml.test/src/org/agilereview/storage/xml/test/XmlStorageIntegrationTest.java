/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.storage.xml.test;

import java.io.File;
import java.io.FileInputStream;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Peter Reuter (15.10.2012)
 */
public class XmlStorageIntegrationTest {
	
	private String projectName = "TestARProject";
	private TmpAgileReviewSourceFolder tmpAgileReviewSourceFolder = new TmpAgileReviewSourceFolder("");

	/**
	 * @throws java.lang.Exception
	 * @author Peter Reuter (15.10.2012)
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 * @author Peter Reuter (15.10.2012)
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 * @author Peter Reuter (15.10.2012)
	 */
	@Before
	public void setUp() throws Exception {
		tmpAgileReviewSourceFolder.createProject();
		File reviewFolder = new File("/org.agilereview.storage.xml.test/resources/review.r135");
		for (File f : reviewFolder.listFiles()) {
			IFile newFile = tmpAgileReviewSourceFolder.getProject().getFile(new Path(f.getName()));
            newFile.create(new FileInputStream(f), true, null);
		}
		InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, projectName);
	}

	/**
	 * @throws java.lang.Exception
	 * @author Peter Reuter (15.10.2012)
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
	}

}
