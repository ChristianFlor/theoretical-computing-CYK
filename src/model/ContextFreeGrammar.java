package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ContextFreeGrammar {
    public static final String DATA_PATH = "./data/answers.txt";
    public final static Character START = 'S';
    public final static String LAMBDA = "";
    private HashSet<Character> variables;
    private HashSet<Character> terminals;

    private HashMap<Character, HashSet<String>> productionRules;

    public ContextFreeGrammar() {
        variables = new HashSet<>();
        terminals = new HashSet<>();
        productionRules = new HashMap<>();
        addVariable(START);
    }

    /**
     * @return True if the variable was valid and was not already registered
     * @throws NullPointerException     if var is null
     * @throws IllegalArgumentException When var is not an uppercase letter
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
     *
     * @param head The variable that produces the body
     * @return True if the production was added, false otherwise
     * @throws IllegalArgumentException when T, which is a variable that can only be used by the grammar internally, is tried to be added manually (in the body)
     * @paran body If body is the empty string then lambda is assumed. body must not be null
     */
    public boolean addProductionRule(Character head, String body) {
        if (!variables.contains(head)) {
            addVariable(head);
        }
        if (body.contains("T")) {
            throw new IllegalArgumentException("T is a character that can only be used by the grammar to perform CNF conversion");
        }
        addVariablesAndTerminals(body); //register everything present in the body
        return productionRules.get(head).add(body);
    }

    public void addVariablesAndTerminals(String string) {
        for (int i = 0; i < string.length(); i++) {
            Character c = string.charAt(i);
            //try to add as variable and terminal as a maximum of one method will actually add the character to a set
            addVariable(c);
            addTerminal(c);
        }
    }

    public HashSet<Character> getVariables() {
        return variables;
    }

    public HashSet<Character> getTerminals() {
        return terminals;
    }

    public HashMap<Character, HashSet<String>> getProductionRules() {
        return productionRules;
    }

    @Override
    public String toString() {
        String output = "";
        for (Character head : variables) {
            String line = head + " ->";
            HashSet<String> bodies = productionRules.get(head);
            for (String body : bodies) {
                line += (line.endsWith(">") ? " " : " | ") + " " + (body.equals(LAMBDA) ? "''" : body);
            }
            output += line + "\n";
        }
        return output;
    }
}
