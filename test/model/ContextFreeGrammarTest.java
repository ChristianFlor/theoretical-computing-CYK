package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ContextFreeGrammarTest {
    private ContextFreeGrammar cfg;

    private void setupStage1() {
        cfg = new ContextFreeGrammar();
    }

    @Test
    public void testAddVariable() {
        setupStage1();
        assertFalse(cfg.addVariable('a'), "Variables must be uppercase letters");
        try {
            cfg.addVariable('T');
            fail("T is reserved and thus it cannot be added manually");
        } catch (IllegalArgumentException iae) {
            // :)
        }
        assertFalse(cfg.addVariable('S'), "S is the start variable and must be already present in the grammar");
        assertTrue(cfg.addVariable('A'), "A must have been added to the variables set");
    }
}
