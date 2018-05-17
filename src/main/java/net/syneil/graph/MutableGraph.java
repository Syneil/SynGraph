package net.syneil.graph;

public interface MutableGraph<V, E extends Edge<V>> extends Graph<V, E> {

    /**
     * Adds a vertex to this graph if it is not already a part of it
     *
     * @param v the vertex to add
     * @return true if this graph was updated as a result of this call; false otherwise
     */
    boolean addVertex(V v);

    /**
     * Removes a vertex from this graph if it is a part of it
     *
     * @param v the vertex to remove
     * @return true if the graph was updated as a result of this call; false otherwise
     */
    boolean removeVertex(V v);

    /**
     * Adds an edge to this graph.
     *
     * @param edge the predefined edge to add
     * @return true if this graph was updated as a result of this call; false otherwise
     */
    boolean addEdge(E edge);

    /**
     * Removes all edges from the source vertex to the target vertex.
     *
     * @param source the source vertex
     * @param target the target vertex
     * @return true if this graph was updated as a result of this call; false otherwise
     */
    boolean removeEdges(V source, V target);
}
