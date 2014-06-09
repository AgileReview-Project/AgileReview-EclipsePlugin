package org.agilereview.export.xls.impl;

import java.io.File;

/**
 * File wrapper in order to get a standardized object with the fitting output informations for an export
 */
public class FileExportWrapper {
    
    /**
     * Filename of the represented file
     */
    private String filename = "";
    /**
     * Review of the represented file
     */
    private String review = "";
    /**
     * Project of the represented file
     */
    private String project = "";
    /**
     * Path of the represented file
     */
    private String path = "";
    
    /**
     * Creates a new FileExportWrapper instance with a given {@link java.io.File} and the correlated project
     * @param file which should be represented by this object
     * @param project correlating to the given file
     */
    protected FileExportWrapper(java.io.File file, String project) {
        this.filename = file.getName();
        this.project = project;
        String p = file.getPath();
        this.path = p.substring(p.indexOf(project) + project.length(), p.indexOf(filename) - 1);
    }
    
    /**
     * Creates a new FileExportWrapper instance with a given {@link File} and the correlated project
     * @param file which should be represented by this object
     * @param review in which the given file is in
     * @param project correlating to the given file
     */
    protected FileExportWrapper(File file, String review, String project) {
        this.filename = file.getName();
        this.review = review;
        this.project = project;
        this.path = file.getAbsolutePath();
    }
    
    /**
     * Returns the filename of this instance
     * @return the filename of this instance
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Returns the review of this instance
     * @return the review of this instance
     */
    public String getReview() {
        return review;
    }
    
    /**
     * Returns the project of this instance
     * @return the project of this instance
     */
    public String getProject() {
        return project;
    }
    
    /**
     * Returns the path of this instance
     * @return the path of this instance
     */
    public String getPath() {
        return path;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * @author Malte Brunnlieb (09.06.2014)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof FileExportWrapper) {
            return getPath().equals(((FileExportWrapper) obj).getPath());
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * @author Malte Brunnlieb (09.06.2014)
     */
    @Override
    public int hashCode() {
        return path.hashCode();
    }
}