package net.syneil.graphs.analysis;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.syneil.graphs.Graph;
import net.syneil.graphs.Graph.Edge;
import net.syneil.graphs.GraphView;
import net.syneil.graphs.constraints.GraphConstraint;
import net.syneil.graphs.generation.GraphGenerator;
import net.syneil.streams.StreamUtils;

/**
 * Utility class providing methods for analysing graphs.
 */
public class GraphAnalyser {
    /**
     * @return a constraint that a graph be connected
     */
    public static final <V, E> GraphConstraint<V, E> connectedConstraint() {
        return new GraphConstraint<>("Connected", g -> !disjointed(g), "A connected graph may not be disjointed");
    }

    /**
     * Determines if a graph is disjointed. A graph is disjointed if there exist two vertices in the graph between which
     * no path exists.
     *
     * @param g the graph to analyse
     * @return true if the graph is disjointed, false otherwise
     */
    public static <V, E> boolean disjointed(final Graph<V, E> g) {
        // trivial case when graph is empty or has just one node
        if (g.numberOfVertices() < 2) {
            return false;
        }

        // trivial case when |V|>|E|+1
        if (g.numberOfVertices() > g.numberOfEdges() + 1) {
            return true;
        }

        // trivial cases have been eliminated; test further
        final Set<Set<V>> vertexGroups = g.vertices().map(Arrays::asList) // essentially "collects" the stream elements
                .map(HashSet<V>::new) // put each vertex in its own set
                .collect(Collectors.toSet()); // collect into a set of singletons

        StreamUtils.crossProduct(g::vertices, g::vertices) // find every vertex combination
                .filter(vs -> g.hasEdge(vs[0], vs[1])) // for which there is an edge
                .forEach(vs -> mergeSets(vertexGroups, vs[0], vs[1])); // and merge the two vertex sets together

        // a connected graph would yield a single set with all of its vertices
        // so if there are more than one set still left unmerged, the graph is disjointed
        return vertexGroups.size() > 1;
    }

