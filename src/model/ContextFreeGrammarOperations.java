package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ContextFreeGrammarOperations {
    // TODO The method has unnecessary validations that are already done by propieatary methods of CFG.
    // TODO All in english
    // TODO Exception is too general
    // FIXME Take into account the above comments
    public ContextFreeGrammar read(String source) throws Exception {
        ContextFreeGrammar cfg = new ContextFreeGrammar();
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line != null && !line.trim().isEmpty()) {
                line = line.replace(" ", "");
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    throw new Exception("Regla sin el formato : " + line);
                }

                if (parts[0].length() != 1) {
                    throw new Exception("La parte izquierda de una regla debe ser un caracter");
                }

                char head = parts[0].charAt(0);
                if (head < 'A' || head > 'Z') {
                    throw new Exception("El generador debe ser una letra mayuscula");
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
     * @param w   The String that wants to be validated. It is assumed that the String contains only terminals or is lambda
     * @return true if the grammar produces the given String, false otherwise
     */
    public boolean CYK(ContextFreeGrammar cfg, String w) {
        if(w.equals(ContextFreeGrammar.LAMBDA)) { // if it is lambda
            return cfg.getProductionRules().get(ContextFreeGrammar.START).contains(w);
        }
        int n = w.length();
        HashSet<Character>[][] cykMatrix = new HashSet[n][n];
        // Initialize
        for (int i = 0; i < n; i++) {
            cykMatrix[i][0] = new HashSet<>();
            checkBodyInProduction(cfg, cykMatrix, w.charAt(i)+"", i, 0);
        }
        // Repeat until j = n
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                cykMatrix[i][j] = new HashSet<>();
                for (int k = 0; k <= j - 1; k++) {
                    HashSet<Character> B = cykMatrix[i][k]; // Xik
                    HashSet<Character> C = cykMatrix[i + k + 1][j - k - 1]; // X(i+k),(j-k)
                    String[] prods = cartesianProduct(B, C);
                    for(String body : prods) { // Xij := A in V / A --> BC is a production of G
                        checkBodyInProduction(cfg, cykMatrix, body, i, j);
                    }
                }
            }
        }
        return cykMatrix[0][n-1].contains(ContextFreeGrammar.START);
    }

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

    public ContextFreeGrammar removeNonTerminalVariables(ContextFreeGrammar cfg) {
        HashSet<Character> term = new HashSet<>();
        HashSet<Character> nonTerm = (HashSet<Character>) cfg.getVariables().clone(); // it is nonterm only after finding all terminal variables
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        // Initialize
        for(Character var : nonTerm) {
            for(String prod : originalProductionRules.get(var)) {
                boolean containsOnlyTerminals = true;
                for(int i = 0; i < prod.length() && containsOnlyTerminals; i++) {
                    containsOnlyTerminals = cfg.getTerminals().contains(prod.charAt(i));
                }
                if(containsOnlyTerminals) {
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
                        prod.replace(p.toString(), "");
                    }
                    for(Character ter : cfg.getTerminals()) {
                        prod.replace(ter.toString(), "");
                    }
                    if (prod.isEmpty()) {
                        term.add(var);
                        break;
                    }
                }
            }
        }
        // Remove non-terminal variables
        nonTerm.remove(term);
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        for(Character var : term) {
            cfg.addVariable(var);
            HashSet<String> prods = originalProductionRules.get(var);
            for(String body : prods) {
                boolean safe = true;
                for(Character ver : nonTerm) {
                    if(body.contains(ver.toString())) { // do not add productions that contain non-terminal variables
                        safe = false;
                        break;
                    }
                }
                if(safe) {
                    newCfg.addProductionRule(var, body);
                }
            }
        }
        return newCfg;
    }

    public ContextFreeGrammar removeNonReachableVariables(ContextFreeGrammar cfg) {
        HashSet<Character> alc =new HashSet<>() ; alc.add(ContextFreeGrammar.LAMBDA.charAt(0));
        HashSet<Character> analize = new HashSet<>();
        boolean change = true;
        while(change){
            HashSet<Character> alcInit =new HashSet<>(alc) ;
            for (Character c : alc) {
                if(analize.contains(c) == false){
                    HashMap<Character, HashSet<String>> newProductionRules = cfg.getProductionRules();
                    if(newProductionRules != null){
                        for (String prod : cfg.getProductionRules().get(c)) {
                            //HashSet<Character> lista = prod.equals(c.equals(Character.isUpperCase(c)));
                            //alc = alc.addAll(lista.clone().toString().charAt());
                        }

                    }
                    analize.add(c);
                }
            }
            if(alcInit.size() == alc.size()){
                change = false;
            }
        }

        return null;
    }

    public ContextFreeGrammar removeLambdaProductions(ContextFreeGrammar cfg) {
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
                        prod.replace(p.toString(), "");
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
                HashSet<String> prods = (HashSet<String>) newProductionRules.get(var).clone();
                productions -= prods.size();
                for (String prod : prods) {
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
            cfg.addVariable(head);
            bodies.forEach(body -> cfg.addProductionRule(head, body));
        });
        return newCfg;
    }

    private void simulateNullableVariablesInProductions(Character head, String body, Character nullable, HashMap<Character, HashSet<String>> productionRules) {
        int lastIndex = body.indexOf(nullable);
        while (lastIndex != -1) {
            productionRules.get(head).add(new StringBuilder(body).deleteCharAt(lastIndex).toString());
            lastIndex = lastIndex < body.length() - 1 ? body.substring(lastIndex + 1).indexOf(nullable) : -1;
        }
    }

    private void simulateUnitaryProductions(ContextFreeGrammar cfg, Character head, HashMap<Character, HashSet<String>> productionRules) {
        for (String prod : productionRules.get(head)) {
            if (prod.length() == 1 && cfg.getVariables().contains(prod.charAt(0))) { //if it is a unitary production
                for (String prod2 : productionRules.get(prod.charAt(0))) { //simulate it
                    if (prod2.length() == 1 && cfg.getVariables().contains(prod2.charAt(0))) { //recursive call to simulate bodies of secondary unitary production (i.e A --> B --> C)
                        simulateUnitaryProductions(cfg, prod2.charAt(0), productionRules);
                    } else {
                        cfg.addProductionRule(head, prod2);
                    }
                }
            } else {
                cfg.addProductionRule(head, prod);
            }
        }
    }

    public ContextFreeGrammar removeUnitaryProductions(ContextFreeGrammar cfg) {
        HashSet<Character> variables = (HashSet<Character>) cfg.getVariables().clone();
        HashMap<Character, HashSet<String>> originalProductionRules = cfg.getProductionRules();
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        originalProductionRules.forEach((head, bodies) -> {
            newCfg.addVariable(head);
            newCfg.getProductionRules().get(head).remove(head.toString()); //remove unitary self production
            bodies.forEach(body -> {
                simulateUnitaryProductions(newCfg, head, originalProductionRules);
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
}
