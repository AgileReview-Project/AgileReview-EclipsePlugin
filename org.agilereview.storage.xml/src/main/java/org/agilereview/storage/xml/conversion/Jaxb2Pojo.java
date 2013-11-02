package org.agilereview.storage.xml.conversion;

import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.StorageAPI;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.storage.xml.exception.DataLoadingException;
import org.agilereview.xmlschema.author.Comments;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;


public class Jaxb2Pojo {
	
	public static org.agilereview.xmlschema.review.Review loadReview(IFile reviewFile) throws ConversionException, DataLoadingException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.agilereview.xmlschema.review.Review.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			org.agilereview.xmlschema.review.Review jaxbReview = (org.agilereview.xmlschema.review.Review) jaxbUnmarshaller.unmarshal(reviewFile.getContents());
			
			return jaxbReview;
			
		} catch (JAXBException e) {
			throw new ConversionException(e);
		} catch (CoreException e) {
			throw new DataLoadingException(e);
		}
	}
	
	public static Comments loadComments(IFile authorFile) throws ConversionException, DataLoadingException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Comments.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Comments jaxbComments = (Comments) jaxbUnmarshaller.unmarshal(authorFile.getContents());
			
			return jaxbComments;
			
		} catch (JAXBException e) {
			throw new ConversionException(e);
		} catch (CoreException e) {
			throw new DataLoadingException(e);
		}
	}
	
	public static Comment getComment(Review review, org.agilereview.xmlschema.author.Comment jaxbComment) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jaxbComment.getResourcePath()));
			XMLGregorianCalendar creationDate = jaxbComment.getCreationDate();
			if (creationDate == null) {
				try {
					creationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
				} catch (DatatypeConfigurationException e) {
					e.printStackTrace();
				}
				ExceptionHandler.logAndNotifyUser(new Exception(String.format("Creation Date of comment %s in review %s contains an invalid value and was set to a default value.", jaxbComment.getId(), jaxbComment.getReviewID())), Activator.PLUGIN_ID);
			}
			XMLGregorianCalendar lastModified = jaxbComment.getLastModified();
			if (lastModified == null) {
				try {
					lastModified = DatatypeFactory.newInstance().newXMLGregorianCalendar();
				} catch (DatatypeConfigurationException e) {
					e.printStackTrace();
				}
				ExceptionHandler.logAndNotifyUser(new Exception(String.format("Modification Date of comment %s in review %s contains an invalid value and was set to a default value.", jaxbComment.getId(), jaxbComment.getReviewID())), Activator.PLUGIN_ID);
			}
			return StorageAPI.createComment(jaxbComment.getId(), jaxbComment.getAuthorName(), file, review, (Calendar) creationDate.toGregorianCalendar(), (Calendar) lastModified.toGregorianCalendar(), jaxbComment.getRecipient(), jaxbComment.getStatus(), jaxbComment.getPriority(), jaxbComment.getText());
	}
	
	public static Review getReview(org.agilereview.xmlschema.review.Review jaxbReview) {
		return new Review(jaxbReview.getId(), jaxbReview.getName(), jaxbReview.getStatus(), jaxbReview.getReferenceId(), jaxbReview.getResponsibility(), jaxbReview.getDescription());
	}

}
