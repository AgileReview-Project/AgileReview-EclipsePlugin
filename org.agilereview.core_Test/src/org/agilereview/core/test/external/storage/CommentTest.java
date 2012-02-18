package org.agilereview.core.test.external.storage;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.test.utils.HelperClass;
import org.eclipse.core.resources.IFile;
import org.junit.Test;

/**
 * @author Malte
 *
 */
public class CommentTest {

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#Comment(java.lang.String, org.eclipse.core.resources.IFile)}.
	 */
	@Test
	public final void testCommentStringIFile() {
		IFile file = HelperClass.getIFile("resources/Test.txt");
		
		//create comment
		Comment comment = new Comment("r2", file); //TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id and file?)
		Assert.assertNotNull(comment);
		
		//check integrity
		if(!comment.getId().equals("r2") || !comment.getCommentedFile().equals(file)) {
			throw new AssertionError("Comment integrity not valid after creation!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#Comment(java.lang.String, java.lang.String, org.eclipse.core.resources.IFile, org.agilereview.core.external.storage.Review, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String, java.util.ArrayList)}.
	 */
	@Test
	public final void testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply() {
		IFile file = HelperClass.getIFile("resources/Test.txt");
		
		Date d = Calendar.getInstance().getTime();
		Reply r = new Reply();
		ArrayList<Reply> replies = new ArrayList<Reply>();
		replies.add(r);
		Review review = new Review("r2");
		//create comment
		Comment comment = new Comment("c4", "klaus", file, review, d, d, "anyone", 1, 2, "description", replies);
		Assert.assertNotNull(comment);
		
		//check integrity
		if(!comment.getId().equals("c4") || !comment.getCommentedFile().equals(file) || !comment.getAuthor().equals("klaus")
				|| !comment.getReview().equals(review) || !comment.getCreationDate().equals(d) || !comment.getModificationDate().equals(d)
				|| !comment.getRecipient().equals("anyone") || comment.getStatus() != 1 || comment.getPriority() != 2
				|| !comment.getText().equals("description") || !comment.getReplies().get(0).equals(r)) {
			throw new AssertionError("Comment integrity not valid after creation!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getId()}.
	 */
	@Test
	public final void testGetId() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getAuthor()}.
	 */
	@Test
	public final void testGetAuthor() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getCommentedFile()}.
	 */
	@Test
	public final void testGetCommentedFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setCommentedFile(org.eclipse.core.resources.IFile)}.
	 */
	@Test
	public final void testSetCommentedFile() {
		Comment comment = createDefaultComment();
		IFile file = HelperClass.getIFile("resources/Test2.txt");
		comment.setCommentedFile(file);
		
		if(!comment.getCommentedFile().equals(file)) {
			throw new AssertionError("CommentFile could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getReview()}.
	 */
	@Test
	public final void testGetReview() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getCreationDate()}.
	 */
	@Test
	public final void testGetCreationDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getModificationDate()}.
	 */
	@Test
	public final void testGetModificationDate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getRecipient()}.
	 */
	@Test
	public final void testGetRecipient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setRecipient(java.lang.String)}.
	 */
	@Test
	public final void testSetRecipient() {
		Comment comment = createDefaultComment();
		comment.setRecipient("Theo");
		
		if(!comment.getRecipient().equals("Theo")) {
			throw new AssertionError("Recipient could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getStatus()}.
	 */
	@Test
	public final void testGetStatus() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setStatus(int)}.
	 */
	@Test
	public final void testSetStatus() {
		Comment comment = createDefaultComment();
		comment.setStatus(5);
		
		if(comment.getStatus() != 5) {
			throw new AssertionError("Status could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getPriority()}.
	 */
	@Test
	public final void testGetPriority() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setPriority(int)}.
	 */
	@Test
	public final void testSetPriority() {
		Comment comment = createDefaultComment();
		comment.setPriority(10);
		
		if(comment.getPriority() != 10) {
			throw new AssertionError("Priority could not be set successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getText()}.
	 */
	@Test
	public final void testGetText() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#setText(java.lang.String)}.
	 */
	@Test
	public final void testSetText() {
		Comment comment = createDefaultComment();
		comment.setText("Dies \n is /*3´ß?)?^`^");
		
		if(!comment.getText().equals("Dies \n is /*3´ß?)?^`^")) {
			throw new AssertionError("Comment text could not be set successfully!");
		}
	}
	
	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#getReplies()}.
	 */
	@Test
	public final void testGetReplies() {
		// is tested by testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply()
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#addReply(org.agilereview.core.external.storage.Reply)}.
	 */
	@Test
	public final void testAddReply() {
		Comment comment = createDefaultComment();
		
		//create reply
		Calendar c = Calendar.getInstance();
		Reply r = new Reply("Karl", c.getTime(), c.getTime(), "TestTestTest");
		comment.addReply(r);
		
		if(!comment.getReplies().get(0).equals(r)) {
			throw new AssertionError("Reply could not be added successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#deleteReply(org.agilereview.core.external.storage.Reply)}.
	 */
	@Test
	public final void testDeleteReplyReply() {
		Comment comment = createDefaultComment();
		
		//create reply
		Calendar c = Calendar.getInstance();
		Reply r = new Reply("Karl", c.getTime(), c.getTime(), "TestTestTest");
		comment.addReply(r);
		
		comment.deleteReply(r);
		if(comment.getReplies().size() != 0) {
			throw new AssertionError("Reply could not be removed by name successfully!");
		}
	}

	/**
	 * Test method for {@link org.agilereview.core.external.storage.Comment#deleteReply(int)}.
	 */
	@Test
	public final void testDeleteReplyInt() {
		Comment comment = createDefaultComment();
		
		//create reply
		Calendar c = Calendar.getInstance();
		Reply r = new Reply("Karl", c.getTime(), c.getTime(), "TestTestTest");
		comment.addReply(r);
		
		comment.deleteReply(0);
		if(comment.getReplies().size() != 0) {
			throw new AssertionError("Reply could not be removed by index successfully!");
		}
	}

	/**
	 * Creates a {@link Comment} with values IFile = "" and id = ""
	 * @return {@link Comment} as specified above
	 */
	private Comment createDefaultComment() {
		IFile file = HelperClass.getIFile("");
		
		//create comment
		Comment comment = new Comment("", file); //TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id and file?)
		Assert.assertNotNull(comment);
		
		return comment;
	}
}
