package org.agilereview.storage.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.conversion.PojoConversion;
import org.agilereview.storage.xml.conversion.XmlBeansConversion;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.review.ReviewDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class XmlBeansConversionTest {
	
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
	public void testGetReview() {
		Review r = new Review(reviewId, reviewName, status, reviewReference, recipient, reviewText);
		
		ReviewDocument xr = PojoConversion.getXmlBeansReviewDocument(r);
		
		Review rConverted = XmlBeansConversion.getReview(xr.getReview());
		
		assertEquals(reviewId, rConverted.getId());
		assertEquals(status, rConverted.getStatus());
		assertEquals(reviewReference, rConverted.getReference());
		assertEquals(recipient, rConverted.getResponsibility());
		assertEquals(reviewText, rConverted.getDescription());
	}

	@Test
	public void testGetComment() {
		Review r = new Review(reviewId);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Comment c = new Comment(commentIds[0], author, file, r, date, date, recipient, prio, status, ctext);
		comments.add(c);
		
		CommentsDocument cDoc = PojoConversion.getXmlBeansCommentsDocument(comments);
		
		Comment cConverted = XmlBeansConversion.getComment(r, cDoc.getComments().getCommentArray()[0]);
		
		assertEquals(commentIds[0], cConverted.getId());
		assertEquals(author, cConverted.getAuthor());
		assertEquals(file, cConverted.getCommentedFile());
		assertEquals(r, cConverted.getReview());
		assertEquals(0, date.compareTo(cConverted.getCreationDate()));
		assertEquals(0, date.compareTo(cConverted.getModificationDate()));
		assertEquals(recipient, cConverted.getRecipient());
		assertEquals(prio, cConverted.getPriority());
		assertEquals(status, cConverted.getStatus());
		assertEquals(ctext, cConverted.getText());
	}

	@Test
	public void testGetReplyList() {
		Review r = new Review(reviewId);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Comment c = new Comment(commentIds[0], author, file, r, date, date, recipient, prio, status, ctext);
		comments.add(c);
		for (int j=0; j<replyIds.length; j++) {
			Reply rp = new Reply(replyIds[j], recipient, date, date, rtext, c);
			c.addReply(rp);
			for (int k=0; k<replyIds.length; k++) {
				Reply subrp = new Reply(replyIds[k], recipient, date, date, rtext, rp);
				rp.addReply(subrp);
			}
		}			
		
		CommentsDocument cDoc = PojoConversion.getXmlBeansCommentsDocument(comments);
		
		ArrayList<Reply> rsConverted = XmlBeansConversion.getReplyList(c, cDoc.getComments().getCommentArray()[0].getReplies().getReplyArray());
		
		for (int j=0; j<replyIds.length; j++) {
			Reply rConverted = rsConverted.get(j);
			assertEquals(replyIds[j], rConverted.getId());
			assertEquals(recipient, rConverted.getAuthor());
			assertEquals(0, date.compareTo(rConverted.getCreationDate()));
			assertTrue(date.compareTo(rConverted.getModificationDate()) <= 0);
			assertEquals(rtext, rConverted.getText());
			for (int k=0; k<replyIds.length; k++) {
				Reply subrConverted = rConverted.getReplies().get(k);
				assertEquals(replyIds[k], subrConverted.getId());
				assertEquals(recipient, subrConverted.getAuthor());
				assertEquals(0, date.compareTo(subrConverted.getCreationDate()));
				assertTrue(date.compareTo(subrConverted.getModificationDate()) <= 0);
				assertEquals(rtext, subrConverted.getText());
			}	
		}
	}

}
