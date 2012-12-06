package org.agilereview.storage.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.conversion.PojoConversion;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.review.ReviewDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class PojoConversionTest {
	
	private static final String reviewText = "Test review";
	private static final String reviewReference = "http://www.google.de/";
	private static final String reviewId = "r1";
	private static final String reviewName = "TestReview";
	private static final String rtext = "Reply text";
	private static final String ctext = "Foobar Blubber....";
	private static final int status = 1;
	private static final int prio = 1;
	private static final String recipient = "Malte";
	private static final Calendar date = Calendar.getInstance();
	private static final String author = "Piet";
	private static final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/org.agilereview.storage.xml/src/org/agilereview/storage/xml/conversion/PojoConversion.java"));
	private static final String[] commentIds = { "c0", "c1" };
	private static final String[] replyIds = { "r0", reviewId};

	@Test
	public void testGetXmlBeansCommentsDocument() {
		Review r = new Review(reviewId);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for (int i=0; i<commentIds.length; i++) {
			Comment c = new Comment(commentIds[i], author, file, r, date, date, recipient, prio, status, ctext);
			comments.add(c);
			for (int j=0; j<replyIds.length; j++) {
				Reply rp = new Reply(replyIds[j], recipient, date, date, rtext, c);
				c.addReply(rp);
				for (int k=0; k<replyIds.length; k++) {
					Reply subrp = new Reply(replyIds[k], recipient, date, date, rtext, rp);
					rp.addReply(subrp);
				}
			}			
		}
		
		CommentsDocument cDoc = PojoConversion.getXmlBeansCommentsDocument(comments);
		
		for (int i=0; i<commentIds.length; i++) {
			org.agilereview.xmlSchema.author.CommentDocument.Comment xc = cDoc.getComments().getCommentArray()[i];
			assertEquals(commentIds[i], xc.getId());
			assertEquals(author, xc.getAuthorName());
			assertEquals(file.getFullPath().toPortableString(), xc.getResourcePath());
			assertEquals(reviewId, xc.getReviewID());
			assertEquals(0, date.compareTo(xc.getCreationDate()));
			// comment was modified, so last modified > creation date
			assertTrue(date.compareTo(xc.getLastModified()) <= 0);
			assertEquals(recipient, xc.getRecipient());
			assertEquals(prio, xc.getPriority());
			assertEquals(status, xc.getStatus());
			assertEquals(ctext, xc.getText());
			for (int j=0; j<replyIds.length; j++) {
				org.agilereview.xmlSchema.author.ReplyDocument.Reply xr = xc.getReplies().getReplyArray()[j];
				assertEquals(replyIds[j], xr.getId());
				assertEquals(recipient, xr.getAuthor());
				assertEquals(0, date.compareTo(xr.getCreationDate()));
				assertTrue(date.compareTo(xr.getLastModified()) <= 0);
				assertEquals(rtext, xr.getText());
				for (int k=0; k<replyIds.length; k++) {
					org.agilereview.xmlSchema.author.ReplyDocument.Reply subxr = xr.getReplies().getReplyArray()[k];
					assertEquals(replyIds[k], subxr.getId());
					assertEquals(recipient, subxr.getAuthor());
					assertEquals(0, date.compareTo(subxr.getCreationDate()));
					assertTrue(date.compareTo(subxr.getLastModified()) <= 0);
					assertEquals(rtext, subxr.getText());
				}	
			}
		}
	}
	
	@Test
	public void testGetXmlBeansReviewDocument() {
		Review r = new Review(reviewId, reviewName, status, reviewReference, recipient, reviewText);
		
		ReviewDocument rDoc = PojoConversion.getXmlBeansReviewDocument(r);
		
		org.agilereview.xmlSchema.review.ReviewDocument.Review xr = rDoc.getReview();
		
		assertEquals(reviewId, xr.getId());
		assertEquals(status, xr.getStatus());
		assertEquals(reviewReference, xr.getReferenceId());
		assertEquals(recipient, xr.getResponsibility());
		assertEquals(reviewText, xr.getDescription());
	}
	
}
