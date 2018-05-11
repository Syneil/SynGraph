package net.syneil.graphs;

import java.util.UUID;
import java.util.function.Supplier;

public class BasicGraphTest extends GraphTest<String, Void, BasicGraph<String>> {
    @Override
    BasicGraph<String> getEmptyGraph() {
        return new BasicGraph<>(10);
    }

    @Override
    Supplier<String> getVertexSupplier() {
        return () -> UUID.randomUUID().toString();
    }

    @Override
    Supplier<Void> getEdgeLabelSupplier() {
        return () -> null;
    }
}
