/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.test.external.storage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
/**
 * 
 * @author Peter Reuter (03.06.2012)
 */
public class ReviewTest {
	
	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#Review(java.lang.String)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testReviewString() {
		Review review = new Review("testID");
		
		if (!"testID".equals(review.getId())) {
			throw new AssertionFailedError("Review not created consistently!");
		}
	}

	/**
	 * Test method for
	 * {@link org.agilereview.core.external.storage.Review#Review(String, int, String, String, String)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (03.06.2012)
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
		Review review = new Review("testID", 2, uri.toString(), "Karl-Hänsél", "test description");
		
		if (!"testID".equals(review.getId()) || review.getStatus() != 2 || !uri.toString().equals(review.getReference())
				|| !"Karl-Hänsél".equals(review.getResponsibility()) || !"test description".equals(review.getDescription())) {
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
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testSetStatus() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("status");
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
	 * @author Peter Reuter (03.06.2012)
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
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("reference");
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
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testSetResponsibility() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("responsibility");
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
		// tested by constructor tests
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
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testAddComment() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("comments");
		review.addPropertyChangeListener(pcl);

		int prevSize = review.getComments().size();
		Comment c1 = new Comment("c0", HelperClass.getIFile("resources/Test1.txt"), review);
		
		review.addComment(c1);

		if (!(prevSize+1 == review.getComments().size()) || !c1.equals(review.getComments().get(review.getComments().size()-1)) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be added successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(org.agilereview.core.external.storage.Comment)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testDeleteCommentComment() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("comments");
		review.addPropertyChangeListener(pcl);
		
		Comment c1 = new Comment("c0", HelperClass.getIFile("resources/Test1.txt"), review);
		review.addComment(c1);
		int prevSize = review.getComments().size();
		
		review.deleteComment(c1);

		if (!(prevSize-1 == review.getComments().size()) || review.getComments().contains(c1) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be removed by object successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Review#deleteComment(int)}.
	 * @author Malte Brunnlieb (19.02.2012)
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testDeleteCommentInt() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("comments");
		review.addPropertyChangeListener(pcl);
		
		Comment c1 = new Comment("c0", HelperClass.getIFile("resources/Test1.txt"), review);
		review.addComment(c1);
		int prevSize = review.getComments().size();
		
		review.deleteComment(review.getComments().size()-1);

		if (!(prevSize-1 == review.getComments().size()) || review.getComments().contains(c1) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review comment could not be removed by object successfully!");
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
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testSetIsOpen() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("isOpen");
		review.addPropertyChangeListener(pcl);
		
		review.setIsOpen(false);

		if (!(review.getIsOpen() == false) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Review property isOpen could not be set successfully!");
		}
	}
	
	/**
	 * Test method for {@link Review#addPropertyChangeListener(PropertyChangeListener)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testAddPropertyChangeListener() {
		Review review = new Review("");
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("isOpen");
		
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
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("isOpen");
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
		InternalPropertyChangeListener pcl = new InternalPropertyChangeListener();
		pcl.setPropertyName("test");
		review.addPropertyChangeListener(pcl);
		
		review.propertyChange(new PropertyChangeEvent(review, "test", false, true));
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeEvent not forwarded correctly!");
		}
	}
	
	/**
	 * Helper class to check for correct changes of properties. 
	 * @author Peter Reuter (03.06.2012)
	 */
	private class InternalPropertyChangeListener implements PropertyChangeListener {

		/**
		 * Indicates whether the property specified by propertyName was changed. 
		 */
		private boolean propertyChanged = false;
		/**
		 * Name of the property for which changes should occur. 
		 */
		private String propertyName = "";
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(propertyName)) {
				this.propertyChanged = true;
			}
		}
		
		/**
		 * @return A boolean value indicating whether the observed property was changed or not.
		 * @author Peter Reuter (03.06.2012)
		 */
		public boolean getPropertyChanged() {
			return propertyChanged;
		}
		
		/**
		 * @param propertyName The property to be observed for changes.
		 * @author Peter Reuter (03.06.2012)
		 */
		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}
	}

}
