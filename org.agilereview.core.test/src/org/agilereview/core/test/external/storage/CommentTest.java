/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import java.util.ArrayList;
import java.util.Calendar;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.test.utils.HelperClass;
import org.eclipse.core.resources.IFile;
import org.junit.Test;

/**
 * TestClass for {@link org.agilereview.core.external.storage.Comment}
 * @author Malte Brunnlieb (18.02.2012)
 */
public class CommentTest {

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#Comment(java.lang.String, org.eclipse.core.resources.IFile)}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testCommentStringIFile() {
	IFile file = HelperClass.getIFile("resources/Test.txt");

	// create comment
	Comment comment = new Comment("r2", file); // TODO check the meaning of this "id" parameter (why is it possible to create a comment only with
						   // id and file?)
	Assert.assertNotNull(comment);

	// check integrity
	if (!comment.getId().equals("r2") || !comment.getCommentedFile().equals(file)) {
	    throw new AssertionFailedError("Comment integrity not valid after creation!");
	}
    }

    /**
     * Test method for
     * {@link org.agilereview.core.external.storage.Comment#Comment(java.lang.String, java.lang.String, org.eclipse.core.resources.IFile, org.agilereview.core.external.storage.Review, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String, java.util.ArrayList)}
     * .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply() {
	IFile file = HelperClass.getIFile("resources/Test.txt");

	Calendar d = Calendar.getInstance();
	Reply r = new Reply();
	ArrayList<Reply> replies = new ArrayList<Reply>();
	replies.add(r);
	Review review = new Review("r2");
	// create comment
	Comment comment = new Comment("c4", "klaus", file, review, d, d, "anyone", 1, 2, "description");
	comment.setReplies(replies);
	Assert.assertNotNull(comment);

	// check integrity
	if (!comment.getId().equals("c4") || !comment.getCommentedFile().equals(file) || !comment.getAuthor().equals("klaus")
		|| !comment.getReview().equals(review) || !comment.getCreationDate().equals(d) || !comment.getModificationDate().equals(d)
		|| !comment.getRecipient().equals("anyone") || comment.getStatus() != 1 || comment.getPriority() != 2
		|| !comment.getText().equals("description") || !comment.getReplies().get(0).equals(r)) {
	    throw new AssertionFailedError("Comment integrity not valid after creation!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getId()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetId() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getAuthor()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetAuthor() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getCommentedFile()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetCommentedFile() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#setCommentedFile(org.eclipse.core.resources.IFile)}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testSetCommentedFile() {
	Comment comment = createDefaultComment();
	IFile file = HelperClass.getIFile("resources/Test2.txt");
	comment.setCommentedFile(file);

	if (!comment.getCommentedFile().equals(file)) {
	    throw new AssertionFailedError("CommentFile could not be set successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getReview()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetReview() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getCreationDate()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetCreationDate() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getModificationDate()} .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetModificationDate() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getRecipient()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetRecipient() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#setRecipient(java.lang.String)} .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testSetRecipient() {
	Comment comment = createDefaultComment();
	comment.setRecipient("Theo");

	if (!comment.getRecipient().equals("Theo")) {
	    throw new AssertionFailedError("Recipient could not be set successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getStatus()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetStatus() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#setStatus(int)}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testSetStatus() {
	Comment comment = createDefaultComment();
	comment.setStatus(5);

	if (comment.getStatus() != 5) {
	    throw new AssertionFailedError("Status could not be set successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getPriority()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetPriority() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#setPriority(int)}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testSetPriority() {
	Comment comment = createDefaultComment();
	comment.setPriority(10);

	if (comment.getPriority() != 10) {
	    throw new AssertionFailedError("Priority could not be set successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getText()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetText() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#setText(java.lang.String)} .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testSetText() {
	Comment comment = createDefaultComment();
	comment.setText("Dies \n is /*3´ß?)?^`^");

	if (!comment.getText().equals("Dies \n is /*3´ß?)?^`^")) {
	    throw new AssertionFailedError("Comment text could not be set successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#getReplies()}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testGetReplies() {
	// is tested by the constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#addReply(org.agilereview.core.external.storage.Reply)} .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testAddReply() {
	Comment comment = createDefaultComment();

	// create reply
	Calendar c = Calendar.getInstance();
	Reply r = new Reply("id", "Karl", c, c, "TestTestTest", new Object());
	comment.addReply(r);

	if (!comment.getReplies().get(0).equals(r)) {
	    throw new AssertionFailedError("Reply could not be added successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#deleteReply(org.agilereview.core.external.storage.Reply)} .
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testDeleteReplyReply() {
	Comment comment = createDefaultComment();

	// create reply
	Calendar c = Calendar.getInstance();
	Reply r = new Reply("id", "Karl", c, c, "TestTestTest", new Object());
	comment.addReply(r);

	comment.deleteReply(r);
	if (comment.getReplies().size() != 0) {
	    throw new AssertionFailedError("Reply could not be removed by name successfully!");
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Comment#deleteReply(int)}.
     * @author Malte Brunnlieb (18.02.2012)
     */
    @Test
    public final void testDeleteReplyInt() {
	Comment comment = createDefaultComment();

	// create reply
	Calendar c = Calendar.getInstance();
	Reply r = new Reply("id", "Karl", c, c, "TestTestTest", new Object());
	comment.addReply(r);

	comment.deleteReply(0);
	if (comment.getReplies().size() != 0) {
	    throw new AssertionFailedError("Reply could not be removed by index successfully!");
	}
    }

    /**
     * Creates a {@link Comment} with values IFile = "" and id = ""
     * @return {@link Comment} as specified above
     * @author Malte Brunnlieb (18.02.2012)
     */
    private Comment createDefaultComment() {
	IFile file = HelperClass.getIFile("");

	// create comment
	Comment comment = new Comment("", file); // TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id
						 // and file?)
	Assert.assertNotNull(comment);

	return comment;
    }
}
