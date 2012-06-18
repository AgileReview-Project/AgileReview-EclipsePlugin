/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.test.common.storage.external.StorageClientMock;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for class {@link CommentSummaryView}
 * @author Malte Brunnlieb (26.05.2012)
 */
public class CommentSummaryViewTest {
    
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
    
    /**
     * UI-Test for SearchFilter functionality
     * @author Malte Brunnlieb (26.05.2012)
     */
    @Test
    public void testSearchFilter() {
        //test preparation
        Review reviewMock = mock(Review.class);
        IFile fileMock = mock(IFile.class);
        when(fileMock.getFullPath()).thenReturn(new Path(""));
        
        List<Comment> comments = new LinkedList<Comment>();
        final Comment c1 = new Comment("", "Adam", fileMock, reviewMock, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        comments.add(c1);
        Comment c2 = new Comment("", "Klaus", fileMock, reviewMock, Calendar.getInstance(), Calendar.getInstance(), "", 0, 0, "");
        comments.add(c2);
        when(reviewMock.getComments()).thenReturn(comments);
        when(reviewMock.getId()).thenReturn("");
        
        List<Review> reviews = new LinkedList<Review>();
        reviews.add(reviewMock);
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //perform UI test input
        SWTBotCombo filterType = bot.comboBoxWithId("csFilterType");
        filterType.setSelection(Column.AUTHOR.toString());
        SWTBotText searchText = bot.textWithId("csFilterText");
        searchText.pressShortcut(0, 'A');
        
        //evaluate results
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SWTBotTable table = bot.tableWithId("csTable");
                Assert.assertEquals(1, table.rowCount());
                Comment comment = (Comment) table.getTableItem(0).widget.getData();
                Assert.assertEquals(c1, comment);
            }
        });
    }
}
