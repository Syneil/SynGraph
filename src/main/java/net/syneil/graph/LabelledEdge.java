package net.syneil.graph;

public interface LabelledEdge<V, E> extends Edge<V> {
    E getLabel();
}
