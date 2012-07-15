/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.test.reviewExplorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.test.common.storage.external.StorageClientMock;
import org.agilereview.test.common.utils.TmpJavaProject;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for class {@link CommentSummaryView}
 * @author Malte Brunnlieb (26.05.2012)
 */
public class ReviewExplorerViewTest {

    /**
     * SWTBot instance for UI-Tests
     */
    private final SWTWorkbenchBot bot = new SWTWorkbenchBot();

    @Rule
    public TmpJavaProject tmpJavaProject = new TmpJavaProject("TestProject");

    /**
     * Setup method which will be executed before this test class will be executed at all
     * @author Malte Brunnlieb (27.05.2012)
     */
    @BeforeClass
    public static void setupTestClass() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", true);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", true); //TODO if client can be set via preferences -> use this
    }

    /**
     * Teardown method which will be executed after all tests were executed
     * @author Malte Brunnlieb (27.05.2012)
     */
    @AfterClass
    public static void tearDownTestClass() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", false);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", false); //TODO if client can be set via preferences -> use this
    }

    /**
     * Setup method which resets the workbench and activates the AgileReview perspective. Also the expected exception type will be reset to none.
     * @author Malte Brunnlieb (26.05.2012)
     */
    @Before
    public void setupTest() {
        bot.resetWorkbench();
        SWTBotPerspective perspective = bot.perspectiveById("org.agilereview.ui.basic.perspective");
        if (!perspective.isActive()) {
            perspective.activate();
        }
    }

    @After
    public void tearDownTest() {
        try {
            ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * UI-Test for SearchFilter functionality
     * @author Malte Brunnlieb (26.05.2012)
     * @throws CoreException
     */
    @Test
    public void testViewGeneral() throws CoreException {
        // test data        
        Review reviewMock1 = mock(Review.class); // new Review("r1");
        Review reviewMock2 = mock(Review.class); // new Review("r2");
        tmpJavaProject.createProject();
        IJavaProject test1Tmp = tmpJavaProject.getProject();
        //        IProject p1 = ResourcesPlugin.getWorkspace().getRoot().getProject("p1");
        //        if (!p1.exists()) {
        //            p1.create(new NullProgressMonitor());
        //            p1.open(new NullProgressMonitor());
        //        }

        // First folder with file
        IFolder folder1 = test1Tmp.getProject().getFolder("src");
        if (!folder1.exists()) {
            folder1.create(true, true, new NullProgressMonitor());
        }
        IFile file1 = folder1.getFile("AnswerToEverything.java");
        if (!file1.exists()) {
            file1.create(new ByteArrayInputStream("test 123".getBytes()), true, new NullProgressMonitor());
        }

        // Second folder with file
        IFolder folder2 = test1Tmp.getProject().getFolder("test");
        if (!folder2.exists()) {
            folder2.create(true, true, new NullProgressMonitor());
        }
        IFile file2 = folder2.getFile("Test.java");
        if (!file2.exists()) {
            file2.create(new ByteArrayInputStream("test 123".getBytes()), true, new NullProgressMonitor());
        }

        List<Comment> comments1 = new LinkedList<Comment>();
        List<Comment> comments2 = new LinkedList<Comment>();
        Comment c1 = new Comment("c1", "Adam", file1, reviewMock1, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        comments1.add(c1);
        Comment c2 = new Comment("c2", "Klaus", file2, reviewMock2, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        comments2.add(c2);

        when(reviewMock1.getComments()).thenReturn(comments1);
        when(reviewMock1.getId()).thenReturn("r1");
        when(reviewMock1.getIsOpen()).thenReturn(true);

        when(reviewMock2.getComments()).thenReturn(comments2);
        when(reviewMock2.getId()).thenReturn("r2");
        when(reviewMock2.getIsOpen()).thenReturn(true);

        ReviewSet reviews = new ReviewSet();
        reviews.add(reviewMock1);
        reviews.add(reviewMock2);

        StorageClientMock.getInstance().setStorageContent(reviews);
        try {
            Thread.sleep(90 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //        StorageClientMock.getInstance().addReview(reviewMock2);
        //        try {
        //            Thread.sleep(90 * 1000);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //        waitForJobs(30000);
    }

    /**
     * Wait until all background tasks are complete.
     */
    private void waitForJobs(int timeInMillies) {
        while (Job.getJobManager().currentJob() != null)
            delay(timeInMillies);
    }

    /**
     * Process UI input but do not return for the specified time interval.
     * @param waitTimeMillis the number of milliseconds
     */
    private void delay(long waitTimeMillis) {
        Display display = Display.getCurrent();

        // If this is the UI thread,
        // then process input.
        if (display != null) {
            long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
            while (System.currentTimeMillis() < endTimeMillis) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
            display.update();
        }
        // Otherwise, perform a simple sleep.
        else {
            try {
                Thread.sleep(waitTimeMillis);
            } catch (InterruptedException e) {
                // Ignored.
            }
        }
    }
}
