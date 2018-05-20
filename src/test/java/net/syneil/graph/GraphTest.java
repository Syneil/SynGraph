package net.syneil.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.syneil.graph.Edge.isBetween;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the basic contract for the graph (and mutable extension). Implementations of {@link MutableGraph} need only
 * provide implementations for the construction of an empty instance of the graph, a new unique vertex and a new
 * unique edge between two vertices.
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 * @param <G> the type of graph under test
 */
@SuppressWarnings("unused")
@DisplayName("this implementation")
public abstract class GraphTest<V, E extends Edge<V>, G extends MutableGraph<V, E>> {

    /**
     * The graph instance being used for the test
     */
    private G graph;

    /**
     * The injected vertex supplier
     */
    private Supplier<V> vertexSupplier;

    /**
     * The injected edge supplier
     */
    private BiFunction<V, V, E> edgeGenerator;

    /**
     * @return an instance of the graph implementation to use for testing
     */
    protected abstract G getEmptyGraph();

    /**
     * @return the strategy for creating new vertices
     */
    protected abstract Supplier<V> getVertexSupplier();

    /**
     * @return the strategy for creating new edges given two vertices
     */
    protected abstract BiFunction<V, V, E> getEdgeGenerator();

    /**
     * Queries the implementation for a new graph and the strategies for creating vertices and edges
     */
    @BeforeEach
    final void prepareEmptyGraph() {
        graph = getEmptyGraph();
        vertexSupplier = getVertexSupplier();
        edgeGenerator = getEdgeGenerator();
    }

    @Test
    @DisplayName("is instantiated with no vertices or edges")
    final void emptyGraphHasZeroVerticesAndZeroEdges() {
        testSizes(0, 0);
    }

    @Test
    @DisplayName("is instantiated with an empty adjacency list")
    final void emptyGraphHasEmptyAdjacencyList() {
        assertTrue(graph.adjacencyList().isEmpty());
    }

    @Test
    @DisplayName("is instantiated with a NaN density")
    final void emptyGraphHasNoDensity() {
        assertEquals(Double.NaN, graph.density());
    }

    @Nested
    @DisplayName("when a vertex is added")
    class WithOneVertex {
        V firstVertex;

        @BeforeEach
        final void addFirstVertex() {
            firstVertex = vertexSupplier.get();
            assertTrue(graph.addVertex(firstVertex));
        }

        @Test
        @DisplayName("the graph has one vertex and no edges")
        final void oneVertexZeroEdges() {
            testSizes(1, 0);
        }

        @Test
        @DisplayName("the graph's vertex stream contains the vertex")
        final void streamHasFirstVertex() {
            assertTrue(graph.vertices().anyMatch(firstVertex::equals));
        }

        @Test
        @DisplayName("the graph has the vertex")
        final void hasFirstVertex() {
            assertTrue(graph.hasVertex(firstVertex));
        }

        @Test
        @DisplayName("the graph does not have an edge from the vertex to itself")
        final void hasNoSelfEdge() {
            assertFalse(graph.hasEdge(firstVertex, firstVertex));
        }

        @Test
        @DisplayName("the graph still has NaN density")
        final void singletonGraphHasNoDensity() {
            assertEquals(Double.NaN, graph.density());
        }

        @Nested
        @DisplayName("when an edge is added from the vertex to itself")
        class WithSelfEdge {
            @BeforeEach
            final void addSelfEdge() {
                assertTrue(graph.addEdge(edgeGenerator.apply(firstVertex, firstVertex)));
            }

            @Test
            @DisplayName("the graph has one vertex and one edge")
            final void oneVertexOneEdge() {
                testSizes(1, 1);
            }

