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

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.test.common.storage.external.StorageClientMock;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
     * Expected Exception Type
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    /**
     * Setup method which will be executed before this test class will be executed at all
     * @author Malte Brunnlieb (27.05.2012)
     */
    @BeforeClass
    public static void setupTests() {
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.active", true);
        PlatformUI.getPreferenceStore().setValue("org.agilereview.testrunner.mock.storage", true); //TODO if client can be set via preferences -> use this
    }
    
    /**
     * Teardown method which will be executed after all tests were executed
     * @author Malte Brunnlieb (27.05.2012)
     */
    @AfterClass
    public static void tearDownTests() {
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
        exception = ExpectedException.none();
    }
    
    /**
     * UI-Test for SearchFilter functionality
     * @author Malte Brunnlieb (26.05.2012)
     */
    @Test
    public void testSearchFilter() {
        //test preparation
        List<Comment> comments = new LinkedList<Comment>();
        Comment c1Mock = mock(Comment.class);
        when(c1Mock.getAuthor()).thenReturn("Adam");
        comments.add(c1Mock);
        Comment c2Mock = mock(Comment.class);
        when(c2Mock.getAuthor()).thenReturn("Klaus");
        comments.add(c2Mock);
        
        Review reviewMock = mock(Review.class);
        when(reviewMock.getComments()).thenReturn(comments);
        List<Review> reviews = new LinkedList<Review>();
        
        StorageClientMock.getInstance().setStorageContent(reviews);
        
        //SWTBotView csView = bot.viewById("org.agilereview.ui.basic.commentSummaryView");
        //((CommentSummaryView) csView.getReference().getPart(false)).setReviewData(reviews);
        
        //        try {
        //            Thread.sleep(5000);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        
        //perform UI test input
        SWTBotCombo filterType = bot.comboBoxWithId("csFilterType");
        filterType.setSelection(Column.AUTHOR.toString());
        SWTBotText searchText = bot.textWithId("csFilterText");
        searchText.pressShortcut(0, 'A');
        
        //        try {
        //            Thread.sleep(5000);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        
        //evaluate results
        SWTBotTable table = bot.tableWithId("csTable");
        TableItem item = table.getTableItem(0).widget;
        Assert.assertEquals(c1Mock, item);
        
        exception.expect(WidgetNotFoundException.class);
        item = table.getTableItem(1).widget;
    }
}
