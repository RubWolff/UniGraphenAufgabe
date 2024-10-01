package p3.solver;

import p3.graph.Edge;
import p3.graph.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * An implementation of the {@link MSTCalculator} interface that uses the Prim algorithm to calculate the minimum
 * spanning tree of a {@link Graph}.
 *
 * @param <N> the type of the nodes in the graph.
 */
public class PrimMSTCalculator<N> implements MSTCalculator<N> {

    /**
     * Factory for creating new instances of {@link PrimMSTCalculator}.
     */
    public static final Factory FACTORY = PrimMSTCalculator::new;

    /**
     * The graph to calculate the MST for.
     */
    protected final Graph<N> graph;

    /**
     * Stores the current predecessor for each node.
     */
    protected final Map<N, N> predecessors;

    /**
     * Stores the weight of the edge from the current predecessor of each node to that node.
     */
    protected final Map<N, Integer> keys;

    /**
     * Stores the nodes that have not yet been visited by the algorithm.
     */
    protected final Set<N> remainingNodes;

    /**
     * Creates a new {@link PrimMSTCalculator} for the given graph.
     *
     * @param graph the graph to calculate the MST for.
     */
    public PrimMSTCalculator(Graph<N> graph) {
        this.graph = graph;
        this.predecessors = new HashMap<>();
        this.keys = new HashMap<>();
        this.remainingNodes = new HashSet<>();
    }

    @Override
    public Graph<N> calculateMST(N root) {
        init(root);
        while (!remainingNodes.isEmpty()) {
            N node = extractMin();
            processNode(node);
        }
        return Graph.of(graph.getNodes(), calculateMSTEdges());
    }

    /**
     * Processes the current node with the prim algorithm
     *
     * @param node current node processed by the algorithm
     */
    protected void processNode(N node) {
        for(N n : predecessors.keySet()) {
            if(remainingNodes.contains(n) && graph.getEdge(node, n).weight() < keys.get(n) ) {
                keys.put(n, graph.getEdge(node, n).weight());
                predecessors.put(n, node);
            }
        }
    }

    /**
     * Initializes the fields before executing the prim algorithm
     *
     * @param root the root node of the calculated mst.
     */
    protected void init(N root) {
        for(N node : graph.getNodes()){
            predecessors.put(node, null);
            keys.put(node, Integer.MAX_VALUE);
            remainingNodes.add(node);
        }
        keys.put(root, Integer.MIN_VALUE);

    }

    /**
     * Extracts the node with the smallest key from the remaining nodes and removes it from the set.
     *
     * @return the node in the remaining nodes set with the smallest key.
     */
    protected N extractMin() {
        N min = null;
        int max = Integer.MAX_VALUE;
        for(N node : remainingNodes){
            if(keys.get(node) <= max){
                max = keys.get(node);
                min = node;
            }
        }
        remainingNodes.remove(min);
        return min;
    }

    /**
     * Calculates the edges of the minimum spanning tree using the previously calculated information in the predecessors
     * and keys maps.
     *
     * @return the edges of the minimum spanning tree
     */
    protected Set<Edge<N>> calculateMSTEdges() {
        Set<Edge<N>> edges = new HashSet<>();
        for(N node : graph.getNodes()){
            edges.add(graph.getEdge(node, predecessors.get(node)));
        }
        return edges;
    }
}
