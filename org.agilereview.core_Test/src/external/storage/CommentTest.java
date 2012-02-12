package external.storage;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Test;

public class CommentTest {

	@Test
	public void testCommentStringIFile() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("org.agilereview.core_Test");
		if(project == null) throw new AssertionError("Project org.agilereview.core_Test could not be found!", null);
		
		//generate comment contents
		IFile file = project.getFile("resources/Test.txt");
		//create comment
		Comment comment = new Comment("r2", file); //TODO check the meaning of this "id" parameter (why is it possible to create a comment only with id and file?)
		Assert.assertNotNull(comment);
		
		//check integrity
		if(!comment.getId().equals("r2") || !comment.getCommentedFile().equals(file)) {
			throw new AssertionError("Comment integrity not valid after creation!", null);
		}
	}

	@Test
	public void testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("org.agilereview.core_Test");
		if(project == null) throw new AssertionError("Project org.agilereview.core_Test could not be found!", null);
		
		//generate comment contents
		IFile file = project.getFile("resources/Test.txt");
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
			throw new AssertionError("Comment integrity not valid after creation!", null);
		}
	}

	@Test
	public void testGetId() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetAuthor() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetCommentedFile() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetCommentedFile() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetReview() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetCreationDate() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetModificationDate() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetRecipient() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetRecipient() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetStatus() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetStatus() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetPriority() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetPriority() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetText() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetText() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetReplies() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddReply() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDeleteReplyReply() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDeleteReplyInt() {
		fail("Not yet implemented"); // TODO
	}

}
