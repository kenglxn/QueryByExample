package net.glxn.qbe;

/**
 * JunctionType
 * Specifies conjunction({@link JunctionType#AND}) or disjunction({@link JunctionType#OR})
 */
public enum JunctionType {
    /**
     * conjunction, as in where something AND something else
     */
    AND,
    /**
     * disjunction, as in where something OR something else
     */
    OR
}