            @Test
            @DisplayName("the graph's edge stream has the edge")
            final void edgeIsInStream() {
                assertTrue(graph.edges().anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            @DisplayName("the graph reports the edge is outgoing from the vertex")
            final void edgeIsAnOutEdge() {
                assertTrue(graph.getEdges(firstVertex).stream().anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            @DisplayName("the graph's edge is reported between the vertex and itself")
            final void edgeIsReturned() {
                assertTrue(graph.getEdges(firstVertex, firstVertex).stream()
                        .anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            @DisplayName("the graph reports it has an edge from the vertex to itself")
            final void hasASelfEdge() {
                assertTrue(graph.hasEdge(firstVertex, firstVertex));
            }

            @Nested
            @DisplayName("then the self-edge")
            class TheSelfEdge {
                Edge<V> theSelfEdge;

                @BeforeEach
                final void extractTheSelfEdge() {
                    theSelfEdge = graph.edges().findAny().orElseGet(() -> {
                        fail("Invalid test");
                        return null;
                    });
                }

                @Test
                @DisplayName("has the vertex as its source")
                final void sourceIsFirstVertex() {
                    assertEquals(firstVertex, theSelfEdge.getSource());
                }

                @Test
                @DisplayName("has the vertex as its target")
                final void targetIsFirstVertex() {
                    assertEquals(firstVertex, theSelfEdge.getTarget());
                }

                @Test
                @DisplayName("connects the vertex")
                final void connectsFirstVertex() {
                    assertTrue(theSelfEdge.connects(firstVertex));
                }

                @Test
                @DisplayName("does not connect an unknown vertex")
                final void doesNotConnectNonVertex() {
                    assertFalse(theSelfEdge.connects(vertexSupplier.get()));
                }

                @Test
                @DisplayName("recognises the other vertex as the same vertex")
                final void otherIsSame() {
                    assertEquals(Optional.of(firstVertex), theSelfEdge.other(firstVertex));
                }
            }

            @Nested
            @DisplayName("when the self-edge is removed again")
            class SelfEdgeRemoved {
                @BeforeEach
                final void removeSelfEdge() {
                    assertTrue(graph.removeEdges(firstVertex, firstVertex));
                }

                @Test
                @DisplayName("the graph has one vertex and no edges")
                final void oneVertexZeroEdgesAgain() {
                    testSizes(1, 0);
                }

                @Test
                @DisplayName("the graph has no edge from the vertex to itself")
                final void hasNoSelfEdgeAgain() {
                    assertFalse(graph.hasEdge(firstVertex, firstVertex));
                }
            }
        }

        @Nested
        @DisplayName("when a second vertex is added")
        class WithSecondVertex {
            V secondVertex;

            @BeforeEach
            final void addSecondVertex() {
                secondVertex = vertexSupplier.get();
                assertTrue(graph.addVertex(secondVertex));
            }

            @Test
            @DisplayName("the graph has two vertices and no edges")
            final void twoVerticesZeroEdges() {
                testSizes(2, 0);
            }

            @Test
            @DisplayName("the graph's vertex stream includes the new vertex")
            final void streamHasSecondVertex() {
                assertTrue(graph.vertices().anyMatch(secondVertex::equals));
            }

            @Test
            @DisplayName("the graph has a density of zero")
            final void edgelessTwoGraphHasNoDensity() {
                assertEquals(0d, graph.density());
            }

            @Nested
            @DisplayName("when an edge is added from the first to the second vertex")
            class WithEdgeBetweenThem {
                @BeforeEach
                final void addEdge() {
                    assertTrue(graph.addEdge(edgeGenerator.apply(firstVertex, secondVertex)));
                }

                @Test
                @DisplayName("the graph has two vertices and one edge")
                final void twoVerticesOneEdge() {
                    testSizes(2, 1);
                }

                @Test
                @DisplayName("the graph's edge stream includes the newly added edge")
                final void edgeBetweenThemIsInStream() {
                    assertTrue(graph.edges().anyMatch(isBetween(firstVertex, secondVertex)));
                }

                @Test
                @DisplayName("the graph has a density of 0.5")
                final void connectedTwoGraphHasHalfDensity() {
                    assertEquals(0.5d, graph.density(), 0.001d);
                }

                @Nested
                @DisplayName("then the new edge")
                class TheBridgingEdge {
                    Edge<V> theBridgingEdge;

                    @BeforeEach
                    final void extractTheBridgingEdge() {
                        theBridgingEdge = graph.edges().findAny().orElseGet(() -> {
                            fail("Invalid test");
                            return null;
                        });
                    }

                    @Test
                    @DisplayName("has the first vertex as its source")
                    final void sourceIsFirstVertex() {
                        assertEquals(firstVertex, theBridgingEdge.getSource());
                    }

                    @Test
                    @DisplayName("has the second vertex as its target")
                    final void targetIsFirstVertex() {
                        assertEquals(secondVertex, theBridgingEdge.getTarget());
                    }

                    @Test
                    @DisplayName("reports that it connects both the first and second vertices")
                    final void connectsFirstAndSecondVertices() {
                        assertAll(() -> assertTrue(theBridgingEdge.connects(firstVertex)),
                                () -> assertTrue(theBridgingEdge.connects(secondVertex)));
                    }

                    @Test
                    @DisplayName("reports that it does not connect an unknown vertex")
                    final void doesNotConnectNonVertex() {
                        assertFalse(theBridgingEdge.connects(vertexSupplier.get()));
                    }
                }

                @Nested
                @DisplayName("when removing the first vertex (leaving the second)")
                class WithFirstVertexRemoved {
                    @BeforeEach
                    final void removeFirstVertex() {
                        assertTrue(graph.removeVertex(firstVertex));
                    }

                    @Test
                    @DisplayName("the graph has one vertex and no edges")
                    final void secondVertexZeroEdges() {
                        testSizes(1, 0);
                    }
                }

                @Nested
                @DisplayName("when removing the second vertex (leaving the first)")
                class WithSecondVertexRemoved {
                    @BeforeEach
                    final void removeSecondVertex() {
                        assertTrue(graph.removeVertex(secondVertex));
                    }

                    @Test
                    @DisplayName("the graph has one vertex and no edges")
                    final void firstVertexZeroEdgesAgain() {
                        testSizes(1, 0);
                    }
                }
            }
        }
    }

    /**
     * Convenience method to test the vertex and edge counts of {@link #graph}
     *
     * @param numberOfVertices the expected number of vertices
     * @param numberOfEdges    the expected number of edges
     */
    private void testSizes(int numberOfVertices, int numberOfEdges) {
        assertAll(() -> assertEquals(numberOfVertices, graph.numberOfVertices(), "numberOfVertices incorrect"),
                () -> assertEquals(numberOfEdges, graph.numberOfEdges(), "numberOfEdges incorrect"),
                () -> assertEquals(numberOfVertices, graph.vertices().count(), "vertices.count incorrect"),
                () -> assertEquals(numberOfEdges, graph.edges().count(), "edges.count incorrect"));
    }
}
