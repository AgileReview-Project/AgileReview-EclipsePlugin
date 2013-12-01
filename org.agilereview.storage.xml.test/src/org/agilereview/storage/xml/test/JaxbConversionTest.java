package org.agilereview.storage.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.StorageAPI;
import org.agilereview.storage.xml.conversion.Pojo2Jaxb;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.xmlschema.author.Comments;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * Test for {@link Pojo2Jaxb} conversion. 
 * @author Peter Reuter (01.12.2013)
 */
@SuppressWarnings("javadoc")
public class JaxbConversionTest {
    
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
    private static final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path("/org.agilereview.storage.xml/src/org/agilereview/storage/xml/conversion/PojoConversion.java"));
    private static final String[] commentIds = { "c0", "c1" };
    private static final String[] replyIds = { "r0", reviewId };
    
    @Test
    public void testGetJaxbComments() throws ConversionException {
    	
        Review r = StorageAPI.createReview(reviewId, "", 0, "", "", "");
        ArrayList<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < commentIds.length; i++) {
            Comment c = StorageAPI.createComment(commentIds[i], author, file, r, date, date, recipient, prio, status, ctext);
            comments.add(c);
            for (int j = 0; j < replyIds.length; j++) {
                Reply rp = StorageAPI.createReply(replyIds[j], recipient, date, date, rtext, c);
                c.addReply(rp);
                for (int k = 0; k < replyIds.length; k++) {
                    Reply subrp = StorageAPI.createReply(replyIds[k], recipient, date, date, rtext, rp);
                    rp.addReply(subrp);
                }
            }
        }
        
		Comments jaxbComments = Pojo2Jaxb.getJaxbComments(comments);
        
        for (int i = 0; i < commentIds.length; i++) {
        	org.agilereview.xmlschema.author.Comment jaxbComment = jaxbComments.getComment().get(i);
        	
            assertEquals(commentIds[i], jaxbComment.getId());
            assertEquals(author, jaxbComment.getAuthorName());
            assertEquals(file.getFullPath().toPortableString(), jaxbComment.getResourcePath());
            assertEquals(reviewId, jaxbComment.getReviewID());
            assertEquals(0, date.compareTo(jaxbComment.getCreationDate().toGregorianCalendar()));
            // comment was modified, so last modified > creation date
            assertTrue(date.compareTo(jaxbComment.getLastModified().toGregorianCalendar()) <= 0);
            assertEquals(recipient, jaxbComment.getRecipient());
            assertEquals(new Integer(prio), jaxbComment.getPriority());
            assertEquals(status, jaxbComment.getStatus());
            assertEquals(ctext, jaxbComment.getText());
            for (int j = 0; j < replyIds.length; j++) {
        		org.agilereview.xmlschema.author.Reply jaxbReply = jaxbComment.getReplies().getReply().get(j);
                assertEquals(replyIds[j], jaxbReply.getId());
                assertEquals(recipient, jaxbReply.getAuthor());
                assertEquals(0, date.compareTo(jaxbReply.getCreationDate().toGregorianCalendar()));
                assertTrue(date.compareTo(jaxbReply.getLastModified().toGregorianCalendar()) <= 0);
                assertEquals(rtext, jaxbReply.getText());
                for (int k = 0; k < replyIds.length; k++) {
            		org.agilereview.xmlschema.author.Reply subJaxbReply = jaxbReply.getReplies().getReply().get(k);
                    assertEquals(replyIds[k], subJaxbReply.getId());
                    assertEquals(recipient, subJaxbReply.getAuthor());
                    assertEquals(0, date.compareTo(subJaxbReply.getCreationDate().toGregorianCalendar()));
                    assertTrue(date.compareTo(subJaxbReply.getLastModified().toGregorianCalendar()) <= 0);
                    assertEquals(rtext, subJaxbReply.getText());
                }
            }
        }
    }
    
    @Test
    public void testGetJaxbReview() {
        Review review = StorageAPI.createReview(reviewId, reviewName, status, reviewReference, recipient, reviewText);
        
        org.agilereview.xmlschema.review.Review jaxbReview = Pojo2Jaxb.getJaxbReview(review);
        
        assertEquals(reviewId, jaxbReview.getId());
        assertEquals(status, jaxbReview.getStatus());
        assertEquals(reviewReference, jaxbReview.getReferenceId());
        assertEquals(recipient, jaxbReview.getResponsibility());
        assertEquals(reviewText, jaxbReview.getDescription());
    }
    
}
