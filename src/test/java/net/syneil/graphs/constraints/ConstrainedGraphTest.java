package net.syneil.graphs.constraints;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A constrained graph")
class ConstrainedGraphTest {
    private ConstrainedGraph<String, Integer> graph;

    @DisplayName("is constructed with constraints")
    @Test
    void constructedWithConstraints() {
        new ConstrainedGraph<>(Orientation.MIXED, Multiplicity.MULTI);
    }

    @DisplayName("when new")
    @Nested
    class WhenNew {
        private final Orientation ORIENTATION = Orientation.MIXED;
        private final Multiplicity MULTIPLICITY = Multiplicity.MULTI;

        @BeforeEach
        void createGraph() {
            graph = new ConstrainedGraph<>(ORIENTATION, MULTIPLICITY);
        }

        @DisplayName("is not in edit mode")
        @Test
        void isNotInEditMode() {
            assertFalse(graph.isInEditMode());
        }
    }

    @DisplayName("when unconstrained")
    @Nested
    class WhenUnconstrained {
        private final Orientation ORIENTATION = Orientation.MIXED;
        private final Multiplicity MULTIPLICITY = Multiplicity.MULTI;

        @BeforeEach
        void createGraph() {
            graph = new ConstrainedGraph<>(ORIENTATION, MULTIPLICITY);
        }


    }

    @DisplayName("when directed")
    @Nested
    class WhenDirected {
        private final Orientation ORIENTATION = Orientation.DIRECTED;
        private final Multiplicity MULTIPLICITY = Multiplicity.MULTI;

        @BeforeEach
        void createGraph() {
            graph = new ConstrainedGraph<>(ORIENTATION, MULTIPLICITY);
        }

        @DisplayName("in edit mode")
        @Nested
        class InEditMode {
            @BeforeEach
            void enterEditMode() {
                graph.startEditMode();
            }

            @DisplayName("can add undirected edges")
            @Test
            void canAddUndirectedEdge() {
                assertTrue(graph.addEdge("A", 1, "B", false));
            }
        }
    }

    @DisplayName("when oriented")
    @Nested
    class WhenOriented {
        private final Orientation ORIENTATION = Orientation.ORIENTED;
        private final Multiplicity MULTIPLICITY = Multiplicity.MULTI;

        @BeforeEach
        void createGraph() {
            graph = new ConstrainedGraph<>(ORIENTATION, MULTIPLICITY);
        }


        @DisplayName("in edit mode")
        @Nested
        class InEditMode {
            @BeforeEach
            void enterEditMode() {
                graph.startEditMode();
            }

            @DisplayName("can add undirected edge")
            @Test
            void canAddUndirectedEdge() {
                assertTrue(graph.addEdge("A", 1, "B", false));
            }

            @DisplayName("can add reverse edge")
            @Test
            void canAddReverseEdge() {
                assertTrue(graph.addEdge("A", 1, "B", true));
                assertTrue(graph.addEdge("B", 2, "A", true));
            }
        }

        @DisplayName("not in edit mode")
        @Nested
        class OutOfEditMode {
            @BeforeEach
            void leaveEditMode() {
                graph.endEditMode();
            }


        }
    }
}
