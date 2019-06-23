package com.company.commandline;

public class Parameter{
    /**
     * Whether or not the value can be left out or not
     */
    final private boolean ESSENTIAL;
    final private String IDENTIFIER;
    private String value = null;

    /**
     * Instantiates a new Parameter.
     *
     * @param identifier the identifier
     * @param essential  the essential
     */
    public Parameter(String identifier, boolean essential){
        ESSENTIAL = essential;
        IDENTIFIER = identifier;
    }

    /**
     * Get id string.
     *
     * @return the string
     */
    public String getID(){
        return IDENTIFIER;
    }

    /**
     * Set value.
     *
     * @param val the value
     */
    public void setValue(String val){
        this.value = val;
    }

    /**
     * Get value string.
     *
     * @return the string
     */
    public String getValue(){
        return this.value;
    }

    /**
     * Is essential boolean.
     *
     * @return the boolean
     */
    public boolean isEssential(){
        return ESSENTIAL;
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty(){
        return (value == null);
    }

    /**
     * Converts the captured value to an integer
     *
     * @return the integer
     */
    public Integer parseInteger(){
        if(value != null){
            try{
                Integer ret = Integer.parseInt(value);
                return ret;
            }
            catch(Exception e){
                System.out.println("Invalid format for parameter [-"+IDENTIFIER+"]: \""+value+"\" is not an integer.");
            }
        }

        return null;
    }



}