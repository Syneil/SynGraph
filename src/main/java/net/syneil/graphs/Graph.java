package net.syneil.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.syneil.graphs.constraints.Multiplicity;
import net.syneil.graphs.constraints.Orientation;

/**
 * A graph is a data structure consisting of a set of vertices (nodes, points) connected by a set of edges (arcs,
 * lines).
 * <p>
 * Graphs are distinguished by two primary properties: {@link Orientation orientation} and
 * {@link Multiplicity multiplicity}.
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 */
public interface Graph<V, E> {
    /** @return the number of vertices in this graph */
    long numberOfVertices();

    /** @return the number of edges in this graph */
    long numberOfEdges();

    /** @return all of the vertices in this graph as a stream */
    Stream<V> vertices();

    /** @return all of the edges in this graph as a stream */
    Stream<? extends Edge<V, E>> edges();

    /**
     * @param vertex a vertex to find
     * @return true if the vertex is a node in this graph, false otherwise
     */
    boolean hasVertex(V vertex);

    /**
     * Determines if there is an edge from the source vertex to the target vertex. Note that for undirected edges, the
     * notions of "source" and "target" vertices are irrelevant.
     *
     * @param source the source vertex
     * @param target the target vertex
     *
     * @return true if there is an edge from the source to the target
     */
    boolean hasEdge(V source, V target);

    /**
     * Finds the neighbours of a vertex in this graph. If the vertex is not a member of this graph, the empty set is
     * returned. A neighbour of a vertex v is any vertex w in this graph for which there is a traversible edge from v to
     * w.
     *
     * @param v the vertex whose neighbours are to be found
     * @return the set of every vertex in this graph such that {@link #hasEdge hasEdge(v, vertex)} is true
     */
    Set<V> neighbours(V v);

    /**
     * Adds a vertex to this graph if it is not already a part of it
     *
     * @param v the vertex to add
     * @return true if this graph was updated as a result of this call; false otherwise (the vertex was already in this
     *         graph)
     */
    boolean addVertex(V v);

    /**
     * Removes a vertex from this graph if it is a part of it
     *
     * @param v the vertex to remove
     * @return true if the graph was updated as a result of this call; false otherwise (the vertex was not in this
     *         graph)
     */
    boolean removeVertex(V v);

    /**
     * Adds an edge from v1 to v2 with the given label. Note that for undirected edges, the notions of "source" and
     * "target" vertices are irrelevant
     *
     * @param source the source vertex
     * @param label the edge label
     * @param target the target vertex
     * @param directed true if the edge is directed, false otherwise
     * @return true if this graph was updated as a result of this call; false otherwise (the edge was already in this
     *         graph and cannot be repeated)
     */
    boolean addEdge(V source, E label, V target, boolean directed);

    /**
     * Finds the edges from the source to the target. Note that for undirected edges, the notions of "source" and
     * "target" vertices are irrelevant
     *
     * @param source the source vertex
     * @param target the target vertex
     * @return all of the edges from the source to the target
     */
    Set<? extends Edge<V, E>> getEdges(V source, V target);

    /**
     * Finds the edges from the vertex. This will include directed edges with the vertex as the source, and undirected
     * edges attached to the vertex.
     *
     * @param source the vertex
     * @return a set of edges from the vertex
     */
    Set<? extends Edge<V, E>> getEdges(V source);

    /**
     * Removes all edges from the source vertex to the target vertex. Note that for undirected edges, the notions of
     * "source" and "target" vertices are irrelevant.
     *
     * @param source the source vertex
     * @param target the target vertex
     * @return true if this graph was updated as a result of this call; false otherwise (no such edges were in this
     *         graph)
     */
    boolean removeEdges(V source, V target);

    /**
     * Returns the density of this graph. By default, for a graph {@code G = (V, E)} where {@code V} is the set of
     * vertices and {@code E} is the set of edges, the density is calculated as {@code |E|/(|V|*(|V|-1))}
     *
     * @return the density of this graph
     */
    default double density() {
        double vs = numberOfVertices();
        double es = numberOfEdges();
        return es / (vs * (vs - 1));
    }

    /**
     * @return the vertices of this graph represented as an adjacency list (without edge labels)
     */
    default Map<V, List<V>> adjacencyList() {
        return vertices().collect(HashMap::new, (map, vertex) -> map.put(vertex, new ArrayList<>(neighbours(vertex))),
                                  null);
    }

    /**
     * An edge in the graph.
     *
     * @param <V> the type used for vertices
     * @param <E> the type used for edge labels
     */
    public interface Edge<V, E> {
        /** @return true if this edge is directed, false otherwise */
        boolean isDirected();

        /**
         * @param vertex a vertex to examine
         * @return true if the vertex is one of the vertices this edge connects, false otherwise
         */
        boolean connects(V vertex);

        /** @return the label attached to this edge, optionally */
        Optional<E> getLabel();

        /**
         * @param vertex the vertex to compare
         * @return true if this edge {@link #isDirected} and the vertex is this edge's source, or if this edge is
         *         undirected and {@link #connects} the vertex; false if neither of these are the case
         */
        boolean hasSource(V vertex);

        /**
         * @param vertex the vertex to compare
         * @return true if this edge {@link #isDirected} and the vertex is this edge's target, or if this edge is
         *         undirected and {@link #connects} the vertex; false if neither of these are the case
         */
        boolean hasTarget(V vertex);

        /**
         * @return the source vertex of this edge if this edge {@link #isDirected}, or one of the vertices this edge
         *         {@link #connects} otherwise (must be the other end of this edge to that returned by
         *         {@link #getTarget}).
         */
        V getSource();

        /**
         * @return the target vertex of this edge if this edge {@link #isDirected}, or one of the vertices this edge
         *         {@link #connects} otherwise (must be the other end of this edge to that returned by
         *         {@link #getSource}).
         */
        V getTarget();

        /**
         * @param vertex one of the vertices this edge connects (otherwise the empty optional will be returned)
         * @return the "other" vertex that this edge connects, or the empty optional if this edge does not connect the
         *         vertex
         */
        default Optional<V> other(final V vertex) {
            Optional<V> result = Optional.empty();
            if (Objects.equals(getSource(), vertex)) {
                result = Optional.of(getTarget());
            } else if (Objects.equals(getTarget(), vertex)) {
                result = Optional.of(getSource());
            }
            return result;
        }
    }
}
