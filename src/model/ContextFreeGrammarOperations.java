package model;

import java.util.HashMap;
import java.util.HashSet;

public class ContextFreeGrammarOperations {
    /**
     * used to keep track of newly inserted variables in CNF conversion
     */
    private static char newVar;
    /**
     * This cykMatrix represents the resulting matrix after applying the CYK algorithm to a grammar
     */
    private HashSet<Character>[][] cykMatrix;

    /**
     * Reads a grammar from a given string.
     * Please use S as the start variable and do not use the last letters from the alphabet (Z, Y, X...) as they are
     * used as new variables in the process of CNF conversion
     *
     * @param source The string representation of the grammar which is similar to the format of {@link ContextFreeGrammar}.toString()
     * @return A context free grammar representation of source string
     * @throws IllegalArgumentException if the format of input string source is incorrect
     */
    public ContextFreeGrammar read(String source) throws IllegalArgumentException {
        ContextFreeGrammar cfg = new ContextFreeGrammar();
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line != null && !line.trim().isEmpty()) {
                line = line.replace(" ", "");
                String[] parts = line.split("->");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("The line is in an invalid format : " + line);
                }
                if (parts[0].length() != 1) {
                    throw new IllegalArgumentException("CFG grammars must have productions like A -> w, where A is a variable and w is a string");
                }
                char head = parts[0].charAt(0);
                if (head < 'A' || head > 'Z') {
                    throw new IllegalArgumentException("var must be an uppercase letter: " + head);
                }
                cfg.addVariable(head);
                String[] productions = parts[1].split("\\|");
                for (int j = 0; j < productions.length; j++) {
                    String prod = productions[j].trim().replace("''", ContextFreeGrammar.LAMBDA); // translate '' into lambda
                    cfg.addProductionRule(head, prod);
                }
            }
        }
        return cfg;
    }

    /**
     * Validates whether the given grammar produces the specified String
     *
     * @param cfg A context-free grammar that is assumed to be in CNF.
     * @param w The String that wants to be validated. It is assumed that the String contains only terminals or is lambda
     * @return true if the grammar produces the given String, false otherwise
     */
    public boolean CYK(ContextFreeGrammar cfg, String w) {
        if (w.equals(ContextFreeGrammar.LAMBDA)) { // if it is lambda
            return cfg.getProductionRules().get(ContextFreeGrammar.START).contains(w);
        }
        int n = w.length();
        cykMatrix = new HashSet[n][n];
        // Initialize
        for (int i = 0; i < n; i++) {
            cykMatrix[i][0] = new HashSet<>();
            checkBodyInProduction(cfg, cykMatrix, w.charAt(i) + "", i, 0);
        }
        // Repeat until j = n
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                cykMatrix[i][j] = new HashSet<>();
                for (int k = 0; k <= j - 1; k++) {
                    HashSet<Character> B = cykMatrix[i][k]; // Xik
                    HashSet<Character> C = cykMatrix[i + k + 1][j - k - 1]; // X(i+k),(j-k)
                    String[] prods = cartesianProduct(B, C);
                    for (String body : prods) { // Xij := A in V / A --> BC is a production of G
                        checkBodyInProduction(cfg, cykMatrix, body, i, j);
                    }
                }

            }
        }
        return cykMatrix[0][n - 1].contains(ContextFreeGrammar.START);
    }

    /**
     * This method checks if the grammar is in body production
     * @param cfg A context-free grammar that is assumed to be in CNF.
     * @param cykMatrix represents the resulting matrix after applying the CYK algorithm to a grammar
     * @param w The String that wants to be validated. It is assumed that the String contains only terminals or is lambda
     * @param i represents the value of the matrix row
     * @param j represents the value of the matrix column
     */
    private void checkBodyInProduction(ContextFreeGrammar cfg, HashSet<Character>[][] cykMatrix, String w, int i, int j) {
        for (char head : cfg.getVariables()) { // check the variables
            for (String body : cfg.getProductionRules().get(head)) { // and their productions
                if (body.equals(w)) { // to see if they produce w
                    cykMatrix[i][j].add(head);
                    break;
                }
            }
        }
    }

    /**
     * This method realizes the cartesian product between two characters
     * @param B represents the first value of a character hashset
     * @param C represents the second value of a character hashset
     * @return the cartesian product between the two characters
     */
    private String[] cartesianProduct(HashSet<Character> B, HashSet<Character> C) {
        String[] cp = new String[B.size() * C.size()];
        int i = 0;
        for (Character b : B) {
            for (Character c : C) {
                cp[i] = b.toString() + c.toString();
                i++;
            }
        }
        return cp;
    }

    /**
     * This method removes the terminal variables from the grammar entered by the user
     * @param cfg A user-typed context free grammar
     * @return context-free grammar are terminal variables
     */
    private ContextFreeGrammar removeNonTerminalVariables(ContextFreeGrammar cfg) {
        HashSet<Character> term = new HashSet<>();
        HashSet<Character> nonTerm = (HashSet<Character>) cfg.getVariables().clone(); // it is nonterm only after finding all terminal variables
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        // Initialize
        for (Character var : nonTerm) {
            for (String prod : originalProductionRules.get(var)) {
                boolean containsOnlyTerminals = true;
                for (int i = 0; i < prod.length() && containsOnlyTerminals; i++) {
                    containsOnlyTerminals = cfg.getTerminals().contains(prod.charAt(i));
                }
                if (containsOnlyTerminals) {
                    term.add(var);
                    break;
                }
            }
        }
        // Repeat until term does not change
        int previousSize = 0;
        while (previousSize < term.size()) {
            previousSize = term.size();
            for (Character var : nonTerm) {
                HashSet<String> prods = originalProductionRules.get(var);
                for (String prod : prods) {
                    for (Character p : term) {
                        prod = prod.replace(p.toString(), "");
                    }
                    for (Character ter : cfg.getTerminals()) {
                        prod = prod.replace(ter.toString(), "");
                    }
                    if (prod.isEmpty()) {
                        term.add(var);
                        break;
                    }
                }
            }
        }
        // Remove non-terminal variables
        nonTerm.removeAll(term);
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        for (Character var : term) {
            cfg.addVariable(var);
            HashSet<String> prods = originalProductionRules.get(var);
            for (String body : prods) {
                boolean safe = true;
                for (Character ver : nonTerm) {
                    if (body.contains(ver.toString())) { // do not add productions that contain non-terminal variables
                        safe = false;
                        break;
                    }
                }
                if (safe) {
                    newCfg.addProductionRule(var, body);
                }
            }
        }
        return newCfg;
    }

    /**
     * This method removes the unreachable variables from the user-entered grammar
     * @param cfg A user-typed context free grammar
     * @return context-free grammar without unreachable variables
     */
    private ContextFreeGrammar removeNonReachableVariables(ContextFreeGrammar cfg) {
        HashSet<Character> alc = new HashSet<>();
        HashSet<Character> variables = cfg.getVariables();
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        // Initialize
        alc.add(ContextFreeGrammar.START);
        // Repeat until alc does not change
        int previousSize = 0;
        while (previousSize < alc.size()) {
            previousSize = alc.size();
            for (Character var : (HashSet<Character>) alc.clone()) {
                HashSet<String> prods = originalProductionRules.get(var);
                for (String prod : prods) {
                    for (int i = 0; i < prod.length(); i++) {
                        char p = prod.charAt(i);
                        if (variables.contains(p)) {
                            alc.add(p);
                        }
                    }
                }
            }
        }
        // Remove non-reachable variables
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        for (Character var : alc) {
            cfg.addVariable(var);
            HashSet<String> prods = originalProductionRules.get(var);
            for (String body : prods) {
                newCfg.addProductionRule(var, body);
            }
        }
        return newCfg;
    }

    /**
     * This method removes the lambda productions from the user-entered grammar
     * @param cfg A user-typed context free grammar
     * @return returns context-free grammar without lambda productions
     */
    private ContextFreeGrammar removeLambdaProductions(ContextFreeGrammar cfg) {
        HashSet<Character> anul = new HashSet<>();
        HashSet<Character> variables = (HashSet<Character>) cfg.getVariables().clone();
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        HashMap<Character, HashSet<String>> newProductionRules = new HashMap<>();
        originalProductionRules.forEach((head, bodies) -> newProductionRules.put(head, (HashSet<String>) bodies.clone()));
        // Initialize
        for (Character var : variables) {
            if (newProductionRules.get(var).contains(ContextFreeGrammar.LAMBDA)) {
                anul.add(var);
            }
        }
        // Repeat until anul does not change
        int previousSize = 0;
        while (previousSize < anul.size()) {
            previousSize = anul.size();
            for (Character var : variables) {
                HashSet<String> prods = newProductionRules.get(var);
                for (String prod : prods) {
                    for (Character p : anul) {
                        prod = prod.replace(p.toString(), "");
                    }
                    if (prod.isEmpty()) {
                        anul.add(var);
                        break;
                    }
                }
            }
        }
        // Simulate every lambda production
        int productions = 0;
        for (Character var : variables) {
            productions += newProductionRules.get(var).size();
        }
        previousSize = 0;
        while (previousSize < productions) {
            previousSize = productions;
            for (Character var : variables) {
                HashSet<String> prods = newProductionRules.get(var);
                productions -= prods.size();
                for (String prod : (HashSet<String>) prods.clone()) {
                    for (Character p : anul) {
                        simulateNullableVariablesInProductions(var, prod, p, newProductionRules);
                        if (var != ContextFreeGrammar.START) {
                            newProductionRules.get(var).remove(ContextFreeGrammar.LAMBDA);
                        }
                    }
                }
                productions += newProductionRules.get(var).size();
            }
        }
        // Create new CFG with the resulting productions and return it
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        newProductionRules.forEach((head, bodies) -> {
            newCfg.addVariable(head);
            bodies.forEach(body -> newCfg.addProductionRule(head, body));
        });
        return newCfg;
    }

    /**
     * This method simulates variables that accept null values in productions
     * @param head The variable that produces the body
     * @param body If body is the empty string then lambda is assumed. body must not be null
     * @param nullable the possible character that is nullable
     * @param productionRules This character hashMap and string hashSet represents the identified production rules
     */
    private void simulateNullableVariablesInProductions(Character head, String body, Character nullable, HashMap<Character, HashSet<String>> productionRules) {
        int lastIndex = body.indexOf(nullable);
        while (lastIndex != -1) {
            productionRules.get(head).add(new StringBuilder(body).deleteCharAt(lastIndex).toString());
            lastIndex = lastIndex < body.length() - 1 ? body.substring(lastIndex + 1).indexOf(nullable) : -1;
        }
    }

    /**
     * This method removes unit productions from context-free grammar
     * @param cfg A user-typed context free grammar
     * @return context-free grammar after removing unit productions
     */
    private ContextFreeGrammar removeUnitaryProductions(ContextFreeGrammar cfg) {
        HashSet<Character> variables = (HashSet<Character>) cfg.getVariables().clone();
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        originalProductionRules.forEach((head, bodies) -> {
            newCfg.addVariable(head);
            newCfg.getProductionRules().get(head).remove(head.toString()); //remove unitary self production
            bodies.forEach(body -> {
                simulateUnitaryProductions(newCfg, head, body, variables, originalProductionRules);
            });
        });
        //after that there are not unitary productions, but maybe there are variables without productions, delete them
        variables.forEach(var -> {
            if (newCfg.getProductionRules().get(var).isEmpty()) {
                newCfg.getVariables().remove(var);
            }
        });
        return newCfg;
    }

    /**
     * This method simulates the grammar entered by the user in a unit production
     * @param cfg A user-typed context free grammar
     * @param head The variable that produces the body
     * @param body If body is the empty string then lambda is assumed. body must not be null
     * @param variables This character hashSet represents the identified grammar variables
     * @param productionRules This character hashMap and string hashSet represents the identified production rules
     */
    private void simulateUnitaryProductions(ContextFreeGrammar cfg, Character head, String body, HashSet<Character> variables, HashMap<Character, HashSet<String>> productionRules) {
        if (body.length() == 1 && variables.contains(body.charAt(0)) && body.charAt(0) != head) { //if it is a unitary production and it is not self production
            for (String prod : productionRules.get(body.charAt(0))) { //simulate it
                if (prod.length() == 1 && prod.charAt(0) == body.charAt(0)) {
                    char p2 = prod.charAt(0);
                    productionRules.get(p2).forEach(body2 -> {
                        simulateUnitaryProductions(cfg, p2, body2, variables, productionRules);
                    });
                    return; // Avoid operating self productions
                }
                cfg.addProductionRule(head, prod);
            }
        } else {
            cfg.addProductionRule(head, body);
        }
    }

    /**
     * This method makes binary and simple productions
     * @param cfg A context-free grammar that is assumed to be in CNF.
     * @return a context-free grammar with built-in simple and binary productions
     */
    private ContextFreeGrammar makeProductionsBinaryAndSimple(ContextFreeGrammar cfg) {
        HashSet<Character> terminals = (HashSet<Character>) cfg.getTerminals().clone();
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        HashMap<String, Character> auxProdToVar = new HashMap<>();
        // Add new variables for each terminal
        newVar = 'Z';
        for (Character term : terminals) {
            newCfg.addVariable(newVar);
            newCfg.addProductionRule(newVar, term.toString());
            auxProdToVar.put(term.toString(), newVar);
            newVar--;
        }
        // Replace terminals in productions with their new simple variables that produce one terminal each
        originalProductionRules.forEach((head, bodies) -> {
            newCfg.addVariable(head);
            ((HashSet<String>) bodies.clone()).forEach(prod -> {
                if (prod.length() > 1) {
                    for (int i = 0; i < prod.length(); i++) {
                        String c = prod.charAt(i) + "";
                        if (auxProdToVar.containsKey(c)) {
                            prod = prod.replace(c, auxProdToVar.get(c).toString());
                        }
                    }
                }
                newCfg.addProductionRule(head, prod);
            });
        });
        ((HashMap<Character, HashSet<String>>) newCfg.getProductionRules().clone()).forEach((head, bodies) -> {
            ((HashSet<String>) bodies.clone()).forEach(body -> {
                makeBinary(newCfg, head, body, auxProdToVar);
            });
        });
        // if the grammar was already in CNF then some of the variables added in this method will be non-reachable, this removes them
        return removeNonReachableVariables(newCfg);
    }

    /**
     * This method do the grammar in binary
     * @param cfg A user-typed context free grammar
     * @param head The variable that produces the body
     * @param oldBody If old body is the empty string then lambda is assumed. body must not be null
     * @param prodToVariable represents a hashmap that returns a production and is passed to a variable
     */
    private void makeBinary(ContextFreeGrammar cfg, Character head, String oldBody, HashMap<String, Character> prodToVariable) {
        if (head == null) {
            head = newVar;
            newVar--;
        }
        if (oldBody.length() > 2) { //if it can be shortened
            String firstTwo = oldBody.substring(0, 2);
            String newBody = "";
            if (prodToVariable.containsKey(firstTwo)) {
                newBody = prodToVariable.get(firstTwo).toString();
            }
            if (newBody.isEmpty()) { // No auxiliary produced the first two digits concatenated
                cfg.addVariable(newVar);
                cfg.addProductionRule(newVar, firstTwo);
                prodToVariable.put(firstTwo, newVar);
                newBody = newVar + "";
                newVar--;
            }
            if (oldBody.length() == 3) { //
                newBody += oldBody.charAt(2);
            } else {
                String remainder = oldBody.substring(2);
                makeBinary(cfg, null, remainder, prodToVariable);
                newBody += prodToVariable.get(remainder);
            }
            cfg.addProductionRule(head, newBody);
            cfg.getProductionRules().get(head).remove(oldBody);
        } else {
            cfg.addProductionRule(head, oldBody);
            if(head == newVar + 1) { // head came null in the argument
                prodToVariable.put(oldBody, head);
            }
        }
    }

    /**
     * This method converts the grammar entered by the user to a grammar in chomsky normal form
     * @param cfg A user-typed context free grammar
     * @return chomsky's grammar in normal form
     */
    public ContextFreeGrammar convertToCNF(ContextFreeGrammar cfg) {
        cfg = removeNonTerminalVariables(cfg);
        cfg = removeNonReachableVariables(cfg);
        cfg = removeLambdaProductions(cfg);
        cfg = removeUnitaryProductions(cfg);
        cfg = makeProductionsBinaryAndSimple(cfg);
        return cfg;
    }

    /**
     * @return the resulting matrix after applying the c and k algorithm on a grammar
     */
    public HashSet<Character>[][] getCykMatrix() {
        return cykMatrix;
    }
}
