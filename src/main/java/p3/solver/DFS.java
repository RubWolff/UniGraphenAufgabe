package p3.solver;

import p3.graph.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjIntConsumer;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * An implementation of the {@link GraphTraverser} interface that uses the Depth-First Search algorithm to traverse
 * the graph
 *
 * @param <N> the type of the nodes in the graph.
 */
public class DFS<N> implements GraphTraverser<N> {

    /**
     * Factory for creating new instances of {@link DFS}.
     */
    public static final Factory FACTORY = DFS::new;

    /**
     * The graph to traverse.
     */
    protected final Graph<N> graph;

    /**
     * Stores the current color of each node in the graph.
     */
    protected final Map<N, Color> colors;

    /**
     * Stores the discovery time of each node in the graph. It only contains values for nodes that have been discovered.
     */
    protected final Map<N, Integer> discoveryTimes;

    /**
     * Stores the finish time of each node in the graph. It only contains values for nodes that have been finished.
     */
    protected final Map<N, Integer> finishTimes;

    /**
     * Stores the used predecessor of each node in the graph. It only contains values for nodes that have been discovered.
     */
    protected final Map<N, N> predecessors = new HashMap<>();

    /**
     * Stores the current time during the visiting by the DFS algorithm.
     */
    protected int time = 0;

    /**
     * Stores whether a cycle has been detected during the traversal of the graph.
     */
    protected boolean cyclic = false;

    /**
     * Creates a new {@link DFS} for the given graph.
     *
     * @param graph the graph to traverse.
     */
    public DFS(Graph<N> graph) {
        this.graph = graph;
        this.colors = new HashMap<>();
        this.discoveryTimes = new HashMap<>();
        this.finishTimes = new HashMap<>();
    }

    @Override
    public void traverse(ObjIntConsumer<N> consumer) {
        init();
        for (N node : graph.getNodes()) {
            if(colors.get(node) == Color.WHITE){
                visit(consumer, node);
            }
        }
    }

    /**
     * Checks whether the graph contains negative cycles.
     * <p>
     * The result is only valid for the last traversal of the graph. If the graph has been changed since the last
     * traversal, the result may be incorrect. If the graph has not been traversed yet, the result is always {@code false}.
     *
     * @return {@code true} if graph contains negative cycles, {@code false} otherwise.
     */
    public boolean isCyclic() {
        return cyclic;
    }


    /**
     * Initializes the DFS algorithm to its starting state, i.e., the maps are cleared, all nodes are colored white and
     * the time is set to 0.
     */
    protected void init() {
        for (N n : graph.getNodes()) {
            colors.put(n, Color.WHITE);
            predecessors.put(n, null);
            discoveryTimes.put(n, 0);
            finishTimes.put(n, 0);
        }
        time = 0;
        cyclic = false;
    }

    /**
     * Visits a new node in the graph.
     * <p>
     * A node is visited by first discovering it and coloring it gray and, afterward, recursively visiting all its neighbors.
     * After all neighbors have been visited, the node is finished, i.e., colored black and passed to the consumer.
     *
     * @param consumer Function that accepts the node and its finish time.
     * @param current  Node that is processed by this method
     */
    protected void visit(ObjIntConsumer<N> consumer, N current) {
        time = time + 1;
        discoveryTimes.put(current, time);
        colors.put(current, Color.GRAY);
        for(N n: graph.getAdjacentNodes(current)){
            if(colors.get(n) == Color.WHITE){
                predecessors.put(n, current);
                visit(consumer, n);
            }else if (colors.get(n) == Color.GRAY){
                cyclic = true;
            }
        }
        colors.put(current, Color.BLACK);
        time = time+1;
        consumer.accept(current, time);
        finishTimes.put(current, time);
    }

    /**
     * The colors used to mark the state of a node during the DFS algorithm.
     */
    protected enum Color {

        /**
         * The node has not been discovered yet. This is the initial state of all nodes.
         */
        WHITE,

        /**
         * The node has been discovered but not yet finished. This means that the node, or one of its neighbors
         * recursively, is currently being processed.
         */
        GRAY,

        /**
         * The node has been discovered and finished. This means that the node and its previously {@link #WHITE}
         * neighbors have been fully processed.
         */
        BLACK
    }
}
