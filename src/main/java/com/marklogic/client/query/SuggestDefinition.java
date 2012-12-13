package com.marklogic.client.query;

public interface SuggestDefinition {

	 /**
     * Returns the name of the query options used for this query.
     * @return The options name.
     */
    public String getOptionsName();

    /**
     * Sets the name of the query options to be used for this query.
     *
     * If no query options node with the specified name exists, the search will fail.
     *
     * @param name The name of the saved query options node on the server.
     */
    public void setOptionsName(String name);
    
    /**
     * Sets one or more criteria for the suggestion call.  
     * 
     * If there are no strings, the server will provide suggestions for
     * the empty string.
     * 
     * @param qtext A string for input to suggestions, and zero or more
     * string queries to qualify that input string.
     */
    public void setStringCriteria(String[] qtext);
    
    
    /**
     * Returns the array of strings set for this SuggestDefinition.
     * @return The query text strings and suggestion input.
     */
    public String[] getStringCriteria();
    
    /**
     * Sets a limit for a suggest call.  Only this number of suggestions
     * will be returned by the server.  Server default is 10.
     * 
     * @param limit The maximum number of suggestions to fetch.
     */
    public void setLimit(Integer limit);
    
    /**
     * Returns the maximum number of suggestions to fetch.
     * @return The limit.
     */
    public Integer getLimit();
    
    /**
     * Sets the cursor position to use in the suggest call.
     * @param cursorPosition The cursor position.
     */
    public void setCursorPosition(Integer cursorPosition);
    
    /**
     * Returns the cursor position for the suggest call.
     * @return The cursor position
     */
    public Integer getCursorPosition();
    
    /**
     * Sets the index for which query text is a suggestion input, as opposed to a qualifying search string.
     * @param focus
     */
    public void setFocus(Integer focus);
    
    /**
     * Returns index to which query text array element is input for suggestions
     */
    public Integer getFocus();
    
}
