package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ContextFreeGrammarOperations {
    public ContextFreeGrammar removeNonTerminalVaribles(ContextFreeGrammar cfg) {
        return null;
    }

    public ContextFreeGrammar removeNonReachableVaribles(ContextFreeGrammar cfg) {
        return null;
    }

    public ContextFreeGrammar removeLambdaProductions(ContextFreeGrammar cfg) {
        HashSet<Character> anul = new HashSet<>();
        HashSet<Character> variables = (HashSet<Character>)cfg.getVariables().clone();
        HashMap<Character, HashSet<String>> productionRules = (HashMap<Character, HashSet<String>>)cfg.getProductionRules().clone();
        for(Character var : variables) {
            if(productionRules.get(var).contains(ContextFreeGrammar.LAMBDA)) {
                anul.add(var);
            }
        }
        int previousSize = 0;
        while(previousSize < anul.size()) {
            previousSize = anul.size();
            for (Character var : variables) {
                HashSet<String> prods = productionRules.get(var);
                for(String prod : prods) {
                    for(Character p : anul) {
                        prod.replace(p.toString(), "");
                    }
                    if(prod.isEmpty()) {
                        anul.add(var);
                        break;
                    }
                }
            }
        }
        int productions = 0;
        for (Character var : variables) {
            productions += productionRules.get(var).size();
        }
        previousSize = 0;
        while(previousSize < productions) {
            previousSize = productions;
            for (Character var : variables) {
                HashSet<String> prods = (HashSet<String>)productionRules.get(var).clone();
                productions -= prods.size();
                for (String prod : prods) {
                    for (Character p : anul) {
                        simulateNullableVariablesInProductions(var, prod, p, productionRules);
                        if (var != ContextFreeGrammar.START) {
                            productionRules.get(var).remove(ContextFreeGrammar.LAMBDA);
                        }
                    }
                }
                productions += productionRules.get(var).size();
            }
        }
        ContextFreeGrammar newCfg = new ContextFreeGrammar();
        productionRules.forEach((head, bodies) -> {
            cfg.addVariable(head);
            bodies.forEach(body -> cfg.addProductionRule(head, body));
        });
        return newCfg;
    }

    private void simulateNullableVariablesInProductions(Character head, String body, Character nullable, HashMap<Character, HashSet<String>> productionRules) {
        int lastIndex = body.indexOf(nullable);
        while(lastIndex != -1) {
            productionRules.get(head).add(new StringBuilder(body).deleteCharAt(lastIndex).toString());
            lastIndex = lastIndex < body.length() - 1 ? body.substring(lastIndex + 1).indexOf(nullable) : -1;
        }
    }

    public ContextFreeGrammar removeUnitaryProductions(ContextFreeGrammar cfg) {
        return null;
    }
}
