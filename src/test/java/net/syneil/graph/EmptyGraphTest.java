package net.syneil.graph;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Provides common tests for {@link Graph} implementations to verify behaviour when empty.
 *
 * @param <V> the type used for vertices
 */
public abstract class EmptyGraphTest<V> {

    public abstract Edge<V> createNewEdge(V source, V target);

    @Test
    void shouldHaveExpectedDensity() {
        assertEquals(Double.NaN, getEmptyGraph().density());
    }

    public abstract Graph<V, Edge<V>> getEmptyGraph();

    @Test
    void shouldHaveZeroNumberOfVertices() {
        assertEquals(0L, getEmptyGraph().numberOfVertices());
    }

    @Test
    void shouldHaveZeroNumberOfEdges() {
        assertEquals(0L, getEmptyGraph().numberOfEdges());
    }

    @Test
    void shouldBeEmpty() {
        assertTrue(getEmptyGraph().isEmpty());
    }

    @Test
    void verticesShouldBeEmptyStream() {
        assertFalse(getEmptyGraph().vertices().findAny().isPresent());
    }

    @Test
    void edgesShouldBeEmptyStream() {
        assertFalse(getEmptyGraph().edges().findAny().isPresent());
    }

    @Test
    void adjacencyListShouldBeEmptyMap() {
        assertEquals(Collections.emptyMap(), getEmptyGraph().adjacencyList());
    }

    @Test
    void shouldNotHaveVertex() {
        assertFalse(getEmptyGraph().hasVertex(createNewVertex()));
    }

    public abstract V createNewVertex();

    @Test
    void shouldNotHaveEdge() {
        assertFalse(getEmptyGraph().hasEdge(createNewVertex(), createNewVertex()));
    }

    @Test
    void shouldProvideProperties() {
        assertNotNull(getEmptyGraph().getProperties());
    }
}
