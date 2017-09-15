package net.syneil.graphs.constraints;

/**
 * <B>Valued:</B> true<br/>
 * <B>Labelled:</B> false<br/>
 * <B>Directed:</B> true<br/>
 * <B>Multiedged:</B> false<br/>
 *
 * @param <V> type for vertices
 */
public class UnlabelledDiGraph<V> extends ConstrainedGraph<V, Void> {
    /**
     * Constructs the empty unlabelled digraph
     */
    public UnlabelledDiGraph() {
        super(Orientation.DIRECTED, Multiplicity.SIMPLE);
    }

    /**
     * Shortcut method to add an edge given that there will be no label
     *
     * @param source the source vertex
     * @param target the target vertex
     * @return true if this graph was updated as a result of the method call
     */
    public boolean addEdge(final V source, final V target) {
        return super.addEdge(source, null, target, true);
    }
}
