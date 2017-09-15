package net.syneil.fsm;

import static net.syneil.func.Predicates.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

public class FSMTest {
    private StringBuilder output;

    @Before
    public void setUp() {
        this.output = new StringBuilder();
    }

    @Test
    public void testMod3Machine() {
        FSM<String, Character, String> fsm = buildMod3Machine();
        assertTrue(fsm.isInStartState());
        assertTrue(fsm.isInEndState());

        fsm.accept('1'); // 1
        assertEquals("1", fsm.currentState());
        assertFalse(fsm.isInStartState());
        fsm.accept('1'); // 11
        assertEquals("2", fsm.currentState());
        assertFalse(fsm.isInEndState());
        fsm.accept('1'); // 111
        assertTrue(fsm.isInStartState());
        assertTrue(fsm.isInEndState());

        assertFalse(fsm.acceptAll(Integer.toString(38576).chars().mapToObj(i -> (char) i)));
        assertEquals(Integer.toString(38576 % 3), fsm.currentState());

        System.out.println(this.output.toString());
    }

    @Test(expected = NoTransitionForSymbolException.class)
    public void testMod3MachineNoAlpha() {
        buildMod3Machine().accept('A');
    }

    private FSM<String, Character, String> buildMod3Machine() {
        final FSM.Builder<String, Character, String> fsmBuilder = new FSM.Builder<>("0", true);
        fsmBuilder.setOutput(this.output::append);

        fsmBuilder.addState("1", false);
        fsmBuilder.addState("2", false);

        Function<Character, String> mod3_0 = c -> isOneOf('0', '3', '6', '9').test(c) ? "=" : null;
        Function<Character, String> mod3_1 = c -> isOneOf('1', '4', '7').test(c) ? "+" : null;
        Function<Character, String> mod3_2 = c -> isOneOf('2', '5', '8').test(c) ? "-" : null;

        fsmBuilder.addTransition("0", mod3_0, "0").addTransition("1", mod3_0, "1").addTransition("2", mod3_0, "2");
        fsmBuilder.addTransition("0", mod3_1, "1").addTransition("1", mod3_1, "2").addTransition("2", mod3_1, "0");
        fsmBuilder.addTransition("0", mod3_2, "2").addTransition("1", mod3_2, "0").addTransition("2", mod3_2, "1");

        return fsmBuilder.build();
    }
}
