/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import junit.framework.AssertionFailedError;

import org.agilereview.core.external.storage.Reply;
import org.junit.Test;

/**
 * 
 * @author Peter Reuter (04.06.2012)
 */
public class ReplyTest {
	
	/**
	 * Time for tests to wait until modifications are made.
	 * Required as otherwise tests perfom to fast and changes of modification date do not occur.
	 */
	private static int SLEEPTIME = 1;

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#Reply(java.lang.String, java.lang.String, java.util.Calendar, java.util.Calendar, java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testReplyStringStringCalendarCalendarStringObject() {
		String id = "r1";
		String author = "Hans Olo";
		Calendar creationDate = Calendar.getInstance();
		Calendar modificationDate = creationDate;
		String text = "Never walk Solo, always take a Wookie with you!";
		Object parent = new Object();
		
		Reply r = new Reply(id, author, creationDate, modificationDate, text, parent);
		if (!id.equals(r.getId()) || !author.equals(r.getAuthor()) || !creationDate.equals(r.getCreationDate()) || !modificationDate.equals(r.getModificationDate()) || !text.equals(r.getText()) || !parent.equals(r.getParent())) {
			throw new AssertionFailedError("Reply integrity not valid after creation!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#Reply(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testReplyStringObject() {
		String id = "r1";
		Object parent = new Object();
		
		Reply r = new Reply(id, parent);
		if (!id.equals(r.getId()) || !parent.equals(r.getParent())) {
			throw new AssertionFailedError("Reply integrity not valid after creation!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getId()}.
	 */
	@Test
	public void testGetId() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getAuthor()}.
	 */
	@Test
	public void testGetAuthor() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getCreationDate()}.
	 */
	@Test
	public void testGetCreationDate() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getModificationDate()}.
	 */
	@Test
	public void testGetModificationDate() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getText()}.
	 */
	@Test
	public void testGetText() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#setText(java.lang.String)}.
	 */
	@Test
	public void testSetText() {
		Reply r = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("text");
		r.addPropertyChangeListener(pcl);
		
		sleep();
		
		String text = "05/04 will be with you!";
		
		r.setText(text);
		
		if (!text.equals(r.getText()) || r.getModificationDate().equals(r.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Text could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getReplies()}.
	 */
	@Test
	public void testGetReplies() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#setReplies(java.util.List)}.
	 */
	@Test
	public void testSetReplies() {
		Reply r = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		r.addPropertyChangeListener(pcl);
		
		Reply r1 = new Reply("id1", r);
		Reply r2 = new Reply("id2", r);
		Reply r3 = new Reply("id3", r);
		ArrayList<Reply> replies = new ArrayList<Reply>();
		replies.add(r1);
		replies.add(r2);
		replies.add(r3);
	
		sleep();
		
		r.setReplies(replies);
		
		if (!(replies.size() == r.getReplies().size()) || !(r.getReplies().contains(r1) && r.getReplies().contains(r2) && r.getReplies().contains(r3))) {
			throw new AssertionFailedError("Replies could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#addReply(org.agilereview.core.external.storage.Reply)}.
	 */
	@Test
	public void testAddReply() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		reply.addPropertyChangeListener(pcl);
		
		Reply r = new Reply("id", reply);
		int prevSize = reply.getReplies().size();
		
		sleep();
		
		reply.addReply(r);

		if (!(reply.getReplies().contains(r)) || !(prevSize+1 == reply.getReplies().size()) || !reply.getReplies().get(reply.getReplies().size()-1).equals(r) || reply.getModificationDate().equals(reply.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Reply could not be added successfully!");
		}
		
		reply.addReply(r);
		if (reply.getReplies().size() > prevSize+1) {
			throw new AssertionFailedError("Reply added twice!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#deleteReply(org.agilereview.core.external.storage.Reply)}.
	 */
	@Test
	public void testDeleteReplyReply() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		reply.addPropertyChangeListener(pcl);
		
		Reply r1 = new Reply("id1", reply);
		Reply r2 = new Reply("id2", reply);
		Reply r3 = new Reply("id3", reply);
		Reply[] replies = {r1, r1, r2, r3};
		reply.addReply(r1);
		reply.addReply(r1);
		reply.addReply(r2);
		reply.addReply(r3);
		
		int prevSize = reply.getReplies().size();
		
		int index = new Random().nextInt(3);
		
		sleep();
		
		reply.deleteReply(replies[index]);
		
		if (!(prevSize-1 == reply.getReplies().size()) || reply.getReplies().contains(replies[index]) || reply.getModificationDate().equals(reply.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Reply could not be removed by name successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#deleteReply(int)}.
	 */
	@Test
	public void testDeleteReplyInt() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		reply.addPropertyChangeListener(pcl);

		Reply r1 = new Reply("id1", reply);
		Reply r2 = new Reply("id2", reply);
		Reply r3 = new Reply("id3", reply);
		Reply[] replies = {r1, r2, r3, r1};
		reply.addReply(r1);
		reply.addReply(r1);
		reply.addReply(r2);
		reply.addReply(r3);
		
		int prevSize = reply.getReplies().size();
		
		int index = new Random().nextInt(3);
		
		sleep();

		reply.deleteReply(reply.getReplies().size()-(replies.length-1-index));
		
		if (!(prevSize-1 == reply.getReplies().size()) || reply.getReplies().contains(replies[index]) || reply.getModificationDate().equals(reply.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Reply could not be removed by index successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#getParent()}.
	 */
	@Test
	public void testGetParent() {
		// is tested by the constructor tests
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#addPropertyChangeListener(java.beans.PropertyChangeListener)}.
	 */
	@Test
	public void testAddPropertyChangeListener() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("text");
		reply.addPropertyChangeListener(pcl);
		
		reply.addPropertyChangeListener(pcl);
		
		reply.setText("Foobar!");
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not added successfully to review!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#removePropertyChangeListener(java.beans.PropertyChangeListener)}.
	 */
	@Test
	public void testRemovePropertyChangeListener() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("text");
		reply.addPropertyChangeListener(pcl);
		
		reply.removePropertyChangeListener(pcl);
		
		reply.setText("Foobar!");
		
		if (pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not removed successfully from review");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Reply#propertyChange(java.beans.PropertyChangeEvent)}.
	 */
	@Test
	public void testPropertyChange() {
		Reply reply = createDefaultReply();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("test");
		reply.addPropertyChangeListener(pcl);
		
		reply.propertyChange(new PropertyChangeEvent(reply, "test", false, true));
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeEvent not forwarded correctly!");
		}
	}
	
	/**
	 * Creates a {@link Reply} with values id = "r1" and parent as new Object.
	 * @return {@link Reply} as specified above
	 * @author Peter Reuter (20.06.2012)
	 */
	private Reply createDefaultReply() {
		Reply r = new Reply("r1", new Object());
		assertEquals(r.getCreationDate(), r.getModificationDate());
		assertNotNull(r);
		return r;
	}


	/**
	 * Ask the test to sleep for a certain ammount of time.
	 * @author Peter Reuter (20.06.2012)
	 */
	private void sleep() {
		try {
			Thread.sleep(SLEEPTIME);
		} catch (InterruptedException e) {}
	}
}