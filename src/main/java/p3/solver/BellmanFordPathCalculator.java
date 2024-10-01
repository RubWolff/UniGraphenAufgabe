package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * An implementation of the {@link PathCalculator} interface that uses the Bellman-Ford algorithm to calculate the
 * shortest path between two nodes in a {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph.
 */
public class BellmanFordPathCalculator<N> implements PathCalculator<N> {

    /**
     * Factory for creating new instances of {@link BellmanFordPathCalculator}.
     */
    public static final Factory FACTORY = BellmanFordPathCalculator::new;

    /**
     * The graph to calculate paths in.
     */
    protected final Graph<N> graph;

    /**
     * The current determined distance from the start node to each node in the graph.
     */
    protected final Map<N, Integer> distances;

    /**
     * The predecessor of each node in the graph along the shortest path to the start node.
     */
    protected final Map<N, N> predecessors;

    /**
     * Creates a new {@link BellmanFordPathCalculator} for the given graph.
     *
     * @param graph the graph to calculate the shortest path in.
     */
    public BellmanFordPathCalculator(Graph<N> graph) {
        this.graph = graph;
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
    }

    @Override
    public List<N> calculatePath(N start, N end) {
        initSSSP(start);
        processGraph();
        if(hasNegativeCycle()){
            throw new CycleException("Negative cycle");
        }else{
            return reconstructPath(start, end);
        }
    }


    /**
     * Initializes the state of this single-source shortest path algorithm to its starting state.
     *
     * @param start the start node of the algorithm.
     */
    protected void initSSSP(N start) {
        for(N n : graph.getNodes()){
            distances.put(n, Integer.MAX_VALUE);
            predecessors.put(n, null);
        }
        distances.put(start, 0);
    }

    /**
     * Processes the given graph with the Bellman-Ford algorithm.
     */
    protected void processGraph() {
        for (int i = 1; i <= graph.getNodes().size()-1; i++) {
            for(Edge<N> e : graph.getEdges()){
                    relax(e);
            }
        }
    }

    /**
     * Relaxes the given edge.
     * <p>
     * An edge is relaxed by updating the entries in {@link #distances} and {@link #predecessors} for the destination
     * node if using this edge instead of the previous entries results in a shorter path to the destination node.
     *
     * @param edge the edge to relax.
     */
    protected void relax(Edge<N> edge) {
        if(distances.get(edge.to()) > distances.get(edge.from()) + edge.weight() && distances.get(edge.from()) != Integer.MAX_VALUE) {
            distances.put(edge.to(), distances.get(edge.from()) + edge.weight());
            predecessors.put(edge.to(), edge.from());
        }
    }

    /**
     * Determines if the graph contains any edges that cause a negative cycle within the graph.
     *
     * @return {@code true} if the graph contains a negative cycle, {@code false} otherwise.
     */
    protected boolean hasNegativeCycle() {
        for(Edge<N> edge : graph.getEdges()) {
            if(distances.get(edge.to()) > distances.get(edge.from()) + edge.weight()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reconstructs the path calculated by the Bellman-Ford algorithm from the start node to the end node by
     * using the {@link #predecessors} map.
     * <p>
     * The path is returned as a list of nodes, starting with the start node and ending with the end node.
     *
     * @param start the start node of the path.
     * @param end   the end node of the path.
     * @return A list of nodes representing the shortest path from the start node to the end node.
     */
    protected List<N> reconstructPath(N start, N end) {
        LinkedList<N> shortestPath = new LinkedList<>();
        N current = end;
        while (!current.equals(start)) {
            shortestPath.addFirst(current);
            current = predecessors.get(current);
        }
        shortestPath.addFirst(start);
        return shortestPath;
    }
}
