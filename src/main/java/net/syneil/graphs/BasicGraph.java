package net.syneil.graphs;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * generic vertices,
 * no edge labels,
 * no multi-edges,
 * self-edges permitted,
 * directed edges,
 * maximum number of vertices defined at instantiation
 *
 * @param <V>
 */
public class BasicGraph<V> implements Graph<V, Void> {
    private final boolean[][] matrix;
    private final V[] vertices;

    public BasicGraph(int maxVertexCount) {
        this.matrix = new boolean[maxVertexCount][maxVertexCount];
        this.vertices = (V[]) new Object[maxVertexCount];
    }

    @Override
    public long numberOfVertices() {
        return Arrays.stream(vertices).filter(Objects::nonNull).count();
    }

    @Override
    public long numberOfEdges() {
        return Arrays.stream(matrix).mapToInt(row -> {
            int count = 0;
            for (boolean aRow : row) if (aRow) count++;
            return count;
        }).sum();
    }

    @Override
    public Stream<? extends V> vertices() {
        return Arrays.stream(vertices).filter(Objects::nonNull);
    }

    @Override
    public Stream<? extends Edge<V, Void>> edges() {
        return IntStream.range(0, matrix.length).boxed().flatMap(this::collectEdgeStreams);
    }

    @Override
    public boolean hasVertex(V vertex) {
        return vertices().anyMatch(vertex::equals);
    }

    @Override
    public boolean hasEdge(V source, V target) {
        return edges().anyMatch(edge -> Objects.equals(edge.getSource(), source)
                && Objects.equals(edge.getTarget(), target));
    }

    @Override
    public Set<? extends V> neighbours(V v) {
        return new HashSet<>(adjacencyList().get(v));
    }

    @Override
    public boolean addVertex(V v) {
        return !hasVertex(v) && IntStream.range(0, vertices.length)
                .filter(i -> vertices[i] == null)
                .peek(i -> vertices[i] = v)
                .findFirst()
                .isPresent();
    }

    @Override
    public boolean removeVertex(V v) {
        return IntStream.range(0, vertices.length)
                .filter(i -> Objects.equals(v, vertices[i]))
                .peek(i -> vertices[i] = null)
                .peek(i -> matrix[i] = new boolean[vertices.length])
                .count() > 0;
    }

    @Override
    public boolean addEdge(V source, Void label, V target, boolean directed) {
        Optional<Integer> sourceIndexOpt = vertexIndex(source);
        if (!sourceIndexOpt.isPresent()) {
            return false;
        }
        int sourceIndex = sourceIndexOpt.get();
        if (Objects.equals(source, target)) {
            if (!matrix[sourceIndex][sourceIndex]) {
                matrix[sourceIndex][sourceIndex] = true;
                return true;
            }
            return false;
        }
        Optional<Integer> targetIndexOpt = vertexIndex(target);
        if (targetIndexOpt.isPresent()) {
            int targetIndex = targetIndexOpt.get();
            if (!matrix[sourceIndex][targetIndex]) {
                matrix[sourceIndex][targetIndex] = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<? extends Edge<V, Void>> getEdges(V source, V target) {
        Optional<Integer> sourceIndexOpt = vertexIndex(source);
        Optional<Integer> targetIndexOpt = vertexIndex(target);
        if (sourceIndexOpt.isPresent() && targetIndexOpt.isPresent()) {
            int sourceIndex = sourceIndexOpt.get();
            int targetIndex = targetIndexOpt.get();
            if (matrix[sourceIndex][targetIndex]) {
                return Collections.singleton(createEdge(source, target));
            }
        }
        return Collections.emptySet();
    }

    @Override
    public Set<? extends Edge<V, Void>> getEdges(V source) {
        Optional<Integer> indexOpt = vertexIndex(source);
        if (indexOpt.isPresent()) {
            int index = indexOpt.get();
            return IntStream.range(0, matrix[index].length)
                    .filter(i -> matrix[index][i])
                    .mapToObj(i -> vertices[i])
                    .map(target -> createEdge(source, target))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public boolean removeEdges(V source, V target) {
        Optional<Integer> sourceIndexOpt = vertexIndex(source);
        Optional<Integer> targetIndexOpt = vertexIndex(target);
        if (sourceIndexOpt.isPresent() && targetIndexOpt.isPresent()) {
            int sourceIndex = sourceIndexOpt.get();
            int targetIndex = targetIndexOpt.get();
            if (matrix[sourceIndex][targetIndex]) {
                matrix[sourceIndex][targetIndex] = false;
                return true;
            }
        }
        return false;
    }

    private Optional<Integer> vertexIndex(V vertex) {
        return IntStream.range(0, vertices.length)
                .filter(i -> Objects.equals(vertex, vertices[i]))
                .boxed()
                .findAny();
    }

    private Stream<? extends Edge<V, Void>> collectEdgeStreams(Integer i) {
        return IntStream.range(0, matrix.length).filter(j -> matrix[i][j]).mapToObj(getEdgeIntFunction(vertices[i]));
    }

    private IntFunction<Edge<V, Void>> getEdgeIntFunction(V source) {
        return j -> createEdge(source, vertices[j]);
    }

    private Edge<V, Void> createEdge(V source, V target) {
        return new Edge<V, Void>() {
            @Override
            public boolean isDirected() {
                return false;
            }

            @Override
            public boolean connects(V vertex) {
                return Objects.equals(vertex, source) || Objects.equals(vertex, target);
            }

            @Override
            public Optional<Void> getLabel() {
                return Optional.empty();
            }

            @Override
            public boolean hasSource(V vertex) {
                return Objects.equals(vertex, source);
            }

            @Override
            public boolean hasTarget(V vertex) {
                return Objects.equals(vertex, target);
            }

            @Override
            public V getSource() {
                return source;
            }

            @Override
            public V getTarget() {
                return target;
            }
        };
    }
}
