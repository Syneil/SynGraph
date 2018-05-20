package net.syneil.graph.edgelist;

import net.syneil.graph.GraphTest;
import net.syneil.graph.LabelledEdge;
import net.syneil.graph.MutableGraph;
import net.syneil.graph.edge.ObjectLabelledEdge;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Tests the {@link EdgeListGraph} is a valid implementation of {@link MutableGraph}.
 */
public class EdgeListGraphTest extends GraphTest<String, // vertex
        LabelledEdge<String, String>, // edge
        EdgeListGraph<String, LabelledEdge<String, String>>> // graph impl
{
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
