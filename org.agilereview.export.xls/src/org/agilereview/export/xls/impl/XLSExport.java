package org.agilereview.export.xls.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.Configuration;
import net.sf.jxls.transformer.XLSTransformer;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the interface to jxls for exporting reviews and comments to xls, xlsx sheets
 */
public class XLSExport implements IRunnableWithProgress {
    
    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(XLSExport.class);
    
    /**
     * Reviews to be exported
     */
    private final List<Review> reviews;
    /**
     * Template path where the xls template can be found
     */
    private final String templatePath;
    /**
     * Output path where the new instance of the exported template should be stored
     */
    private String outputPath;
    
    /**
     * Creates a new Instance of XSLExport for a list of reviews, the templatePath where the export class can find the xls template to be used and the
     * outputPath where the new instance of the exported template should be stored
     * @param reviews which should be exported
     * @param templatePath path to the xls/xlsx template
     * @param outputPath directory to which the data should exported
     */
    public XLSExport(List<Review> reviews, String templatePath, String outputPath) {
        this.reviews = reviews;
        this.templatePath = templatePath;
        this.outputPath = outputPath;
    }
    
    /**
     * Starts the export process for the attributes with which this class was instantiated
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            exportReviews(monitor);
        } catch (ParsePropertyException e) {
            LOG.error("Error while parsing the xls/xlsx template.", e);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error while exporting Reviews",
                    "The formulas in the selected template file cannot be evaluated correctly.");
        } catch (InvalidFormatException e) {
            LOG.error("Error while parsing the xls/xlsx template", e);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error while exporting Reviews",
                    "An error occured while exporting the selected Reviews.");
        } catch (IOException e) {
            LOG.error("The template could not be read or the result could not be written.", e);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error while exporting Reviews",
                    "The template could not be read or the result could not be written.");
        }
    }
    
    /**
     * This function provides the functionality for exporting the given Reviews to the outputPath by using the xls/xlsx template specified in the
     * templatePath
     * @param monitor
     * @throws ParsePropertyException occurs during the transfomation process of jxls
     * @throws InvalidFormatException occurs during the transfomation process of jxls
     * @throws IOException occurs during the transfomation process of jxls
     */
    private void exportReviews(IProgressMonitor monitor) throws ParsePropertyException, InvalidFormatException, IOException {
        monitor.beginTask("Performing export: ", IProgressMonitor.UNKNOWN);
        monitor.subTask("Collecting review data...");
        Map<String, Object> beans = new HashMap<String, Object>();
        
        //collect comments
        //collect all files which have been reviewed
        Set<FileExportWrapper> reviewFiles = new HashSet<>();
        ArrayList<CommentWrapper> comments = new ArrayList<>();
        Set<File> projects = new HashSet<>();
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                FileExportWrapper reviewedFile = new FileExportWrapper(c.getCommentedFile().getFullPath().toFile(), r.getName(), c.getCommentedFile()
                        .getProject().getName());
                comments.add(new CommentWrapper(c, reviewedFile));
                projects.add(c.getCommentedFile().getProject().getRawLocation().toFile());
                reviewFiles.add(reviewedFile);
            }
        }
        beans.put("reviewFiles", reviewFiles);
        beans.put("comments", comments);
        
        //collect all files which are in a project which has been reviewed partially
        ArrayList<FileExportWrapper> projectFiles = new ArrayList<FileExportWrapper>();
        for (File f : projects) {
            projectFiles.addAll(getAllWrappedFiles(f, f.getName()));
        }
        beans.put("projectFiles", projectFiles);
        beans.put("reviews", reviews);
        
        Configuration config = new Configuration();
        XLSTransformer transformer = new XLSTransformer(config);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String filetype = templatePath.substring(templatePath.lastIndexOf("."));
        
        if (!outputPath.endsWith(System.getProperty("file.separator"))) {
            outputPath += System.getProperty("file.separator");
        }
        
        monitor.subTask("Creating export sheet...");
        transformer.transformXLS(templatePath, beans, outputPath + "agilereview_export_" + df.format(Calendar.getInstance().getTime()) + filetype);
        monitor.done();
    }
    
    //    /**
    //     * Searches recursively for all files under the given folder and wraps them into a {@link FileExportWrapper} object
    //     * @param folder root node of the search process
    //     * @param review to which these files correlate
    //     * @param project to which these files correlate
    //     * @return a list of all found and wrapped {@link FileExportWrapper} objects
    //     */
    //    private static ArrayList<FileExportWrapper> getAllWrappedFiles(Folder folder, String review, String project, ArrayList<CommentWrapper> comments) {
    //        ArrayList<FileExportWrapper> files = new ArrayList<FileExportWrapper>();
    //        
    //        TreeSet<String> omittings = new TreeSet<String>(Arrays.asList(pm.getInternalProperty(PropertiesManager.INTERNAL_KEYS.EXPORT_OMITTINGS).split(
    //                ",")));
    //        LinkedList<File> tmpFiles = new LinkedList<File>();
    //        for (File f : Arrays.asList(folder.getFileArray())) {
    //            if (!omittings.contains(f.getName())) {
    //                tmpFiles.add(f);
    //                collectComments(comments, review, project, f);
    //            }
    //        }
    //        
    //        files.addAll(convertFilesToWrappedFiles(tmpFiles, review, project));
    //        for (Folder f : folder.getFolderArray()) {
    //            files.addAll(getAllWrappedFiles(f, review, project, comments));
    //        }
    //        
    //        return files;
    //    }
    //    
    /**
     * Searches recursively for all files under the given file and wraps them into a {@link FileExportWrapper} object
     * @param file root node of the search process
     * @param project to which these files correlate
     * @return a list of all found and wrapped {@link FileExportWrapper} objects
     */
    private static ArrayList<FileExportWrapper> getAllWrappedFiles(File file, String project) {
        ArrayList<FileExportWrapper> files = new ArrayList<FileExportWrapper>();
        
        final TreeSet<String> omittings = new TreeSet<>();
        //TODO (MB) Omittings
        //                new TreeSet<String>(Arrays.asList(pm.getInternalProperty(PropertiesManager.INTERNAL_KEYS.EXPORT_OMITTINGS)
        //                .split(",")));
        
        File[] fs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return !omittings.contains(arg1);
            }
        });
        
        for (File f : fs) {
            if (f.isFile()) {
                files.add(new FileExportWrapper(f, project));
            } else if (f.isDirectory()) {
                files.addAll(getAllWrappedFiles(f, project));
            }
        }
        
        return files;
    }
    //    
    //    /**
    //     * Converts a given list of files in a given review and project to the respective {@link FileExportWrapper} representation
    //     * @param input files which should be wrapped
    //     * @param review in which the given files are in
    //     * @param project under which the given files are lying
    //     * @return a list of all wrapped {@link FileExportWrapper}
    //     */
    //    private static ArrayList<FileExportWrapper> convertFilesToWrappedFiles(List<File> input, String review, String project) {
    //        ArrayList<FileExportWrapper> files = new ArrayList<FileExportWrapper>();
    //        
    //        for (File f : input) {
    //            files.add(new FileExportWrapper(f, review, project));
    //        }
    //        
    //        return files;
    //    }
}