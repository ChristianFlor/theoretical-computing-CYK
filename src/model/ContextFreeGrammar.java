package model;

import java.util.HashMap;
import java.util.HashSet;

public class ContextFreeGrammar {
    public static final String DATA_PATH = "./data/answers.txt";
    public final static Character start = 'S';
    private HashSet<Character> variables;
    private HashSet<Character> terminals;
    private HashMap<Character, HashSet<String>> productionRules;

    public ContextFreeGrammar() {
        variables = new HashSet<>();
        terminals = new HashSet<>();
        productionRules = new HashMap<>();
        addVariable(start);
    }
    public ContextFreeGrammar(String src) {

    }
    /**
     * @return True if the variable was not
     * @throws NullPointerException if var is null
     * */
    public boolean addVariable(Character var) {
        if(Character.isAlphabetic(var) && Character.isUpperCase(var)) {
            addProduction(var, var.toString()); //self-production of a variable is trivial, but it is still present
            return variables.add(var);
        }
        return false;
    }

    public boolean addTerminal(Character term) {
        if(Character.isAlphabetic(term) && Character.isLowerCase(term)) {
            return terminals.add(term);
        }
        return false;
    }

    /** Associates the specified body to a new production of the head variable
     * @param  head head exists in the variables set or alphabet
     * @return True if the production was added, false otherwise
     * */
    public boolean addProduction(Character head, String body) {
        if(!variables.contains(head)) {
            return false; //cannot add a production to a nonexistent variable, use addVariable(head) prior to trying again
        }
        if(!productionRules.containsKey(head)) {
            productionRules.put(head, new HashSet<>());
        }
        return productionRules.get(head).add(body);
    }

    public void addVariablesAndTerminals(String string) {
        for(int i = 0; i < string.length(); i++) {
            Character c = string.charAt(i);
            //try to add as variable and terminal as a maximum of one method will actually add the character to a set
            addVariable(c);
            addTerminal(c);
        }
    }
}
