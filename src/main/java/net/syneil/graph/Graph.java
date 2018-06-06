package net.syneil.graph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A graph is a data structure consisting of a set of vertices (nodes, points) connected by a set of edges (arcs,
 * lines).
 *
 * @param <V> the type used for vertices
 * @param <E> the type used for edges
 */
public interface Graph<V, E extends Edge<V>> {
    /**
     * @return true if this graph has no vertices, false otherwise
     */
    default boolean isEmpty() {
        return numberOfVertices() == 0L;
    }

    /**
     * @return the number of vertices in this graph
     */
    long numberOfVertices();

    /**
     * @return all of the edges in this graph as a stream
     */
    Stream<? extends E> edges();

    /**
     * @param vertex a vertex to find
     *
     * @return true if the vertex is a node in this graph, false otherwise
     */
    boolean hasVertex(V vertex);

    /**
     * Determines if there is an edge from the source vertex to the target vertex. If either of the parameters are not
     * in this graph, implementations should return false rather than throw an exception.
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
     * w. If the parameter is not in this graph, implementations may either return the empty set or throw a runtime
     * exception.
     *
     * @param v the vertex whose neighbours are to be found
     *
     * @return the set of every vertex in this graph such that {@link #hasEdge hasEdge(v, vertex)} is true
     */
    Set<? extends V> neighbours(V v);

    /**
     * Finds the edges from the source to the target. If either of the parameters are not in this graph, implementations
     * may either return the empty list or throw a runtime exception.
     *
     * @param source the source vertex
     * @param target the target vertex
     *
     * @return all of the edges from the source to the target
     */
    List<? extends E> getEdges(V source, V target);

    /**
     * Returns the properties of this graph. If any properties are undefined, the result should be a non-null instance
     * giving {@link Optional#empty} for the relevant getters.
     *
     * @return the properties of this graph
     */
    GraphProperties getProperties();

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
     * @return the number of edges in this graph
     */
    long numberOfEdges();

    /**
     * Returns the vertices of this graph represented as an adjacency list. Only the vertex information is returned in
     * the map, but the graph properties should honoured. That is, if the graph has {@link GraphProperties.Multiplicity
     * multiple} edges between vertices then the target vertex should be present twice in the source vertex's list.
     *
     * @return the vertices of this graph represented as an adjacency list
     */
    default Map<? extends V, ? extends List<V>> adjacencyList() {
        return vertices().collect(HashMap::new, (map, vertex) -> map.put(vertex, getEdges(vertex).stream()
                                                                                                 .map(Edge::getTarget)
                                                                                                 .collect(
                                                                                                         Collectors
                                                                                                                 .toList())),
                                  HashMap::putAll);
    }

    /**
     * @return all of the vertices in this graph as a stream
     */
    Stream<? extends V> vertices();

    /**
     * Finds the edges from the vertex. If the parameter is not part of this graph, implementations may either return
     * the empty list or throw a runtime exception.
     *
     * @param source the vertex
     *
     * @return a set of edges from the vertex
     */
    List<? extends E> getEdges(V source);

}
