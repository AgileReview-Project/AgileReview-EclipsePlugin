package org.agilereview.storage.xml.conversion;

import java.util.ArrayList;
import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.xmlSchema.author.CommentDocument;
import org.agilereview.xmlSchema.author.CommentsDocument;
import org.agilereview.xmlSchema.author.CommentsDocument.Comments;
import org.agilereview.xmlSchema.author.RepliesDocument.Replies;
import org.agilereview.xmlSchema.review.ReviewDocument;

/**
 * The {@link PojoConversion} class provides methods for converting pojo data into XmlBeans data.
 * @author Peter Reuter (28.05.2012)
 */
public class PojoConversion {

	/**
	 * Converts a list of {@link Comment} objects to a XmlBeans {@link CommentDocument}. 
	 * @param comments An {@link ArrayList} of comments to be converted.
	 * @return A XmlBeans {@link CommentDocument} holding {@link org.agilereview.xmlSchema.author.CommentDocument.Comment}
	 *  and their respective {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static CommentsDocument getXmlBeansCommentsDocument(ArrayList<Comment> comments) {
		CommentsDocument doc = CommentsDocument.Factory.newInstance();
		Comments xmlBeansComments = doc.addNewComments();
		for (Comment comment: comments) {
			org.agilereview.xmlSchema.author.CommentDocument.Comment xmlBeansComment = xmlBeansComments.addNewComment();
			xmlBeansComment.setId(comment.getId());
			xmlBeansComment.setAuthorName(comment.getAuthor());
			xmlBeansComment.setResourcePath(comment.getCommentedFile().getFullPath().toPortableString());
			xmlBeansComment.setReviewID(comment.getReview().getId());
			xmlBeansComment.setCreationDate(comment.getCreationDate());
			xmlBeansComment.setLastModified(comment.getModificationDate());
			xmlBeansComment.setRecipient(comment.getRecipient());
			xmlBeansComment.setStatus(comment.getStatus());
			xmlBeansComment.setPriority(comment.getPriority());
			xmlBeansComment.setText(comment.getText());
			if (!comment.getReplies().isEmpty()) {
				getXmlBeansReplies(comment.getReplies(), xmlBeansComment.addNewReplies());
			}
		}
		return doc;
	}

	/**
	 * Converts a {@link List} of {@link Reply} objects to {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply}
	 *  objects and adds them to the {@link Replies} object.
	 * @param replies The {@link Reply} objects to be converted.
	 * @param xmlBeansReplies The {@link Replies} object to which the 
	 * {@link org.agilereview.xmlSchema.author.ReplyDocument.Reply} objects are to be added to.
	 * @author Peter Reuter (04.04.2012)
	 */
	public static void getXmlBeansReplies(List<Reply> replies, Replies xmlBeansReplies) {
		for (Reply reply : replies) {
			org.agilereview.xmlSchema.author.ReplyDocument.Reply xmlBeansReply = xmlBeansReplies.addNewReply();
			xmlBeansReply.setId(reply.getId());
			xmlBeansReply.setAuthor(reply.getAuthor());
			xmlBeansReply.setCreationDate(reply.getCreationDate());
			xmlBeansReply.setLastModified(reply.getModificationDate());
			xmlBeansReply.setText(reply.getText());
			if (!reply.getReplies().isEmpty()) {
				getXmlBeansReplies(reply.getReplies(), xmlBeansReply.addNewReplies());
			}
		}
	}

	/**
	 * Converts a {@link Review} object to a {@link ReviewDocument}.
	 * @param review The {@link Review} to convert into a {@link ReviewDocument}.
	 * @return The {@link ReviewDocument}.
	 */
	public static ReviewDocument getXmlBeansReviewDocument(Review review) {
		ReviewDocument doc = ReviewDocument.Factory.newInstance();
		org.agilereview.xmlSchema.review.ReviewDocument.Review xmlBeansReview = doc.addNewReview();
		xmlBeansReview.setId(review.getId());
		xmlBeansReview.setName(review.getName());
		xmlBeansReview.setStatus(review.getStatus());
		xmlBeansReview.setReferenceId(review.getReference());
		xmlBeansReview.setResponsibility(review.getResponsibility());
		xmlBeansReview.setDescription(review.getDescription());
		return doc;
	}

}
