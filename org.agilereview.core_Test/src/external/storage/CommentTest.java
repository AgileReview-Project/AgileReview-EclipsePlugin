package external.storage;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.agilereview.core.external.storage.Comment;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Test;

public class CommentTest {

	@Test
	public void testCommentStringIFile() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("org.agilereview.core_Test");
		if(project != null) {
			IFile file = project.getFile("resources/Test.txt");
			Assert.assertNotNull(new Comment("r2.klaus.c4", file));
		} else {
			throw new AssertionError("Project org.agilereview.core_Test could not be found", null);
		}
	}

	@Test
	public void testCommentStringStringIFileReviewDateDateStringIntIntStringArrayListOfReply() {
		fail("Not yet implemented"); // TODO
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
