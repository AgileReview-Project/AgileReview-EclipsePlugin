package org.agilereview.storage.xml.persistence;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.Helper;
import org.agilereview.storage.xml.conversion.Pojo2Jaxb;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.storage.xml.exception.DataStoringException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Methods for persisting {@link Comment}s and {@link Review}s
 * @author Peter Reuter (03.11.2013)
 */
public class XmlPersister {
	
	/**
	 * @param comment
	 * @author Peter Reuter (03.11.2013)
	 */
	public static void store(Comment comment) {
		IFile commentFile = SourceFolderManager.getCommentFile(comment.getReview().getId(), comment.getAuthor());
		if (commentFile != null) {
			try {
				org.agilereview.xmlschema.author.Comments jaxbComments = Pojo2Jaxb.getJaxbComments(Helper.getComments(comment.getReview(), comment.getAuthor()));
				XmlPersister.saveComments(jaxbComments, commentFile);
			} catch (ConversionException e) {
				String message = "Error while storing data in current Review Source Project. The data could not be converted to XML.";
				ExceptionHandler.logAndNotifyUser(new DataStoringException(message), Activator.PLUGIN_ID);
			} catch (DataStoringException e) {
				String message = "Error while storing data in current Review Source Project.";
				ExceptionHandler.logAndNotifyUser(new DataStoringException(message), Activator.PLUGIN_ID);
			}
		}
	}
	
	/**
	 * @param review
	 * @author Peter Reuter (03.11.2013)
	 */
	public static void store(Review review) {
		IFile reviewFile = SourceFolderManager.getReviewFile(review.getId());
		if (reviewFile != null) {
			try {
			org.agilereview.xmlschema.review.Review jaxbReview = Pojo2Jaxb.getJaxbReview(review);
			XmlPersister.saveReview(jaxbReview, reviewFile);
			} catch (ConversionException e) {
				String message = "Error while storing data in current Review Source Project. The data could not be converted to XML.";
				ExceptionHandler.logAndNotifyUser(new DataStoringException(message), Activator.PLUGIN_ID);
			} catch (DataStoringException e) {
				String message = "Error while storing data in current Review Source Project.";
				ExceptionHandler.logAndNotifyUser(new DataStoringException(message), Activator.PLUGIN_ID);
			}
		}		
	}

	/**
	 * @param review
	 * @param file
	 * @throws ConversionException
	 * @throws DataStoringException
	 * @author Peter Reuter (03.11.2013)
	 */
	private static void saveReview(org.agilereview.xmlschema.review.Review review, IFile file) throws ConversionException, DataStoringException {
		XmlPersister.save(file, review.getClass(), review);
	}

	/**
	 * @param comments
	 * @param file
	 * @throws ConversionException
	 * @throws DataStoringException
	 * @author Peter Reuter (03.11.2013)
	 */
	private static void saveComments(org.agilereview.xmlschema.author.Comments comments, IFile file) throws ConversionException, DataStoringException {
		XmlPersister.save(file, comments.getClass(), comments);
	}

	/**
	 * @param file
	 * @param clazz 
	 * @param jaxbObject 
	 * @throws DataStoringException
	 * @throws ConversionException
	 * @author Peter Reuter (02.11.2013) 
	 */
	private static void save(IFile file, @SuppressWarnings("rawtypes") Class clazz, Object jaxbObject) throws ConversionException, DataStoringException {
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

}
