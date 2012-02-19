/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.AssertionFailedError;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.test.utils.HelperClass;
import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Test;

/**
 * TestClass for {@link org.agilereview.core.external.storage.Review}
 * @author Malte Brunnlieb (19.02.2012)
 */
public class ReviewTest {

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#Review(java.lang.String)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testReviewString() {
	Review review = new Review("testID");

	if (!"testID".equals(review.getId())) {
	    throw new AssertionFailedError("Review not be created consistently:\ntestID!=" + review.getId());
	}
    }

    /**
     * Test method for
     * {@link org.agilereview.core.external.storage.Review#Review(java.lang.String, int, java.net.URI, java.lang.String, java.util.ArrayList)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testReviewStringIntURIStringArrayListOfComment() {
	// generate data
	URI uri;
	try {
	    uri = new URI("http://mantis/blubber");
	} catch (URISyntaxException e) {
	    throw new AssertionFailedException("Failed on creating an URI for testing purpose");
	}
	ArrayList<Comment> comments = new ArrayList<Comment>();
	Comment c1 = new Comment("c1", HelperClass.getIFile(""));
	Comment c2 = new Comment("c2", HelperClass.getIFile(""));
	comments.add(c1);
	comments.add(c2);

	// check consistency
	Review review = new Review("testID", 2, uri, "Karl-Hänsél", comments);
	if (!"testID".equals(review.getId()) || review.getStatus() != 2 || !uri.equals(review.getReference())
		|| !"Karl-Hänsél".equals(review.getResponsibility()) || comments.size() != review.getComments().size()
		|| !c1.equals(review.getComments().get(0)) || !c2.equals(review.getComments().get(1))) {
	    throw new AssertionFailedError("Review not be created consistently:\ntestID==" + review.getId() + "\n2==" + review.getId() + "\n" + uri
		    + "==" + review.getReference() + "\nKarl-Hänsél==" + review.getResponsibility() + "\n" + comments.size() + "=="
		    + review.getComments().size() + "\n" + c1 + "==" + review.getComments().get(0) + "\n" + c2 + "==" + review.getComments().get(1));
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getId()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetId() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getStatus()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetStatus() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#setStatus(int)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testSetStatus() {
	Review review = new Review("");
	review.setStatus(9);

	if (review.getStatus() != 9) {
	    throw new AssertionFailedError("Review status could not be set successfully:\n9!=" + review.getStatus());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getReference()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetReference() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#setReference(java.net.URI)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testSetReference() {
	Review review = new Review("");
	URI uri;
	try {
	    uri = new URI("http://mantis/testURI");
	} catch (URISyntaxException e) {
	    throw new AssertionFailedException("Failed on creating an URI for testing purpose");
	}
	review.setReference(uri);

	if (!uri.equals(review.getReference())) {
	    throw new AssertionFailedError("Review reference could not be set successfully:\n" + uri + "!=" + review.getReference());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getResponsibility()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetResponsibility() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#setResponsibility(java.lang.String)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testSetResponsibility() {
	Review review = new Review("");
	review.setResponsibility("Käusî Méusli");

	if (!"Käusî Méusli".equals(review.getResponsibility())) {
	    throw new AssertionFailedError("Review responsibility could not be set successfully:\nKäusî Méusli!=" + review.getResponsibility());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getComments()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetComments() {
	// tested by constructor tests
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#addComment(org.agilereview.core.external.storage.Comment)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testAddComment() {
	Review review = new Review("");
	Comment c1 = new Comment("c0", HelperClass.getIFile("/TestFolder"));
	review.addComment(c1);

	if (review.getComments().size() != 1 || !c1.equals(review.getComments().get(0))) {
	    throw new AssertionFailedError("Review comment could not be added successfully:\n1==" + review.getComments().size() + "\n" + c1 + "=="
		    + review.getComments().get(0));
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(org.agilereview.core.external.storage.Comment)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testDeleteCommentComment() {
	Review review = new Review("");
	Comment c1 = new Comment("c0", HelperClass.getIFile("/TestFolder"));
	review.addComment(c1);
	review.deleteComment(c1);

	if (review.getComments().size() != 0) {
	    throw new AssertionFailedError("Review comment could not be removed by object successfully:\n0==" + review.getComments().size());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(int)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testDeleteCommentInt() {
	Review review = new Review("");
	Comment c1 = new Comment("c0", HelperClass.getIFile("/TestFolder"));
	review.addComment(c1);
	review.deleteComment(0);

	if (review.getComments().size() != 0) {
	    throw new AssertionFailedError("Review comment could not be removed by index successfully:\n0==" + review.getComments().size());
	}
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#getIsOpen()}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testGetIsOpen() {
	// tested by testSetIsOpen test
    }

    /**
     * Test method for {@link org.agilereview.core.external.storage.Review#setIsOpen(boolean)}.
     * @author Malte Brunnlieb (19.02.2012)
     */
    @Test
    public final void testSetIsOpen() {
	Review review = new Review("");
	review.setIsOpen(false);

	if (review.getIsOpen() != false) {
	    throw new AssertionFailedError("Review property isOpen could not be set successfully:\nfalse!=" + review.getIsOpen());
	}
    }
}
