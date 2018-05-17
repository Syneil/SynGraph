package net.syneil.graph;

import net.syneil.graph.edge.ObjectLabelledEdge;
import net.syneil.graph.edgelist.EdgeListGraph;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EdgeGraphTest extends GraphTest<String, LabelledEdge<String, String>, EdgeListGraph<String, LabelledEdge<String, String>>> {
    @Override
    protected EdgeListGraph<String, LabelledEdge<String, String>> getEmptyGraph() {
        return new EdgeListGraph<>();
    }

    @Override
    protected Supplier<String> getVertexSupplier() {
        return () -> UUID.randomUUID().toString();
    }

    @Override
    protected BiFunction<String, String, LabelledEdge<String, String>> getEdgeGenerator() {
        return (source, target) -> new ObjectLabelledEdge<>(source, target, UUID.randomUUID().toString());
    }
}
