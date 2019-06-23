package com.company.commandline;

import java.util.ArrayList;
import java.util.List;

/**
 * This is responsible for parsing the main commandline parameters
 *
 * @author mathew
 */
public class ParameterParser {
    /**
     * Contains processed parameters when processing is done
     */
    private List<Parameter> consoleParameters;

    /**
     * Instantiates a new Parameter parser.
     */
    public ParameterParser() {
        consoleParameters = new ArrayList<Parameter>();
    }

    /**
     * Add a parameter to be parsed
     *
     * @param p the parameter to be added
     * @return this, so multiple arguments can be added in the same
     * statement
     */
    public ParameterParser add(Parameter p){
        consoleParameters.add(p);
        return this;
    }

    /**
     * Sets a specific parameter
     * @param paramID parameter to be set
     * @param value value to be set
     * @return whether opr not operation is in the parameter list
     */
    private boolean setParamByID(String paramID, String value) {

        for (Parameter param : consoleParameters) {
            if (param.getID().equals(paramID)) {
                param.setValue(value);
                return true;
            }
        }
        //ID was not in parameter list
        return false;
    }

    /**
     * Gets parameter by ID
     * Eg. '-cca'
     *
     * @param ID the id to be checked against list
     * @return the parameter
     */
    public Parameter getID(String ID) {
        for (Parameter p : consoleParameters) {
            if (p.getID().equals(ID)) {
                return p;
            }
        }
        return null;
    }

    /**
     *
     * @return Whether or not the all the essential parameters were filled
     */
    private boolean paramsFufilled() {
        boolean emptyEssential = false;

        for (Parameter p : consoleParameters) {
            if (p.isEssential() && p.isEmpty()) {
                System.out.println("Missing value for parameter: " + p.getID());
                emptyEssential = true;
            }
        }
        return !emptyEssential;
    }

    /**
     * Removes any unknown arguments, or any strings that are in the wrong format
     * @param arguments to be cleaned
     * @return nicely formatted arguments
     */
    private List<String> cleanUpArguments(String[] arguments) {
        List<String> validArguments = new ArrayList<String>();
        boolean expectingID = true;

        for (String arg : arguments) {
            boolean isIDParam = arg.matches("-([A-Za-z])([A-Za-z0-9])*");

            if (isIDParam && expectingID) {
                validArguments.add(arg.substring(1));
                expectingID = false;
            } else if (expectingID) {
                System.out.println("Ignoring unexpected token: " + arg);
            } else if (isIDParam) {

                System.out.println("Ignoring unmatched value for: " + validArguments.remove(validArguments.size() - 1));
                validArguments.add(arg.substring(1));
            } else {
                validArguments.add(arg);
                expectingID = true;
            }
        }
        return validArguments;
    }

    /**
     * Separates the parameter IDs from the values
     * @param validArguments
     */
    private void addParameters(List<String> validArguments) {
        for (int i = 0; i < validArguments.size(); i += 2) {
            if (!setParamByID(validArguments.get(i), validArguments.get(i + 1))) {
                System.out.println("Ignoring unknown parameter ID: " + validArguments.get(i));
            }
        }
    }

    /**
     * Parse parameters boolean.
     *
     * @param arguments the arguments
     * @return the boolean
     */
    public boolean parseParameters(String[] arguments) {

        List<String> validArguments = cleanUpArguments(arguments);
        addParameters(validArguments);

        return paramsFufilled();
    }
}
