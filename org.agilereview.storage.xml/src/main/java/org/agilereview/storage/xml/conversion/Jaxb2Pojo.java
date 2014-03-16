package org.agilereview.storage.xml.conversion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.StorageAPI;
import org.agilereview.xmlschema.author.Replies;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * Methods for converting Jaxb objects to AgileReview Pojos
 * @author Peter Reuter (03.11.2013)
 */
public class Jaxb2Pojo {
    
    /**
     * Convert Jaxb {@link org.agilereview.xmlschema.author.Comment} to AgileReview {@link Comment}
     * @param review
     * @param jaxbComment
     * @return the converted {@link Comment}
     * @author Peter Reuter (03.11.2013)
     */
    public static Comment getComment(Review review, org.agilereview.xmlschema.author.Comment jaxbComment) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jaxbComment.getResourcePath()));
        XMLGregorianCalendar creationDate = getSaveXmlGregorianCalendar(jaxbComment.getCreationDate());
        XMLGregorianCalendar lastModified = getSaveXmlGregorianCalendar(jaxbComment.getLastModified());
        Comment comment = StorageAPI.createComment(jaxbComment.getId(), jaxbComment.getAuthorName(), file, review,
                creationDate.toGregorianCalendar(), lastModified.toGregorianCalendar(), jaxbComment.getRecipient(), jaxbComment.getStatus(),
                jaxbComment.getPriority(), jaxbComment.getText());
        comment.setReplies(getReplies(comment, jaxbComment.getReplies()));
        return comment;
    }
    
    /**
     * @param date
     * @return
     * @author Peter Reuter (03.11.2013)
     */
    private static XMLGregorianCalendar getSaveXmlGregorianCalendar(XMLGregorianCalendar date) {
        if (date == null) {
            try {
                date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        return date;
    }
    
    /**
     * Convert Jaxb {@link org.agilereview.xmlschema.review.Review} to AgileReview {@link Review}
     * @param jaxbReview
     * @return the converted {@link Review}
     * @author Peter Reuter (03.11.2013)
     */
    public static Review getReview(org.agilereview.xmlschema.review.Review jaxbReview) {
        return StorageAPI.createReview(jaxbReview.getId(), jaxbReview.getName(), jaxbReview.getStatus(), jaxbReview.getReferenceId(), jaxbReview
                .getResponsibility(), jaxbReview.getDescription());
    }
    
    /**
     * @param parent
     * @param jaxbReplies
     * @return
     * @author Peter Reuter (03.11.2013)
     */
    private static List<Reply> getReplies(Object parent, Replies jaxbReplies) {
        List<Reply> replies = new ArrayList<Reply>();
        for (org.agilereview.xmlschema.author.Reply jaxbReply : jaxbReplies.getReply()) {
            replies.add(getReply(parent, jaxbReply));
        }
        return replies;
    }
    
    /**
     * @param parent
     * @param jaxbReply
     * @return
     * @author Peter Reuter (03.11.2013)
     */
    private static Reply getReply(Object parent, org.agilereview.xmlschema.author.Reply jaxbReply) {
        XMLGregorianCalendar creationDate = getSaveXmlGregorianCalendar(jaxbReply.getCreationDate());
        XMLGregorianCalendar modificationDate = getSaveXmlGregorianCalendar(jaxbReply.getLastModified());
        Reply reply = StorageAPI.createReply(jaxbReply.getId(), jaxbReply.getAuthor(), creationDate.toGregorianCalendar(), modificationDate
                .toGregorianCalendar(), jaxbReply.getText(), parent);
        Replies jaxbReplies = jaxbReply.getReplies();
        if (jaxbReplies != null) {
            reply.setReplies(getReplies(reply, jaxbReplies));
        }
        return reply;
    }
    
}
