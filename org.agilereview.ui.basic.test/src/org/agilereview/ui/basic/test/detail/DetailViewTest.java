/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.test.detail;

import java.util.Calendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.core.external.storage.StorageAPI;
import org.agilereview.test.common.storage.external.StorageClientMock;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for class {@link CommentSummaryView}
 * @author Malte Brunnlieb (26.05.2012)
 */
public class DetailViewTest {
    
    /**
     * SWTBot instance for UI-Tests
     */
    private final SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    /**
     * Setup method which will be executed before this test class will be executed at all
     * @author Malte Brunnlieb (27.05.2012)
     */
    @BeforeClass
    public static void setupTestClass() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", true);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", false); //TODO if client can be set via preferences -> use this
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
        SWTBotPerspective perspective = bot.perspectiveById("org.agilereview.perspective");
        if (!perspective.isActive()) {
            perspective.activate();
        }
    }
    
    /**
     * UI-Test for DetailView
     * @author Malte Brunnlieb (26.05.2012)
     */
    @Test
    public void Test_Show() {
        //test preparation
        Review review = mock(Review.class);
        when(review.getId()).thenReturn("1");
        
        IFile fileMock = mock(IFile.class);
        when(fileMock.getFullPath()).thenReturn(new Path("some filepath"));
        
        final Comment c1 = StorageAPI.createComment("", "Adam", fileMock, review, Calendar.getInstance(), Calendar.getInstance(), "rec 1", 1, 0,
                "some text 1");
        final Comment c2 = StorageAPI.createComment("", "Klaus", fileMock, review, Calendar.getInstance(), Calendar.getInstance(), "rec 2", 0, 1,
                "some text 2");
        c1.addReply(new Reply("reply1", "Eva", Calendar.getInstance(), Calendar.getInstance(), "This is my reply", c1));
        review.addComment(c1);
        review.addComment(c2);
        
        ReviewSet reviews = new ReviewSet();
        reviews.add(review);
        Review r2 = mock(Review.class);
        when(r2.getId()).thenReturn("2");
        reviews.add(r2);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        reviews.storeValue("detail", c1);
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        reviews.storeValue("detail", c2);
        
        //perform UI test input (author)
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
