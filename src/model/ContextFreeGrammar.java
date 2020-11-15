package model;

import java.util.HashMap;
import java.util.HashSet;

public class ContextFreeGrammar {
    /**
     * This constant represents the initial character of the grammar
     */
    public final static Character START = 'S';
    /**
     * This constant represents the lambda character used in a grammar
     */
    public final static String LAMBDA = "";
    /**
     * This character hashSet represents the identified grammar variables
     */
    private HashSet<Character> variables;
    /**
     * This character hashSet represents the identified grammar terminals
     */
    private HashSet<Character> terminals;
    /**
     * This character hashMap and string hashSet represents the identified production rules
     */
    private HashMap<Character, HashSet<String>> productionRules;

    /**
     * This function creates a new context-free Grammar
     */
    public ContextFreeGrammar() {
        variables = new HashSet<>();
        terminals = new HashSet<>();
        productionRules = new HashMap<>();
        addVariable(START);
    }

    /**
     * Thie method add a variable to the production rule
     * @param  var represents a possible character to variable
     * @return True if the variable was valid and was not already registered
     * @throws NullPointerException     if var is null
     */
    public boolean addVariable(Character var) {
        if(var == null) {
            throw new NullPointerException();
        }
        if (Character.isAlphabetic(var) && Character.isUpperCase(var)) {
            boolean added = variables.add(var);
            if (added) {
                productionRules.put(var, new HashSet<>());
            }
            return added;
        }
        return false;
    }

    /**
     * This method add a terminal to the list of terminals
     * @param term  represents a possible character to terminal
     * @return True if the variable was terminal and was not already registered
     * @throws NullPointerException     if var is null
     */
    public boolean addTerminal(Character term) {
        if (term == null) {
            throw new NullPointerException("null cannot be a terminal");
        }
        if (!Character.isAlphabetic(term) || !Character.isUpperCase(term)) { //any character can be a terminal, except uppercase letters
            return terminals.add(term);
        }
        return false;
    }

    /**
     * Associates the specified body to a new production of the head variable
     * @param head The variable that produces the body
     * @param body If body is the empty string then lambda is assumed. body must not be null
     * @return True if the production was added, false otherwise
     */
    public boolean addProductionRule(Character head, String body) {
        if (!variables.contains(head)) {
            addVariable(head);
        }
        addVariablesAndTerminals(body); //register everything present in the body
        return productionRules.get(head).add(body);
    }

    /**
     *try to add as variable and terminal as a maximum of one method will actually add the character to a set
     * @param string represents a character that is added to variable and terminal
     */
    public void addVariablesAndTerminals(String string) {
        for (int i = 0; i < string.length(); i++) {
            Character c = string.charAt(i);
            addVariable(c);
            addTerminal(c);
        }
    }

    /**
     * @return the character hashset of variables in context-free grammar
     */
    public HashSet<Character> getVariables() {
        return variables;
    }

    /**
     * @return the terminal character hashset in context-free grammar
     */
    public HashSet<Character> getTerminals() {
        return terminals;
    }

    /**
     * @return the character hashmap and string hashset of the production rules in context-free grammar
     */
    public HashMap<Character, HashSet<String>> getProductionRules() {
        return productionRules;
    }

    /**
     * @return a string with all the grammar that has been generated with the different parameters
     */
    @Override
    public String toString() {
        String output = "";
        for (Character head : variables) {
            String line = head + " ->";
            HashSet<String> bodies = productionRules.get(head);
            for (String body : bodies) {
                line += (line.endsWith(">") ? " " : " | ") + " " + (body.equals(LAMBDA) ? "''" : body);
            }
            if(head != START) {
                output += line + "\n";
            } else {
                output = line + "\n" + output;
            }
        }
        return output.trim();
    }
}