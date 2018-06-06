package net.syneil.graph.edgelist;

import net.syneil.graph.*;
import net.syneil.graph.edge.UnlabelledEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.util.Optional;
import java.util.UUID;

class EdgeListGraphTest {
    private MutableGraph<UUID, Edge<UUID>> graph;

    @BeforeEach
    void createEmptyGraph() {
        graph = Optional.ofNullable(graph).orElseGet(EdgeListGraph::new);
    }

    @Nested
    class WhenEmpty extends EmptyGraphTest<UUID> {
        @Override
        public Graph<UUID, Edge<UUID>> getEmptyGraph() {
            return graph;
        }

        @Override
        public UUID createNewVertex() {
            return UUID.randomUUID();
        }

        @Override
        public Edge<UUID> createNewEdge(UUID source, UUID target) {
            return new UnlabelledEdge<>(source, target);
        }
    }

    @Nested
    class AsMutable extends MutableGraphTest<UUID> {
        @Override
        public MutableGraph<UUID, Edge<UUID>> getMutableGraph() {
            return graph;
        }

        @Override
        public UUID createNewVertex() {
            return UUID.randomUUID();
        }

        @Override
        public Edge<UUID> createNewEdge(UUID source, UUID target) {
            return new UnlabelledEdge<>(source, target);
        }

        @Override
        public boolean loopsPermitted() {
            return true;
        }

        @Override
        public boolean multipleEdgesPermitted() {
            return true;
        }

        @Override
        public boolean edgesAreDirected() {
            return true;
        }
    }
}