    /**
     * Merges the sets containing each vertex
     *
     * @param groups the set of merged vertex sets
     * @param source one of the vertices
     * @param target one of the vertices
     *
     * @throws NoSuchElementException if either of the vertices cannot be found in the groups
     */
    private static <V> void mergeSets(final Set<Set<V>> groups, final V source, final V target) {
        // Find the sets that contain the two vertices
        // throws a NoSuchElementException if either vertex is not in the groups
        final Set<V> sourceSet = groups.stream().filter(group -> group.contains(source)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        final Set<V> targetSet = groups.stream().filter(group -> group.contains(target)).findFirst()
                .orElseThrow(NoSuchElementException::new);

        // if the two vertices are already merged into the same set, nothing to do
        if (sourceSet == targetSet) {
            return;
        }

        // else remove the two groups from the parent set, concatenate them, and put the result back in
        groups.remove(sourceSet);
        groups.remove(targetSet);
        final Set<V> mergedSet = Stream.concat(sourceSet.stream(), targetSet.stream()).collect(toSet());
        groups.add(mergedSet);
    }

    /**
     * Tests if an edge in a graph is a bridge. An edge is a bridge if its removal transforms a connected graph into a
     * disconnected (or "disjointed") one. If the graph is already disjointed, the edge cannot be a bridge, and if the
     * edge is not actually a part of the graph, this method simply returns false.
     *
     * @param graph the graph
     * @param edge the edge within the graph to test for being a bridge
     * @return true if the edge is a bridge in the graph, false otherwise
     */
    public static <V, E> boolean isBridge(final Graph<V, E> graph, final Edge<V, E> edge) {
        if (disjointed(graph)) {
            return false;
        }
        if (graph.edges().noneMatch(e -> Objects.equals(e, edge))) {
            return false;
        }

        return disjointed(GraphView.ofEdges(graph, edges -> edges.filter(e -> !Objects.equals(edge, e))));
    }

    /**
     * Tests if the graph has any bridges. An edge is a bridge if its removal transforms a connected graph into a
     * disconnected (or "disjointed" one. If the graph is already disjointed, none of its edges can be bridges.
     *
     * @param graph the graph to explore for bridges
     * @return true if the graph contains any bridges, false otherwise
     */
    public static <V, E> boolean hasBridge(final Graph<V, E> graph) {
        return !disjointed(graph) && graph.edges().anyMatch(e -> isBridge(graph, e));
    }

    /**
     * Tests if a vertex in a graph is an articulation point. A vertex is an articulation point if its removal
     * transforms a connected graph into a disconnected (or "disjointed") one. If the graph is already disjointed, the
     * vertex cannot be an articulation point, and if the vertex is not actually a part of the graph, this method simply
     * returns false.
     *
     * @param graph the graph
     * @param vertex the vertex within the graph to test for being an articulation point
     * @return true if the vertex is an articulation point in the graph, false otherwise
     */
    public static <V, E> boolean isArticulationPoint(final Graph<V, E> graph, final V vertex) {
        if (!graph.hasVertex(vertex)) {
            return false;
        }
        if (disjointed(graph)) {
            return false;
        }
        return disjointed(GraphView.withoutVertex(graph, vertex));
    }

    /**
     * Tests if the graph has any articulation points. A vertex is an articulation point if its removal transforms a
     * connected graph into a disconnected (or "disjointed") one. If the graph is already disjointed, none of its
     * vertices can be articulation points.
     *
     * @param graph the graph to explore for articulation points
     * @return true if the graph contains any articulation points, false otherwise
     */
    public static <V, E> boolean hasArticulationPoint(final Graph<V, E> graph) {
        return graph.vertices().anyMatch(v -> isArticulationPoint(graph, v));
    }

    public static <V, E> boolean hasCycle(final Graph<V, E> graph) {
        // a cycle exists in the graph if a depth-first search from all vertices ever encounters a back-path
        // i.e. a vertex that has already been visited becomes visible

        return graph.vertices().anyMatch(root -> {
            // TODO
            return false;
        });
    }

    public static <V, E> boolean isAcyclic(final Graph<V, E> graph) {
        throw new RuntimeException("not implemented");
    }

    /**
     * Tests if a graph has any path between two vertices. Note that if the source and the target are
     * {@link Object#equals equal} then this method considers the path trivially true.
     *
     * @param graph the graph to inspect
     * @param source the source vertex
     * @param target the target vertex
     * @return true if there is a path in the graph from the source to the target
     */
    public static <V, E> boolean hasPath(final Graph<V, E> graph, final V source, final V target) {
        if (!graph.hasVertex(source)) {
            return false;
        }
        if (!graph.hasVertex(target)) {
            return false;
        }
        if (Objects.equals(source, target)) {
            return true;
        }

        Set<V> history = new HashSet<>();
        Queue<V> horizon = new LinkedList<>();
        horizon.add(source);
        while (!horizon.isEmpty()) {
            V inspect = horizon.poll();
            history.add(inspect);
            if (Objects.equals(inspect, target)) {
                return true;
            }

            Set<V> visible = graph.neighbours(inspect);
            visible.removeAll(history);
            horizon.addAll(visible);
        }

        return false;
    }

    /**
     * Tries to find a path in a graph from the source to the target and returns it in an Optional. The optional will be
     * empty if no path could be found. Note that if the source and the target are {@link Object#equals equal} then this
     * method returns a path with just that vertex.
     *
     * @param graph the graph to inspect
     * @param source the source vertex
     * @param target the target vertex
     * @return an optional containing a path from the source to the vertex, if one exists, otherwise empty.
     */
    public static <V, E> Optional<GraphView<V, E>> getPath(final Graph<V, E> graph, final V source, final V target) {
        // trivial cases
        if (!graph.hasVertex(source)) {
            return Optional.empty();
        }
        if (!graph.hasVertex(target)) {
            return Optional.empty();
        }
        if (Objects.equals(source, target)) {
            return Optional.of(new GraphView<>(graph, s -> Stream.of(source), e -> Stream.empty()));
        }

        // normal case
        Queue<List<V>> horizon = new LinkedList<>();
        horizon.add(Collections.singletonList(source));

        Set<V> ignores = new HashSet<>();
        ignores.add(source);

        while (!horizon.isEmpty()) {
            List<V> inspect = horizon.poll();
            V last = inspect.get(inspect.size() - 1);
            if (Objects.equals(last, target)) {
                // found the target
                return Optional.of(new GraphView<V, E>(graph, vs -> vs.filter(inspect::contains),
                        es -> es.filter(e -> inspect.contains(e.getSource()) && inspect.contains(e.getTarget())
                                && inspect.indexOf(e.getTarget()) > inspect.indexOf(e.getSource()))));
            }

            Set<V> neighbours = graph.neighbours(last);
            neighbours.removeAll(ignores);
            ignores.addAll(neighbours);
            neighbours.stream().map(v -> Stream.concat(inspect.stream(), Stream.of(v)).collect(toList()))
                    .forEach(horizon::offer);
        }

        return Optional.empty();
    }

    public static <V, E> Optional<GraphView<V, E>> depthFirstSearch(final Graph<V, E> graph, final V root,
            final V target) {
        final List<V> result = depthFirstSearch(graph, new ArrayList<>(), root, target,
                                                new HashSet<>(Arrays.asList(root)));
        return Optional.ofNullable(result == null ? null
                : new GraphView<V, E>(graph, vs -> vs.filter(result::contains),
                        es -> es.filter(e -> result.contains(e.getSource()) && result.contains(e.getTarget())
                                && result.indexOf(e.getTarget()) == result.indexOf(e.getSource()) + 1)));
    }

    private static <V, E> List<V> depthFirstSearch(final Graph<V, E> graph, final List<V> history, final V start,
            final V target, final Set<V> ignores) {
        System.out.println("Visiting " + start);
        if (Objects.equals(start, target)) {
            return concat(history.stream(), Stream.of(start)).collect(toList());
        }

        final Set<V> neighbours = graph.neighbours(start);
        if (neighbours.contains(target)) {
            return concat(history.stream(), Stream.of(start, target)).collect(toList());
        }
        neighbours.removeAll(ignores);

        if (neighbours.isEmpty()) {
            return null;
        }

        List<V> newHistory = Stream.concat(history.stream(), Stream.of(start)).collect(toList());
        ignores.add(start);

        return neighbours.stream().map(n -> depthFirstSearch(graph, newHistory, n, target, ignores))
                .filter(Objects::nonNull).findAny().orElse(null);
    }

    /**
     * Hidden constructor
     */
    private GraphAnalyser() {
    }

    public static void main(final String[] args) {
        Graph<Integer, Void> g = GraphGenerator.apollonian(8);
        Optional<GraphView<Integer, Void>> path = depthFirstSearch(g, 0, (int) g.vertices().count() / 2 - 1);
        System.out.println(path.isPresent());
        System.out.println(path.get().numberOfVertices());
        System.out.println(path.get().numberOfEdges());
        path.ifPresent(gv -> gv.edges().forEach(System.out::println));
    }
}
