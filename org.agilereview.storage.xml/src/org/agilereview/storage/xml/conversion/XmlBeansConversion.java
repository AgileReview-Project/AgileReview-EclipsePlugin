package org.agilereview.storage.xml.conversion;

import java.util.ArrayList;
import java.util.Calendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * The {@link XmlBeansConversion} class provides methods for extracting data of XmlBeans data and putting it into pojos.
 * @author Peter Reuter (28.05.2012)
 */
public class XmlBeansConversion {
	
	/**
	 * Creates a {@link Review} object from the given XmlBeans {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} object.
	 * @param xmlBeansReview The {@link org.agilereview.xmlSchema.review.ReviewDocument.Review} object to convert.
	 * @return The {@link Review} object constructed from the {@link Review} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static Review getReview(org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview) {
		String id = xmlBeansReview.getId();
		int status = xmlBeansReview.getStatus();
		String reference = xmlBeansReview.getReferenceId();
		String responsibility = xmlBeansReview.getResponsibility();
		String description = xmlBeansReview.getDescription();
		
		Review review = new Review(id, status, reference, responsibility, description);

		return review;
	}
	
	/**
	 * Creates a {@link Comment} object from the given XmlBeans {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object.
	 * @param review The parent of the {@link Comment}.
	 * @param xmlBeansComment The {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object to convert.
	 * @return The {@link Comment} object constructed from the {@link org.agilereview.xmlSchema.author.CommentDocument.Comment} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static Comment getComment(Review review, org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment) {
		String id = xmlBeansComment.getId();
		String authorName = xmlBeansComment.getAuthorName();
		IFile commentedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(xmlBeansComment.getResourcePath()));
		Calendar creationDate = xmlBeansComment.getCreationDate();
		Calendar modificationDate = xmlBeansComment.getLastModified();
		String recipient = xmlBeansComment.getRecipient();
		int status = xmlBeansComment.getStatus();
		int priority = xmlBeansComment.getPriority();
		String text = xmlBeansComment.getText();
		
		Comment comment = new Comment(id, authorName, commentedFile, review, creationDate, modificationDate, recipient, status, priority, text);
		
		return comment;
	}
	
	/**
	 * Converts an array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply}
	 *  objects to a {@link ArrayList} of {@link Reply} objects. 
	 * @param parent The parent of the {@link Reply}s.
	 * @param xmlBeansReplies The array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects to convert.
	 * @return The {@link ArrayList} of {@link Reply} objects constructed from the array of {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static ArrayList<Reply> getReplyList(Object parent, org.agilereview.xmlSchema.author.ReplyDocument.Reply[] xmlBeansReplies) {
		ArrayList<Reply> result = new ArrayList<Reply>();
		for (org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply : xmlBeansReplies) {
			Reply reply = getReply(parent, xmlBeansReply);
			result.add(reply);
		}
		return result;
	}
	
	/**
	 * Converts a single {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} reply to a {@link Reply} object.
	 * @param parent The parent of the {@link Reply}. 
	 * @param xmlBeansReply The {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} object to convert.
	 * @return The {@link Reply} object constructed from the {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} object.
	 * @author Peter Reuter (04.04.2012)
	 */
	private static Reply getReply(Object parent, org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply) {
		String id = xmlBeansReply.getId();
		String author = xmlBeansReply.getAuthor();
		Calendar creationDate = xmlBeansReply.getCreationDate();
		Calendar modificationDate =  xmlBeansReply.getLastModified();
		String text = xmlBeansReply.getText();
		
		Reply reply = new Reply(id, author, creationDate, modificationDate, text, parent);
		
		reply.setReplies(getReplyList(reply, xmlBeansReply.getReplies().getReplyArray()));
		
		return reply;
	}

}
