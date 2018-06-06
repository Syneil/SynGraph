package net.syneil.graph;

import java.util.stream.Collectors;

public final class Graphs {
    private Graphs() {
    }

    @SuppressWarnings("unchecked")
    public static <V> UnmodifiableGraph<V, Edge<V>> unmodifiableGraph(Graph<V, Edge<V>> graph) {
        return new UnmodifiableGraph<>(graph.vertices().collect(Collectors.toSet()),
                                       graph.edges().collect(Collectors.toSet()));
    }
}
