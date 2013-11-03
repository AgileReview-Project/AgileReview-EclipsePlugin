package org.agilereview.storage.xml.conversion;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.xmlschema.author.Comments;
import org.agilereview.xmlschema.author.Replies;

/**
 * Methods for converting AgileReview Pojos to Jaxb Pojos
 * @author Peter Reuter (03.11.2013)
 */
public class Pojo2Jaxb {
	
	/**
	 * Get Jaxb {@link Comments} object from {@link List} of {@link Comment}
	 * @param comments
	 * @return the converted {@link Comments} object
	 * @throws ConversionException
	 * @author Peter Reuter (03.11.2013)
	 */
	public static Comments getJaxbComments(List<Comment> comments) throws ConversionException {
		Comments jaxbComments = new Comments();
		for (Comment comment: comments) {
			org.agilereview.xmlschema.author.Comment jaxbComment = new org.agilereview.xmlschema.author.Comment();
			jaxbComment.setAuthorName(comment.getAuthor());
			XMLGregorianCalendar dateCreated = getXmlGregorianCalendar(comment.getCreationDate().getTimeInMillis());
			jaxbComment.setCreationDate(dateCreated);
			jaxbComment.setId(comment.getId());
			XMLGregorianCalendar dateModified = getXmlGregorianCalendar(comment.getModificationDate().getTimeInMillis());
			jaxbComment.setLastModified(dateModified);
			jaxbComment.setPriority(comment.getPriority());
			jaxbComment.setRecipient(comment.getRecipient());
			jaxbComment.setReplies(getJaxbReplies(comment.getReplies()));			
			jaxbComment.setResourcePath(comment.getCommentedFile().getFullPath().toPortableString());
			jaxbComment.setReviewID(comment.getReview().getId());
			jaxbComment.setStatus(comment.getStatus());
			jaxbComment.setText(comment.getText());
						
			jaxbComments.getComment().add(jaxbComment);
		}
		return jaxbComments;
	}
	
	/**
	 * Get Jaxb {@link org.agilereview.xmlschema.review.Review} for AgileReview {@link Review}
	 * @param review
	 * @return the jaxb {@link org.agilereview.xmlschema.review.Review}
	 * @author Peter Reuter (03.11.2013)
	 */
	public static org.agilereview.xmlschema.review.Review getJaxbReview(Review review) {
		org.agilereview.xmlschema.review.Review jaxbReview = new org.agilereview.xmlschema.review.Review();
		jaxbReview.setDescription(review.getDescription());
		jaxbReview.setId(review.getId());
		jaxbReview.setName(review.getName());
		jaxbReview.setReferenceId(review.getReference());
		jaxbReview.setResponsibility(review.getResponsibility());
		jaxbReview.setStatus(review.getStatus());
		return jaxbReview;
	}

	/**
	 * @param timeInMillis
	 * @return a {@link XMLGregorianCalendar} corresponding to the specified time in milliseconds
	 * @throws ConversionException
	 * @author Peter Reuter (02.11.2013)
	 */
	private static XMLGregorianCalendar getXmlGregorianCalendar(long timeInMillis) throws ConversionException {
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTimeInMillis(timeInMillis);
		try {
			XMLGregorianCalendar newXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
			return newXMLGregorianCalendar;
		} catch (DatatypeConfigurationException e) {
			throw new ConversionException(e);
		}
	}
	
	/**
	 * Get Jaxb {@link Replies} for the {@link List} of {@link Reply}
	 * @param replies
	 * @return The jaxb {@link Replies}
	 * @throws ConversionException
	 * @author Peter Reuter (03.11.2013)
	 */
	private static org.agilereview.xmlschema.author.Replies getJaxbReplies(List<Reply> replies) throws ConversionException {
		Replies jaxbReplies = new Replies();
		
		for (Reply reply: replies) {
			org.agilereview.xmlschema.author.Reply jaxbReply = new org.agilereview.xmlschema.author.Reply();
			jaxbReply.setAuthor(reply.getAuthor());
			XMLGregorianCalendar dateCreated = getXmlGregorianCalendar(reply.getCreationDate().getTimeInMillis());
			jaxbReply.setCreationDate(dateCreated);
			jaxbReply.setId(reply.getId());
			XMLGregorianCalendar dateModified = getXmlGregorianCalendar(reply.getModificationDate().getTimeInMillis());
			jaxbReply.setLastModified(dateModified);
			if (!reply.getReplies().isEmpty()) {
				jaxbReply.setReplies(getJaxbReplies(reply.getReplies()));
			}
			jaxbReply.setText(reply.getText());
		}
		
		return jaxbReplies;
	}

}
