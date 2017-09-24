package net.syneil.graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A graph")
class GraphTest {
    private Graph<String, Integer> graph;

    @Test
    @DisplayName("is instantiated with new")
    void isInstantiatedWithNew() {
        new GraphImpl<>();
    }

    @DisplayName("when new")
    @Nested
    class WhenNew {
        @BeforeEach
        void createNewGraph() {
            graph = new GraphImpl<>();
        }

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertAll(() -> assertEquals(0, graph.numberOfVertices()),
                    () -> assertEquals(0, graph.numberOfEdges()),
                    () -> assertEquals(0, graph.vertices().count()),
                    () -> assertEquals(0, graph.edges().count()));
        }

        @Test
        @DisplayName("doesn't fail on queries")
        void doesNotFailOnQueries() {
            assertAll(() -> assertTrue(graph.neighbours("").isEmpty()),
                    () -> assertFalse(graph.hasVertex("")),
                    () -> assertFalse(graph.hasEdge("A", "B")),
                    () -> assertTrue(graph.getEdges("").isEmpty()),
                    () -> assertTrue(graph.getEdges("A", "B").isEmpty()),
                    () -> assertTrue(Double.isNaN(graph.density())));
        }

        @Test
        @DisplayName("returns false for removals")
        void returnsFalseForRemovals() {
            assertAll(() -> assertFalse(graph.removeVertex("")),
                    () -> assertFalse(graph.removeEdges("A", "B")));
        }

        @Test
        @DisplayName("can add vertices")
        void canAddVertices() {
            assertAll(() -> assertTrue(graph.addVertex("A")),
                    () -> assertTrue(graph.addVertex("B")),
                    () -> assertFalse(graph.addVertex("A")));
        }

        @Test
        @DisplayName("can add edges")
        void canAddEdges() {
            assertAll(() -> assertTrue(graph.addEdge("A", 1, "B", true)),
                    () -> assertTrue(graph.addEdge("B", 2, "C", false)),
                    () -> assertTrue(graph.addEdge("B", 2, "C", true)),
                    () -> assertFalse(graph.addEdge("A", 1, "B", true)));
        }
    }

    @DisplayName("when populated")
    @Nested
    class WhenNested {
        @BeforeEach
        void populateGraph() {
            graph = new GraphImpl<>();
            graph.addEdge("A", 1, "B", true);
            graph.addEdge("B", 2, "C", false);
            graph.addEdge("D", 3, "C", true);
            graph.addEdge("A", 4, "D", false);
            graph.addEdge("D", 5, "E", true);
        }

        @DisplayName("reports metrics")
        @Test
        void reportsMetrics() {
            assertAll(() -> assertEquals(5, graph.numberOfVertices()),
                    () -> assertEquals(5, graph.numberOfEdges()),
                    () -> assertEquals(0.25, graph.density()));
        }

        @DisplayName("vertex stream is exactly as expected")
        @Test
        void vertexStreamIsAsExpected() {
            final Set<String> vertices = graph.vertices().collect(Collectors.toSet());
            assertAll(() -> assertTrue(vertices.containsAll(Arrays.asList("A", "B", "C", "D", "E"))),
                    () -> assertEquals(5, vertices.size()));
        }

        @DisplayName("edge stream is exactly as expected")
        @Test
        void edgeStreamIsAsExpected() {
            final Set<Graph.Edge<String, Integer>> edges = graph.edges().collect(Collectors.toSet());
            assertAll(() -> assertTrue(edges.containsAll(Arrays.asList(
                    new EdgeImpl<>("A", true, 1, "B"),
                    new EdgeImpl<>("C", false, 2, "B"),
                    new EdgeImpl<>("D", true, 3, "C"),
                    new EdgeImpl<>("D", false, 4, "A"),
                    new EdgeImpl<>("D", true, 5, "E")))),
                    () -> assertEquals(5, edges.size()));
        }

        @DisplayName("can add an isolated vertex")
        @Test
        void canAddNewVertex() {
            assertTrue(graph.addVertex("F"));
        }

        @DisplayName("can add an isolated edge")
        @Test
        void canAddNewEdge() {
            assertTrue(graph.addEdge("F", 6, "G", false));
        }

        @DisplayName("adding extant vertex gives false")
        @Test
        void addExtantVertexGivesFalse() {
            assertFalse(graph.addVertex("A"));
        }

        @DisplayName("adding extant edge gives false")
        @Test
        void addExtantEdgeIsFalse() {
            assertFalse(graph.addEdge("A", 1, "B", true));
        }
    }
}
