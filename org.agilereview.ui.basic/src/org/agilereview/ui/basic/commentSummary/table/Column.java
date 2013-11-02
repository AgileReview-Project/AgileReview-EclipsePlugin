/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.table;

/**
 * This class summarizes necessary informations about each column in an enumeration
 * @author Malte Brunnlieb (12.05.2012)
 */
public enum Column {
    
    /**
     * Review ID respectively Review name
     */
    REVIEW_NAME {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Review name";
        }
    },
    /**
     * Comment ID
     */
    COMMENT_ID {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "CommentID";
        }
    },
    /**
     * Author name of a comment
     */
    AUTHOR {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Author";
        }
    },
    /**
     * Recipient of a comment
     */
    RECIPIENT {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Recipient";
        }
    },
    /**
     * Status of a comment
     */
    STATUS {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Status";
        }
    },
    /**
     * Priority of a comment
     */
    PRIORITY {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Priority";
        }
    },
    /**
     * Date when a comment was created
     */
    DATE_CREATED {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Date created";
        }
    },
    /**
     * Date when a comment was last modified
     */
    DATE_MODIFIED {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Date modified";
        }
    },
    /**
     * Number of replies made to a comment
     */
    NO_REPLIES {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Replies";
        }
    },
    /**
     * Location of the commented file
     */
    LOCATION {
        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         * @author Malte Brunnlieb (12.05.2012)
         */
        @Override
        public String toString() {
            return "Location";
        }
    },
    /**
     * Null value for switch case compatibility
     */
    NULL;
    
    /**
     * Returns all necessary column table values using their string representation
     * @return a {@link String} array of names
     * @author Malte Brunnlieb (12.05.2012)
     */
    public static String[] tableColumnsToString() {
        Column[] columns = values();
        String[] result = new String[columns.length - 1];
        for (int i = 0; i < columns.length - 1; i++) {
            result[i] = columns[i].toString();
        }
        return result;
    }
    
    /**
     * Returns the {@link Column} represented by the given name.
     * @param name of the {@link Column}
     * @return the {@link Column} which is represented by the given name or null if no {@link Column} was found
     * @author Malte Brunnlieb (07.06.2012)
     */
    public static Column get(String name) {
        for (Column c : values()) {
            if (c.toString().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
