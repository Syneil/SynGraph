package net.syneil.graph;

import net.syneil.graph.edge.UnlabelledEdge;

import java.util.stream.Collectors;

public final class Graphs {
    private Graphs() {
    }

    public static <V> Graph<V, Edge<V>> unmodifiableUnlabelledGraph(Graph<V, ? extends Edge<V>> graph) {
        return new UnmodifiableGraph<>(
                graph.vertices().collect(Collectors.toSet()),
                graph.edges()
                        .map(edge -> new UnlabelledEdge<>(edge.getSource(), edge.getTarget()))
                        .collect(Collectors.toSet()));
    }
}
