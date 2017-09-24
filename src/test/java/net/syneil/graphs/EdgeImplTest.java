package net.syneil.graphs;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("An edge")
class EdgeImplTest {
    private static final String SOURCE = "A";
    private static final boolean DIRECTED = true;
    private static final boolean UNDIRECTED = false;
    private static final int LABEL = 1;
    private static final String TARGET = "B";

    private EdgeImpl<String, Integer> edge;

    @DisplayName("created with new and params")
    @Test
    void createdWithNewParams() {
        new EdgeImpl<>(SOURCE, DIRECTED, LABEL, TARGET);
    }

    @DisplayName("when directed")
    @Nested
    class WhenDirected {
        @BeforeEach
        void createEdge() {
            edge = new EdgeImpl<>(SOURCE, DIRECTED, LABEL, TARGET);
        }

        @DisplayName("getters work as expected")
        @Test
        void testGetters() {
            assertAll(() -> assertEquals(SOURCE, edge.getSource()),
                    () -> assertEquals(DIRECTED, edge.isDirected()),
                    () -> assertTrue(edge.getLabel().isPresent() && LABEL == edge.getLabel().get()),
                    () -> assertEquals(TARGET, edge.getTarget()));
        }

        @DisplayName("has connector is specific")
        @Test
        void testHasConnectors() {
            assertAll(() -> assertTrue(edge.hasSource(SOURCE)),
                    () -> assertTrue(edge.hasTarget(TARGET)),
                    () -> assertFalse(edge.hasSource(TARGET)),
                    () -> assertFalse(edge.hasTarget(SOURCE)),
                    () -> assertTrue(edge.connects(SOURCE)),
                    () -> assertTrue(edge.connects(TARGET)));
        }

        @DisplayName("equals cares about direction")
        @Test
        void testEquals() {
            Graph.Edge<String, Integer> that = new EdgeImpl<>(edge.getTarget(), DIRECTED, LABEL, edge.getSource());
            assertNotEquals(that, edge);
        }
}

    @DisplayName("when undirected")
    @Nested
    class WhenUndirected {
        @BeforeEach
        void createEdge() {
            edge = new EdgeImpl<>(SOURCE, UNDIRECTED, LABEL, TARGET);
        }

        @DisplayName("getters work as expected")
        @Test
        void testGetters() {
            assertAll(() -> assertEquals(SOURCE, edge.getSource()),
                    () -> assertEquals(UNDIRECTED, edge.isDirected()),
                    () -> assertTrue(edge.getLabel().isPresent() && LABEL == edge.getLabel().get()),
                    () -> assertEquals(TARGET, edge.getTarget()));
        }

        @DisplayName("has connector is nonspecific")
        @Test
        void testHasConnectors() {
            assertAll(() -> assertTrue(edge.hasSource(SOURCE)),
                    () -> assertTrue(edge.hasTarget(TARGET)),
                    () -> assertTrue(edge.hasSource(TARGET)),
                    () -> assertTrue(edge.hasTarget(SOURCE)),
                    () -> assertTrue(edge.connects(SOURCE)),
                    () -> assertTrue(edge.connects(TARGET)));
        }

        @DisplayName("equals cares about direction")
        @Test
        void testEquals() {
            Graph.Edge<String, Integer> that = new EdgeImpl<>(edge.getTarget(), UNDIRECTED, LABEL, edge.getSource());
            assertEquals(that, edge);
        }
    }

}
