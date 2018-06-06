package net.syneil.graph;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class MutableGraphTest<V> {

    @Test
    void canAddNewVertex() {
        assertTrue(getMutableGraph().addVertex(createNewVertex()));
    }

    @Test
    void cannotAddNullVertex() {
        assertThrows(NullPointerException.class, () -> getMutableGraph().addVertex(null));
    }

    public abstract MutableGraph<V, Edge<V>> getMutableGraph();

    public abstract V createNewVertex();

    @Test
    void cannotAddNullEdge() {
        assertThrows(NullPointerException.class, () -> getMutableGraph().addEdge(null));
    }

    @Test
    void cannotAddEdgeWithNullSource() {
        assertThrows(NullPointerException.class,
                     () -> getMutableGraph().addEdge(createNewEdge(null, createNewVertex())));
    }

    @Test
    void cannotAddEdgeWithNullTarget() {
        assertThrows(NullPointerException.class,
                     () -> getMutableGraph().addEdge(createNewEdge(createNewVertex(), null)));
    }

    @Test
    void cannotAddEdgeWithNullVertices() {
        assertThrows(NullPointerException.class, () -> getMutableGraph().addEdge(createNewEdge(null, null)));
    }

    @Test
    void givenEdgeVerticesArePresent() {
        // given
        V source = createNewVertex();
        V target = createNewVertex();
        assertTrue(getMutableGraph().addEdge(createNewEdge(source, target)));

        // then
        assertAll(() -> assertTrue(getMutableGraph().hasVertex(source)),
                  () -> assertTrue(getMutableGraph().hasVertex(target)));
    }

    public abstract Edge<V> createNewEdge(V source, V target);

    @Test
    void removeVertexDoesNothing() {
        assertFalse(getMutableGraph().removeVertex(createNewVertex()));
    }

    @Test
    void removeEdgeDoesNothing() {
        assertFalse(getMutableGraph().removeEdge(createNewEdge(createNewVertex(), createNewVertex())));
    }

    @Test
    void removeEdgesDoesNothing() {
        assertFalse(getMutableGraph().removeEdges(createNewVertex(), createNewVertex()));
    }

    @Test
    void givenVertexCannotAddSameVertex() {
        // given
        V vertex = createNewVertex();
        assertTrue(getMutableGraph().addVertex(vertex));

        // then
        assertFalse(getMutableGraph().addVertex(vertex));
    }

    @Test
    void givenVertexCanAddSelfLoop() {
        // precondition
        assumeTrue(loopsPermitted());

        // given
        V vertex = createNewVertex();
        assertTrue(getMutableGraph().addVertex(vertex));

        // then
        assertTrue(getMutableGraph().addEdge(createNewEdge(vertex, vertex)));
    }

    public abstract boolean loopsPermitted();

    @Test
    void givenVertexCanRemoveSameVertex() {
        // given
        V vertex = createNewVertex();
        assertTrue(getMutableGraph().addVertex(vertex));

        // then
        assertTrue(getMutableGraph().removeVertex(vertex));
    }

    @Test
    void givenVertexCannotRemoveUnknownVertex() {
        // given
        assertTrue(getMutableGraph().addVertex(createNewVertex()));

        // then
        assertFalse(getMutableGraph().removeVertex(createNewVertex()));
    }

    @Test
    void givenSelfLoopCanRemoveEdge() {
        // precondition
        assumeTrue(loopsPermitted());

        // given
        V vertex = createNewVertex();
        Edge<V> edge = createNewEdge(vertex, vertex);
        assertAll(() -> assertTrue(getMutableGraph().addVertex(vertex)),
                  () -> assertTrue(getMutableGraph().addEdge(edge)));

        // then
        assertTrue(getMutableGraph().removeEdge(edge));
    }

    @Test
    void givenSelfLoopNeighboursIncludesSourceVertex() {
        // precondition
        assumeTrue(loopsPermitted());

        // given
        V vertex = createNewVertex();
        assertAll(() -> assertTrue(getMutableGraph().addVertex(vertex)),
                  () -> assertTrue(getMutableGraph().addEdge(createNewEdge(vertex, vertex))));

        // then
        assertTrue(getMutableGraph().neighbours(vertex).contains(vertex));
    }

    @Test
    void givenMultiGraphSelfLoopCanAddDuplicateEdge() {
        // preconditions
        assumeTrue(loopsPermitted());
        assumeTrue(multipleEdgesPermitted());

        // given
        V vertex = createNewVertex();
        assertAll(() -> assertTrue(getMutableGraph().addVertex(vertex)),
                  () -> assertTrue(getMutableGraph().addEdge(createNewEdge(vertex, vertex))));

        // then
        assertTrue(getMutableGraph().addEdge(createNewEdge(vertex, vertex)));
    }

    public abstract boolean multipleEdgesPermitted();

    @Test
    void givenMultiGraphSelfLoopCannotAddIdenticalEdge() {
        // preconditions
        assumeTrue(loopsPermitted());
        assumeTrue(multipleEdgesPermitted());

        // given
        V vertex = createNewVertex();
        Edge<V> edge = createNewEdge(vertex, vertex);
        assertAll(() -> assertTrue(getMutableGraph().addVertex(vertex)),
                  () -> assertTrue(getMutableGraph().addEdge(edge)));

        // then
        assertFalse(getMutableGraph().addEdge(edge));
    }

    @Test
    void givenSelfLoopWhenVertexRemovedThenEdgeRemoved() {
        // precondition
        assumeTrue(loopsPermitted());

        // given
        V vertex = createNewVertex();
        assertAll(() -> assertTrue(getMutableGraph().addVertex(vertex)),
                  () -> assertTrue(getMutableGraph().addEdge(createNewEdge(vertex, vertex))));

        // when
        assertTrue(getMutableGraph().removeVertex(vertex));

        // then
        assertAll(() -> assertFalse(getMutableGraph().hasEdge(vertex, vertex)),
                  () -> assertEquals(0L, getMutableGraph().numberOfEdges()),
                  () -> assertFalse(getMutableGraph().edges().findAny().isPresent()));
    }

    @Test
    void givenTwoBridgedVerticesDirectedGraphSourceTarget() {
        // precondition
        assumeTrue(edgesAreDirected());

        // given
        V source = createNewVertex();
        V target = createNewVertex();
        Edge<V> edge = createNewEdge(source, target);
        assertAll(() -> assertTrue(getMutableGraph().addVertex(source)),
                  () -> assertTrue(getMutableGraph().addVertex(target)),
                  () -> assertTrue(getMutableGraph().addEdge(edge)));

        // Then
        assertAll(() -> assertTrue(getMutableGraph().neighbours(source).contains(target)),
                  () -> assertFalse(getMutableGraph().neighbours(target).contains(source)),
                  () -> assertTrue(getMutableGraph().edges().anyMatch(Predicate.isEqual(edge))));
    }

    public abstract boolean edgesAreDirected();
}
