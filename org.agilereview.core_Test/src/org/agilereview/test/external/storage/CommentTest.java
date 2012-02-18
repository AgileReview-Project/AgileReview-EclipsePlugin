package org.agilereview.test.external.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.test.utils.HelperClass;
import org.eclipse.core.resources.IFile;
import org.junit.Test;

public class CommentTest {

	@Test
	public void testCommentStringIFile() {
		IFile file = HelperClass.getIFile("resources/Test.txt");
		
		//create comment
		Comment comment = new Comment("r2", file); //TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id and file?)
		Assert.assertNotNull(comment);
		
		//check integrity
		if(!comment.getId().equals("r2") || !comment.getCommentedFile().equals(file)) {
			throw new AssertionError("Comment integrity not valid after creation!");
		}
	}

	@Test
	public void testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply() {
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

	@Test
	public void testSetCommentedFile() {
		Comment comment = createDefaultComment();
		IFile file = HelperClass.getIFile("resources/Test2.txt");
		comment.setCommentedFile(file);
		
		if(!comment.getCommentedFile().equals(file)) {
			throw new AssertionError("CommentFile could not be set successfully!");
		}
	}

	@Test
	public void testSetRecipient() {
		Comment comment = createDefaultComment();
		comment.setRecipient("Theo");
		
		if(!comment.getRecipient().equals("Theo")) {
			throw new AssertionError("Recipient could not be set successfully!");
		}
	}

	@Test
	public void testSetStatus() {
		Comment comment = createDefaultComment();
		comment.setStatus(5);
		
		if(comment.getStatus() != 5) {
			throw new AssertionError("Status could not be set successfully!");
		}
	}

	@Test
	public void testSetPriority() {
		Comment comment = createDefaultComment();
		comment.setPriority(10);
		
		if(comment.getPriority() != 10) {
			throw new AssertionError("Priority could not be set successfully!");
		}
	}

	@Test
	public void testSetText() {
		Comment comment = createDefaultComment();
		comment.setText("Dies \n is §)?^´ßäöü");
		
		if(!comment.getText().equals("Dies \n is §)?^´ßäöü")) {
			throw new AssertionError("Comment text could not be set successfully!");
		}
	}

	@Test
	public void testAddReply() {
		Comment comment = createDefaultComment();
		
		//create reply
		Calendar c = Calendar.getInstance();
		Reply r = new Reply("Karl", c.getTime(), c.getTime(), "TestTestTest");
		comment.addReply(r);
		
		if(!comment.getReplies().get(0).equals(r)) {
			throw new AssertionError("Reply could not be added successfully!");
		}
	}

	@Test
	public void testDeleteReplyReply() {
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

	@Test
	public void testDeleteReplyInt() {
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

	private Comment createDefaultComment() {
		IFile file = HelperClass.getIFile("");
		
		//create comment
		Comment comment = new Comment("", file); //TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id and file?)
		Assert.assertNotNull(comment);
		
		return comment;
	}
}
