package org.agilereview.core.test.external.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

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
	 * Time for tests to wait until modifications are made.
	 * Required as otherwise tests perfom to fast and changes of modification date do not occur.
	 */
	private static int SLEEPTIME = 1;

	/**
	 * Test method for {@link Comment#Comment(String, IFile, Review)}.
	 * @author Malte Brunnlieb (18.02.2012)
	 */
	@Test
	public final void testCommentStringIFileReview() {
		IFile file = HelperClass.getIFile("/org.agilereview.core.test/resources/Test.txt");
		String id = "c0";
		Review r = new Review("r0");
		
		// create comment
		Comment comment = new Comment(id, file, r);
		// id and file?)
		Assert.assertNotNull(comment);

		// check integrity
		if (!comment.getId().equals(id) || !comment.getCommentedFile().equals(file) || !comment.getReview().equals(r)) {
			throw new AssertionFailedError("Comment integrity not valid after creation!");
		}
	}

	/**
	 * Test method for
	 * {@link Comment#Comment(String, String, IFile, Review, Calendar, Calendar, String, int, int, String)}
	 * .
	 * @author Malte Brunnlieb (18.02.2012)
	 */
	@Test
	public final void testCommentStringStringIFileReviewDateDateStringIntIntString() {
		IFile file = HelperClass.getIFile("resources/Test.txt");
		String id = "c4";
		String author = "klaus";
		String recipient = "anyone";
		String text = "description";
		Calendar d = Calendar.getInstance();
		Review review = new Review("r2");
		
		// create comment
		Comment comment = new Comment(id, author, file, review, d, d, recipient, 1, 2, text);
		
		assertNotNull(comment);
		
		// check integrity
		if (!id.equals(comment.getId()) || !author.equals(comment.getAuthor()) || !file.equals(comment.getCommentedFile())
				|| !review.equals(comment.getReview()) || !d.equals(comment.getCreationDate()) || !d.equals(comment.getModificationDate())
				|| !recipient.equals(comment.getRecipient()) || comment.getStatus() != 1 || comment.getPriority() != 2
				|| !text.equals(comment.getText())) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("commentedFile");
		comment.addPropertyChangeListener(pcl);
		
		IFile file = HelperClass.getIFile("resources/Test2.txt");
		
		sleep();
		
		comment.setCommentedFile(file);
		
		if (!file.equals(comment.getCommentedFile()) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("CommentedFile could not be set successfully!");
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("recipient");
		comment.addPropertyChangeListener(pcl);
		
		sleep();
		
		comment.setRecipient("Theo");

		if (!"Theo".equals(comment.getRecipient()) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("status");
		comment.addPropertyChangeListener(pcl);
		
		comment.setStatus(5);

		if (!(comment.getStatus() == 5) || !pcl.getPropertyChanged()) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("priority");
		comment.addPropertyChangeListener(pcl);
		
		sleep();
		
		comment.setPriority(10);

		if (!(comment.getPriority() == 10) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("text");
		comment.addPropertyChangeListener(pcl);
		
		String text = "Dies \n is /*3´ß?)?^`^";
		
		sleep();
		
		comment.setText(text);

		if (!text.equals(comment.getText()) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Comment text could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getReplies()}.
	 * @author Malte Brunnlieb (18.02.2012)
	 */
	@Test
	public final void testGetReplies() {
		// is tested by the setReplies tests
	}
	
	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setReplies(java.util.List)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public final void testSetReplies() {
		Comment comment = createDefaultComment();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		comment.addPropertyChangeListener(pcl);
		
		Reply r1 = new Reply("id1", comment);
		Reply r2 = new Reply("id2", comment);
		Reply r3 = new Reply("id3", comment);
		ArrayList<Reply> replies = new ArrayList<Reply>();
		replies.add(r1);
		replies.add(r2);
		replies.add(r3);
	
		sleep();
		
		comment.setReplies(replies);
		
		if (!(replies.size() == comment.getReplies().size()) || !(comment.getReplies().contains(r1) && comment.getReplies().contains(r2) && comment.getReplies().contains(r3))) {
			throw new AssertionFailedError("Review comments could not be set successfully!");
		}

	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#addReply(org.agilereview.core.external.storage.Reply)} .
	 * @author Malte Brunnlieb (18.02.2012)
	 */
	@Test
	public final void testAddReply() {
		Comment comment = createDefaultComment();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		comment.addPropertyChangeListener(pcl);
		
		Reply r = new Reply("id", comment);
		int prevSize = comment.getReplies().size();
		
		sleep();
		
		comment.addReply(r);

		if (!(prevSize+1 == comment.getReplies().size()) || !comment.getReplies().get(comment.getReplies().size()-1).equals(r) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		comment.addPropertyChangeListener(pcl);
		
		Reply r1 = new Reply("id1", comment);
		Reply r2 = new Reply("id2", comment);
		Reply r3 = new Reply("id3", comment);
		Reply[] replies = {r1, r2, r3};
		comment.addReply(r1);
		comment.addReply(r2);
		comment.addReply(r3);
		
		int prevSize = comment.getReplies().size();
		
		int index = new Random().nextInt(3);
		
		sleep();
		
		comment.deleteReply(replies[index]);
		
		if (!(prevSize-1 == comment.getReplies().size()) || comment.getReplies().contains(replies[index]) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
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
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("replies");
		comment.addPropertyChangeListener(pcl);

		Reply r1 = new Reply("id1", comment);
		Reply r2 = new Reply("id2", comment);
		Reply r3 = new Reply("id3", comment);
		Reply[] replies = {r1, r2, r3};
		comment.addReply(r1);
		comment.addReply(r2);
		comment.addReply(r3);
		
		int prevSize = comment.getReplies().size();
		
		int index = new Random().nextInt(3);
		
		sleep();

		comment.deleteReply(comment.getReplies().size()-(replies.length-index));
		
		if (!(prevSize-1 == comment.getReplies().size()) || comment.getReplies().contains(replies[index]) || comment.getModificationDate().equals(comment.getCreationDate()) || !pcl.getPropertyChanged()) {
			throw new AssertionFailedError("Reply could not be removed by index successfully!");
		}
	}

	/**
	 * Test method for {@link Comment#addPropertyChangeListener(java.beans.PropertyChangeListener)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testAddPropertyChangeListener() {
		Comment comment = createDefaultComment();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("status");
		comment.addPropertyChangeListener(pcl);
		
		comment.addPropertyChangeListener(pcl);
		
		comment.setStatus(2);
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not added successfully to review!");
		}
	}

	/**
	 * Test method for {@link Comment#removePropertyChangeListener(java.beans.PropertyChangeListener)}. 
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testRemovePropertyChangeListener() {
		Comment comment = createDefaultComment();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("status");
		comment.addPropertyChangeListener(pcl);
		
		comment.removePropertyChangeListener(pcl);
		
		comment.setStatus(2);
		
		if (pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeListener was not removed successfully from review");
		}
	}

	/**
	 * Test method for {@link Comment#propertyChange(PropertyChangeEvent)}.
	 * @author Peter Reuter (03.06.2012)
	 */
	@Test
	public void testPropertyChange() {
		Comment review = createDefaultComment();
		HelperPropertyChangeListener pcl = new HelperPropertyChangeListener("test");
		review.addPropertyChangeListener(pcl);
		
		review.propertyChange(new PropertyChangeEvent(review, "test", false, true));
		
		if (!pcl.getPropertyChanged()) {
			throw new AssertionFailedError("PropertyChangeEvent not forwarded correctly!");
		}
	}

	/**
	 * Creates a {@link Comment} with values IFile = "" and id = ""
	 * @return {@link Comment} as specified above
	 * @author Malte Brunnlieb (18.02.2012)
	 */
	private Comment createDefaultComment() {
		IFile file = HelperClass.getIFile("/org.agilereview.core.test/resources/Test.txt");
		Review r = new Review("r0");

		Comment comment = new Comment("c0", file, r);
		
		assertEquals(comment.getCreationDate(), comment.getModificationDate());
		assertNotNull(comment);

		return comment;
	}

	/**
	 * Ask the test to sleep for a certain ammount of time.
	 * @author Peter Reuter (03.06.2012)
	 */
	private void sleep() {
		try {
			Thread.sleep(SLEEPTIME);
		} catch (InterruptedException e) {}
	}
	
}
