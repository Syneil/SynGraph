package net.syneil.graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
public abstract class GraphTest<V, E, G extends Graph<V, E>> {

    private static <E> Supplier<E> invalidEdgeTest() {
        return () -> {
            fail("Invalid test");
            return null;
        };
    }

    private G graph;
    private Supplier<V> vertexSupplier;
    private Supplier<E> edgeLabelSupplier;

    abstract G getEmptyGraph();

    abstract Supplier<V> getVertexSupplier();

    abstract Supplier<E> getEdgeLabelSupplier();

    @BeforeEach
    final void prepareEmptyGraph() {
        graph = getEmptyGraph();
        vertexSupplier = getVertexSupplier();
        edgeLabelSupplier = getEdgeLabelSupplier();
    }

    @Test
    final void emptyGraphHasZeroVerticesAndZeroEdges() {
        testSizes(0, 0);
    }

    @Test
    final void emptyGraphHasEmptyAdjacencyList() {
        assertTrue(graph.adjacencyList().isEmpty());
    }

    @Test
    final void emptyGraphHasNoDensity() {
        assertEquals(Double.NaN, graph.density());
    }

    @Nested
    class WithOneVertex {
        V firstVertex;

        @BeforeEach
        final void addFirstVertex() {
            firstVertex = vertexSupplier.get();
            assertTrue(graph.addVertex(firstVertex));
        }

        @Test
        final void oneVertexZeroEdges() {
            testSizes(1, 0);
        }

        @Test
        final void streamHasFirstVertex() {
            assertTrue(graph.vertices().anyMatch(firstVertex::equals));
        }

        @Test
        final void hasFirstVertex() {
            assertTrue(graph.hasVertex(firstVertex));
        }

        @Test
        final void hasNoSelfEdge() {
            assertFalse(graph.hasEdge(firstVertex, firstVertex));
        }

        @Test
        final void singletonGraphHasNoDensity() {
            assertEquals(Double.NaN, graph.density());
        }

        @Nested
        class WithSelfEdge {
            @BeforeEach
            final void addSelfEdge() {
                assertTrue(graph.addEdge(firstVertex, edgeLabelSupplier.get(), firstVertex, true));
            }

            @Test
            final void oneVertexOneEdge() {
                testSizes(1, 1);
            }

            @Test
            final void edgeIsInStream() {
                assertTrue(graph.edges().anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            final void edgeIsAnOutEdge() {
                assertTrue(graph.getEdges(firstVertex).stream().anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            final void edgeIsReturned() {
                assertTrue(graph.getEdges(firstVertex, firstVertex).stream()
                        .anyMatch(isBetween(firstVertex, firstVertex)));
            }

            @Test
            final void hasASelfEdge() {
                assertTrue(graph.hasEdge(firstVertex, firstVertex));
            }

            @Nested
            class TheSelfEdge {
                Graph.Edge<V, E> theSelfEdge;

                @BeforeEach
                final void extractTheSelfEdge() {
                    theSelfEdge = graph.edges().findAny().orElseGet(invalidEdgeTest());
                }

                @Test
                final void sourceIsFirstVertex() {
                    assertEquals(firstVertex, theSelfEdge.getSource());
                }

                @Test
                final void targetIsFirstVertex() {
                    assertEquals(firstVertex, theSelfEdge.getTarget());
                }

                @Test
                final void connectsFirstVertex() {
                    assertTrue(theSelfEdge.connects(firstVertex));
                }

                @Test
                final void doesNotConnectNonVertex() {
                    assertFalse(theSelfEdge.connects(vertexSupplier.get()));
                }

                @Test
                final void otherIsSame() {
                    assertEquals(Optional.of(firstVertex), theSelfEdge.other(firstVertex));
                }
            }

            @Nested
            class SelfEdgeRemoved {
                @BeforeEach
                final void removeSelfEdge() {
                    assertTrue(graph.removeEdges(firstVertex, firstVertex));
                }

                @Test
                final void oneVertexZeroEdgesAgain() {
                    testSizes(1, 0);
                }

                @Test
                final void hasNoSelfEdgeAgain() {
                    assertFalse(graph.hasEdge(firstVertex, firstVertex));
                }
            }
        }

        @Nested
        class WithSecondVertex {
            V secondVertex;

            @BeforeEach
            final void addSecondVertex() {
                secondVertex = vertexSupplier.get();
                assertTrue(graph.addVertex(secondVertex));
            }

            @Test
            final void twoVerticesZeroEdges() {
                testSizes(2, 0);
            }

            @Test
            final void streamHasSecondVertex() {
                assertTrue(graph.vertices().anyMatch(secondVertex::equals));
            }

            @Test
            final void edgelessTwoGraphHasNoDensity() {
                assertEquals(0d, graph.density());
            }

            @Nested
            class WithEdgeBetweenThem {
                E edgeLabel;

                @BeforeEach
                final void addEdge() {
                    edgeLabel = edgeLabelSupplier.get();
                    assertTrue(graph.addEdge(firstVertex, edgeLabel, secondVertex, true));
                }

                @Test
                final void twoVerticesOneEdge() {
                    testSizes(2, 1);
                }

                @Test
                final void edgeBetweenThemIsInStream() {
                    assertTrue(graph.edges().anyMatch(isBetween(firstVertex, secondVertex)));
                }

                @Test
                final void connectedTwoGraphHasHalfDensity() {
                    assertEquals(0.5d, graph.density(), 0.001d);
                }

                @Nested
                class TheBridgingEdge {
                    Graph.Edge<V, E> theBridgingEdge;

                    @BeforeEach
                    final void extractTheBridgingEdge() {
                        theBridgingEdge = graph.edges().findAny().orElseGet(invalidEdgeTest());
                    }

                    @Test
                    final void sourceIsFirstVertex() {
                        assertEquals(firstVertex, theBridgingEdge.getSource());
                    }

                    @Test
                    final void targetIsFirstVertex() {
                        assertEquals(secondVertex, theBridgingEdge.getTarget());
                    }

                    @Test
                    final void connectsFirstAndSecondVertices() {
                        assertAll(() -> assertTrue(theBridgingEdge.connects(firstVertex)),
                                () -> assertTrue(theBridgingEdge.connects(secondVertex)));
                    }

                    @Test
                    final void doesNotConnectNonVertex() {
                        assertFalse(theBridgingEdge.connects(vertexSupplier.get()));
                    }
                }

                @Nested
                class WithFirstVertexRemoved {
                    @BeforeEach
                    final void removeFirstVertex() {
                        assertTrue(graph.removeVertex(firstVertex));
                    }

                    @Test
                    final void oneVertexZeroEdgesAgain() {
                        testSizes(1, 0);
                    }
                }
            }
        }
    }

    private void testSizes(int numberOfVertices, int numberOfEdges) {
        assertAll(() -> assertEquals(numberOfVertices, graph.numberOfVertices()),
                () -> assertEquals(numberOfEdges, graph.numberOfEdges()),
                () -> assertEquals(numberOfVertices, graph.vertices().count()),
                () -> assertEquals(numberOfEdges, graph.edges().count()));
    }

    private Predicate<Graph.Edge<V, E>> isBetween(V source, V target) {
        return edge -> edge.hasSource(source) && edge.hasTarget(target);
    }
}
