package org.agilereview.storage.xml.conversion;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.storage.xml.exception.DataStoringException;
import org.agilereview.xmlschema.author.Comments;
import org.agilereview.xmlschema.author.Replies;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


public class Pojo2Jaxb {
	
	public static void saveReview(org.agilereview.xmlschema.review.Review review, IFile file) throws ConversionException, DataStoringException {
		save(file, review.getClass(), review);
	}
	
	public static void saveComments(org.agilereview.xmlschema.author.Comments comments, IFile file) throws ConversionException, DataStoringException {
		save(file, comments.getClass(), comments);
	}

	/**
	 * @param file
	 * @throws ConversionException
	 * @author Peter Reuter (02.11.2013)
	 * @throws DataStoringException 
	 */
	private static void save(IFile file, Class clazz, Object jaxbObject) throws ConversionException, DataStoringException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(jaxbObject, file.getLocation().toFile());
			file.refreshLocal(IFile.DEPTH_INFINITE, null);
		} catch (JAXBException e) {
			throw new ConversionException(e);
		} catch (CoreException e) {
			String message = "Error while refreshing file "+file.getFullPath().toOSString()+"!";
			throw new DataStoringException(message);
		}
	}
	
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
	 * @param comment
	 * @param dateCreated
	 * @return
	 * @throws ConversionException
	 * @author Peter Reuter (02.11.2013)
	 * @param timeInMillis
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
