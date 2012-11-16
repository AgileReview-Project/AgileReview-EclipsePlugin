package org.agilereview.editorparser.itexteditor.data;

import org.eclipse.jface.text.Position;

/**
 * Wrapper for {@link Position} in order to get a comparable Position
 */
public class ComparablePosition extends Position implements Comparable<Position> {
    
    /**
     * Creates a new ComparablePosition
     * @param p {@link Position} which the new ComparablePosition should represent
     */
    public ComparablePosition(Position p) {
        super(p.offset, p.length);
    }
    
    /**
     * Compares two ComparablePositions based on their offsets
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Position arg0) {
        if (arg0 instanceof ComparablePosition) {
            if (offset < ((ComparablePosition) arg0).offset) {
                return -1;
            } else if (offset > ((ComparablePosition) arg0).offset) { return 1; }
        }
        return 0;
    }
}