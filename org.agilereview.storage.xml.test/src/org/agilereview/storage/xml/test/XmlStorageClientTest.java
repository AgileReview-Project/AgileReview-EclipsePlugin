package org.agilereview.storage.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.core.external.storage.StorageAPI;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.XmlStorageClient;
import org.agilereview.storage.xml.conversion.Pojo2Jaxb;
import org.agilereview.storage.xml.exception.ConversionException;
import org.agilereview.storage.xml.persistence.SourceFolderManager;
import org.agilereview.xmlschema.author.Comments;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class XmlStorageClientTest {
    
    private static IProject SourceFolder = null;
    
    private static final String SourceFolderName = "AgileReview_Test";
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
    private static final String[] replyIds = { "r0", "r1", "r2", "r3", "r4", "r5" };
    
    private static Review r;
    
    @BeforeClass
    public static void setUpWorkspace() throws CoreException, IOException, JAXBException, ConversionException {
        // set up test data
        r = StorageAPI.createReview(reviewId, reviewName, status, reviewReference, recipient, reviewText);
        r.setIsOpen(true);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        for (String commentId : commentIds) {
            Comment c = StorageAPI.createComment(commentId, author, file, r, date, date, recipient, prio, status, ctext);
            comments.add(c);
            for (int j = 0; j < 2; j++) {
                Reply rp = StorageAPI.createReply(commentId + replyIds[j], recipient, date, date, rtext, c);
                c.addReply(rp);
                for (int k = 2 + 2 * j; k < 2 + 2 * j + 2; k++) {
                    Reply subrp = StorageAPI.createReply(commentId + replyIds[j] + replyIds[k], recipient, date, date, rtext, rp);
                    rp.addReply(subrp);
                }
            }
        }
        
        // create SourceFolder
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject p = workspaceRoot.getProject(SourceFolderName);
        p.create(null);
        while (!p.exists()) {
        }
        if (!p.isOpen()) {
            p.open(null);
            while (!p.isOpen()) {
            }
            SourceFolder = p;
        }
        IProjectDescription projectDesc = p.getDescription();
        projectDesc.setNatureIds(new String[] { SourceFolderManager.AGILEREVIEW_NATURE });
        p.setDescription(projectDesc, null);
        
        // create review folder
        IFolder folder = p.getFolder("review." + reviewId);
        folder.create(IResource.NONE, true, null);
        while (!folder.exists()) {
        }
        
        // create review file
        IFile rf = folder.getFile("review.xml");
        rf.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
        while (!rf.exists()) {
        }
        
        // create author file
        IFile af = folder.getFile("author_" + author + ".xml");
        af.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
        while (!af.exists()) {
        }
        
        // save test data Jaxb objects to the newly created source folder
        Comments cs = Pojo2Jaxb.getJaxbComments(comments);
        saveToFile(af, cs, Comments.class);
        org.agilereview.xmlschema.review.Review review = Pojo2Jaxb.getJaxbReview(r);
        saveToFile(rf, review, org.agilereview.xmlschema.review.Review.class);
    }
    
    @Test
    public void testPreferenceChangeListener() throws InterruptedException {
        IEclipsePreferences preferencesNode = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        
        preferencesNode.put("source_folder", "");
        assertEquals(SourceFolderManager.getCurrentReviewSourceProject(), null);
        XmlStorageClient xmlStorage = new XmlStorageClient();
        ReviewSet reviews = xmlStorage.getAllReviews();
        assertEquals(0, reviews.size());
        
        preferencesNode.put("source_folder", SourceFolderName);
        assertEquals(SourceFolder, SourceFolderManager.getCurrentReviewSourceProject());
        xmlStorage = new XmlStorageClient();
        reviews = xmlStorage.getAllReviews();
        assertEquals(1, reviews.size());
        
        preferencesNode.put("source_folder", "");
        assertEquals(SourceFolderManager.getCurrentReviewSourceProject(), null);
        reviews = xmlStorage.getAllReviews();
        assertEquals(0, reviews.size());
    }
    
    @Test
    public void testgetReviewSet() {
        
        // prepare Current Source Folder
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", SourceFolderName);
        
        // get review data
        XmlStorageClient xmlStorage = new XmlStorageClient();
        ReviewSet reviews = xmlStorage.getAllReviews();
        
        // check basic criteria of loading, full check done by tests for conversion classes
        assertEquals(1, reviews.size());
        
        List<Comment> comments = ((Review) reviews.toArray()[0]).getComments();
        assertEquals(2, comments.size());
        for (int i = 0; i < 2; i++) {
            Comment currentComment = comments.get(i);
            assertEquals(2, currentComment.getReplies().size());
            for (int j = 0; j < 2; j++) {
                Reply currentReply = currentComment.getReplies().get(j);
                assertEquals(2, currentReply.getReplies().size());
            }
        }
        
        // clean up
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", "");
    }
    
    @Test
    public void testPersistence() throws InterruptedException {
        InstanceScope.INSTANCE.getNode(AgileReviewPreferences.CORE_PLUGIN_ID).put(AgileReviewPreferences.AUTHOR, author);
        
        // prepare Current Source Folder
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", SourceFolderName);
        
        // get review data
        XmlStorageClient xmlStorage = new XmlStorageClient();
        ReviewSet reviews = xmlStorage.getAllReviews();
        
        // update review data
        String newReviewDescription = "Review changed.";
        String newCommentText = "Comment changed.";
        String newReplyText = "Reply changed.";
        
        Review rLoaded = ((Review) reviews.toArray()[0]);
        rLoaded.setDescription(newReviewDescription);
        rLoaded.getComments().get(0).setText(newCommentText);
        rLoaded.getComments().get(0).getReplies().get(0).setText(newReplyText);
        Comment comment2 = StorageAPI.createComment("c2", "Piet", file, rLoaded, Calendar.getInstance(), Calendar.getInstance(), "Malte", 1, 0,
                "Comment text");
        rLoaded.addComment(comment2);
        
        Reply newReply = StorageAPI.createReply("c2r0", "Piet", Calendar.getInstance(), Calendar.getInstance(), "Reply text", rLoaded.getComments()
                .get(2));
        rLoaded.getComments().get(2).addReply(newReply);
        
        // force reload
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", "");
        assertEquals(0, xmlStorage.getAllReviews().size());
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", SourceFolderName);
        assertEquals(1, xmlStorage.getAllReviews().size());
        
        // check if manipulations + added comments/replies were stored
        xmlStorage = new XmlStorageClient();
        reviews = xmlStorage.getAllReviews();
        rLoaded = ((Review) reviews.toArray()[0]);
        assertEquals(newReviewDescription, rLoaded.getDescription());
        assertEquals(newCommentText, rLoaded.getComments().get(0).getText());
        assertEquals(newReplyText, rLoaded.getComments().get(0).getReplies().get(0).getText());
        assertEquals(3, rLoaded.getComments().size());
        assertEquals(1, rLoaded.getComments().get(2).getReplies().size());
        
        // add new comments for new author
        String newAuthor = "DeletingGuy";
        InstanceScope.INSTANCE.getNode(AgileReviewPreferences.CORE_PLUGIN_ID).put(AgileReviewPreferences.AUTHOR, newAuthor);
        Comment commentToDelete = StorageAPI.createComment("c1", newAuthor, file, rLoaded, Calendar.getInstance(), Calendar.getInstance(), "Peter",
                1, 0, "Comment text");
        rLoaded.addComment(commentToDelete);
        // check if new file was created
        assertTrue(SourceFolder.getFolder("review." + reviewId).getFile("author_" + newAuthor + ".xml").exists());
        // remove comments from new author
        rLoaded.deleteComment(commentToDelete);
        // check if author file was deleted and comment was removed
        assertFalse(SourceFolder.getFolder("review." + reviewId).getFile("author_" + newAuthor + ".xml").exists());
        assertFalse(rLoaded.getComments().contains(commentToDelete));
        
        rLoaded.setIsOpen(false);
        assertEquals(0, rLoaded.getComments().size());
        assertTrue(SourceFolder.getFolder("review." + reviewId).getFile("author_" + author + ".xml").exists());
        
        rLoaded.setIsOpen(true);
        assertEquals(3, rLoaded.getComments().size());
        
        // clean up
        rLoaded.deleteComment(comment2);
        InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).put("source_folder", "");
    }
    
    @AfterClass
    public static void cleanUpWorkspace() throws CoreException {
        if (!(SourceFolder == null) && SourceFolder.exists()) {
            SourceFolder.delete(true, null);
        }
    }
    
    private static void saveToFile(IFile file, Object jaxbObject, Class clazz) throws JAXBException, CoreException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(jaxbObject, file.getLocation().toFile());
        file.refreshLocal(IFile.DEPTH_INFINITE, null);
    }
    
}
