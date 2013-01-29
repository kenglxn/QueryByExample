package net.glxn.qbe;

/**
 * Designates the type of matching semantics will be used in the query criteria. <br/>
 * E.g. equals PART, like %PART, like PART%, like %PART%<br/>
 * <br/>
 * See:<br/>
 * * {@link MatchType#EXACT}<br/>
 * * {@link MatchType#START}<br/>
 * * {@link MatchType#END}<br/>
 * * {@link MatchType#MIDDLE}<br/>
 */
public enum MatchType {
    /**
     * Use exact match, i.e. equals
     */
    EXACT,
    /**
     * Match the start of a string. <br/>
     * In other words, use like match with ending wildcard, i.e. like PART%
     */
    START,
    /**
     * Match the end of a string. <br/>
     * In other words, use like match with starting wildcard, i.e. like %PART
     */
    END,

    /**
     * Match the middle of a string. <br/>
     * In other words, use like match with starting AND ending wildcard, i.e. like %PART%
     */
    MIDDLE
}
