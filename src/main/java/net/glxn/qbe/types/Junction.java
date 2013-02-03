package net.glxn.qbe.types;

/**
 * JunctionType
 * Specifies conjunction({@link Junction#UNION}) or disjunction({@link Junction#INTERSECTION})
 */
public enum Junction {
    /**
     * conjunction, as in where something AND something else
     */
    UNION,
    /**
     * disjunction, as in where something OR something else
     */
    INTERSECTION
}
