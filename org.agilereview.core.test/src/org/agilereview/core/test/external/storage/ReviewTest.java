/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import junit.framework.AssertionFailedError;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.test.utils.HelperClass;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
		
		assertNotNull(review);
		
		if (!"testID".equals(review.getId())) {
			throw new AssertionFailedError("Review not created consistently!");
		}
	}

	/**
	 * Test method for
	 * {@link org.agilereview.core.external.storage.Review#Review(String, int, String, String, String, boolean)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	@Test
	public final void testReviewStringIntStringStringString() {
		// generate data
		URI uri;
		try {
			uri = new URI("http://mantis/blubber");
		} catch (URISyntaxException e) {
			throw new AssertionFailedException("Failed on creating an URI for testing purpose");
		}

		// check consistency
		Review review = new Review("testID", 2, uri.toString(), "Karl-Hänsél", "test description", true);
		
		assertNotNull(review);
		
		if (!"testID".equals(review.getId()) || review.getStatus() != 2 || !uri.toString().equals(review.getReference())
				|| !"Karl-Hänsél".equals(review.getResponsibility()) || !"test description".equals(review.getDescription()) || !review.getIsOpen()) {
			throw new AssertionFailedError("Review not created consistently!");
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("status");
		review.addPropertyChangeListener(pcl);
		
		review.setStatus(9);

		if (review.getStatus() != 9 || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review status could not be set successfully!");
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
	 * Test method for {@link org.agilereview.core.external.storage.Review#setReference(String)}.
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("reference");
		review.addPropertyChangeListener(pcl);
		
		review.setReference(uri.toString());

		if (!uri.toString().equals(review.getReference()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review reference could not be set successfully!");
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("responsibility");
		review.addPropertyChangeListener(pcl);
		
		review.setResponsibility("Käusî Méusli");

		if (!"Käusî Méusli".equals(review.getResponsibility()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review responsibility could not be set successfully! " + review.getResponsibility() + pcl.getPropertyChanged());
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#getComments()}.
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	@Test
	public final void testGetComments() {
		// tested by setComments test
	}
	
	
	/**
	 * Test method for {@link Review#setComments(java.util.List)} 
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testSetComments() {
		Review review = new Review("");
		Comment c1 = new Comment("c0", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c2 = new Comment("c1", HelperClass.getIFile("resources/Test1.txt"), review);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		comments.add(c1);
		comments.add(c2);
		
		review.setComments(comments);
		
		if (!(comments.size() == review.getComments().size()) || !(review.getComments().contains(c1) && review.getComments().contains(c2))) {
			throw new AssertionFailedError("Review comments could not be set successfully!");
		}
		
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#addComment(org.agilereview.core.external.storage.Comment)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	@Test
	public final void testAddComment() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("comments");
		review.addPropertyChangeListener(pcl);

		int prevSize = review.getComments().size();
		Comment c1 = new Comment("c0", HelperClass.getIFile("resources/Test1.txt"), review);
		
		review.addComment(c1);

		if (!(review.getComments().contains(c1)) || !(prevSize+1 == review.getComments().size()) || !c1.equals(review.getComments().get(review.getComments().size()-1)) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be added successfully!");
		}
		
		review.addComment(c1);
		if (review.getComments().size() > prevSize+1) {
			throw new AssertionFailedError("Review comment added twice!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(org.agilereview.core.external.storage.Comment)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	@Test
	public final void testDeleteCommentComment() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("comments");
		review.addPropertyChangeListener(pcl);
		
		Comment c1 = new Comment("c1", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c2 = new Comment("c2", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c3 = new Comment("c3", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment[] comments = {c1, c2, c3};
		review.addComment(c1);
		review.addComment(c2);
		review.addComment(c3);
		int prevSize = review.getComments().size();
		
		int index = new Random().nextInt(3);
		
		review.deleteComment(comments[index]);

		if (!(prevSize-1 == review.getComments().size()) || review.getComments().contains(comments[index]) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be removed by object successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(int)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 */
	@Test
	public final void testDeleteCommentInt() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("comments");
		review.addPropertyChangeListener(pcl);
		
		review.addPropertyChangeListener(pcl);
		
		Comment c1 = new Comment("c1", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c2 = new Comment("c2", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c3 = new Comment("c3", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment[] comments = {c1, c2, c3, c1};
		review.addComment(c1);
		review.addComment(c1);
		review.addComment(c2);
		review.addComment(c3);
		int prevSize = review.getComments().size();
		
		int index = new Random().nextInt(3);
		
		review.deleteComment(review.getComments().size()-(comments.length-1-index));

		if (!(prevSize-1 == review.getComments().size()) || review.getComments().contains(comments[index]) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be removed by index successfully!");
		}
	}
	
	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#clearComments()}.
	 * @author Peter Reuter (26.06.2012)
	 */
	public final void testClearComments() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("comments");
		review.addPropertyChangeListener(pcl);
		
		review.addPropertyChangeListener(pcl);
		
		Comment c1 = new Comment("c1", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c2 = new Comment("c2", HelperClass.getIFile("resources/Test1.txt"), review);
		Comment c3 = new Comment("c3", HelperClass.getIFile("resources/Test1.txt"), review);
		review.addComment(c1);
		review.addComment(c2);
		review.addComment(c3);
		
		review.clearComments();

		if (!(0 == review.getComments().size()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comments could not be cleared successfully!");
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
		//TODO check also for correct "open_reviews" property
		String id = "TestReviewSetIsOpen";
		Review review = new Review(id);
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("isOpen");
		review.addPropertyChangeListener(pcl);
		
		String openReviewIds = InstanceScope.INSTANCE.getNode("org.agilereview.core").get(AgileReviewPreferences.OPEN_REVIEWS, id);
		assertTrue(openReviewIds.contains(id));
		
		review.setIsOpen(false);

		if (!(review.getIsOpen() == false) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review property isOpen could not be set successfully!");
		}
		
		openReviewIds = InstanceScope.INSTANCE.getNode("org.agilereview.core").get(AgileReviewPreferences.OPEN_REVIEWS, id);
		assertFalse(openReviewIds.contains(id));
	}
	
	/**
	 * Test method for {@link Review#addPropertyChangeListener(PropertyChangeListener)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testAddPropertyChangeListener() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("isOpen");
		
		review.addPropertyChangeListener(pcl);
		
		review.setIsOpen(false);
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not added successfully to review!");
		}
	}

	/**
	 * Test method for {@link Review#removePropertyChangeListener(PropertyChangeListener)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testRemovePropertyChangeListener() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("isOpen");
		review.addPropertyChangeListener(pcl);
		
		review.removePropertyChangeListener(pcl);
		
		review.setIsOpen(false);
		
		if (pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not removed successfully from review");
		}
	}

	/**
	 * Test method for {@link Review#propertyChange(PropertyChangeEvent)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testPropertyChange() {
		Review review = new Review("");
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("test");
		review.addPropertyChangeListener(pcl);
		
		review.propertyChange(new PropertyChangeEvent(review, "test", false, true));
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeEvent not forwarded correctly!");
		}
	}

}
