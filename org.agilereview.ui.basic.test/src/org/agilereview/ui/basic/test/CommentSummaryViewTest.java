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

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for class {@link CommentSummaryView}
 * @author Malte Brunnlieb (26.05.2012)
 */
public class CommentSummaryViewTest {
    
    private final SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    /**
     * Setup method which resets the workbench and activates the AgileReview perspective
     * @author Malte Brunnlieb (26.05.2012)
     */
    @Before
    public void runBeforeEveryTest() {
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
    @Ignore("Still under development")
    @Test
    public void testSearchFilter() {
        
        SWTBotText searchText = bot.textWithId("testKey", "filterText");
        
        List<Comment> comments = new LinkedList<Comment>();
        Comment c1Mock = mock(Comment.class);
        comments.add(c1Mock);
        Comment c2Mock = mock(Comment.class);
        comments.add(c2Mock);
        
        Review reviewMock = mock(Review.class);
        when(reviewMock.getComments()).thenReturn(comments);
        List<Review> reviews = new LinkedList<Review>();
        
        cs.setReviewData(reviews);
        
    }
}
