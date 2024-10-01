package p3.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A mutable, directed, weighted graph that uses an {@link AdjacencyRepresentation} to store the graph.
 *
 * @param <N> the type of the nodes in this graph.
 * @see Graph
 * @see MutableGraph
 * @see AdjacencyRepresentation
 */
public class AdjacencyGraph<N> implements MutableGraph<N> {

    /**
     * The {@link AdjacencyRepresentation} that stores the graph.
     */
    private final AdjacencyRepresentation representation;

    /**
     * A map that associates each node with its weight.
     */
    private final Map<N, Map<N, Integer>> weights = new HashMap<>();

    /**
     * A map from nodes to their indices in the adjacency matrix.
     * Every node in the graph is mapped to a distinct index in the range [0, {@link #representation}.size() -1].
     * This map is the inverse of {@link #indexToNode}.
     */
    private final Map<N, Integer> nodeToIndex = new HashMap<>();

    /**
     * A map from indices in the adjacency matrix to the nodes they represent.
     * Every index in the range [0, {@link #representation}.size() -1] is mapped to a distinct node in the graph.
     * This map is the inverse of {@link #nodeToIndex}.
     */
    private final Map<Integer, N> indexToNode = new HashMap<>();

    /**
     * Constructs a new {@link AdjacencyGraph} which initially contains the given nodes and edges.
     * <p>
     * It uses the object created by the given representationFactory to store the graph.
     *
     * @param nodes                 the initial set of nodes.
     * @param edges                 the initial set of edges.
     * @param representationFactory a factory that creates an {@link AdjacencyRepresentation} with the given size.
     */
    public AdjacencyGraph(Set<N> nodes, Set<Edge<N>> edges, AdjacencyRepresentation.Factory representationFactory) {
        representation = representationFactory.create(nodes.size());
        for(N node : nodes) {
            addNode(node);
        }
        for(Edge<N> edge : edges) {
            addEdge(edge);
        }
    }

    @Override
    public void addNode(N node) {
        if(nodeToIndex.containsKey(node)) {
            return;
        }
        int index = nodeToIndex.size();
        nodeToIndex.put(node, index);
        indexToNode.put(index, node);
        representation.grow();
    }

    @Override
    public void addEdge(Edge<N> edge) {
        int fromIndex = nodeToIndex.get(edge.from());
        int toIndex = nodeToIndex.get(edge.to());
        representation.addEdge(fromIndex, toIndex);
        weights.get(fromIndex).put(edge.to(), 1);
    }

    @Override
    public Set<N> getNodes() {
        return nodeToIndex.keySet();
    }

    @Override
    public Set<Edge<N>> getEdges() {
        Set<Edge<N>> set = new HashSet<>();

        for (N node : nodeToIndex.keySet()) {
            set.addAll(getOutgoingEdges(node));
        }

        return set;
    }

    @Override
    public Set<Edge<N>> getOutgoingEdges(N node) {
        Set<Edge<N>> set = new HashSet<>();
        Integer index = nodeToIndex.get(node);
        if(index == null) {
            return set;
        }
        Set<Integer> adjacentindices = representation.getAdjacentIndices(index);
        for(Integer adjacentIndex : adjacentindices) {
            N toNode = indexToNode.get(adjacentIndex);
            int weight = weights.get(node).get(toNode);
            set.add(Edge.of(node,toNode,weight));
        }
        return set;
    }

    @Override
    public Set<Edge<N>> getIngoingEdges(N node) {
        checkNode(node);

        Set<Edge<N>> set = new HashSet<>();

        for (N fromNode : nodeToIndex.keySet()) {
            if (representation.hasEdge(nodeToIndex.get(fromNode), nodeToIndex.get(node))) {
                set.add(Edge.of(fromNode, node, getWeight(fromNode, node)));
            }
        }

        return set;
    }

    @Override
    public Edge<N> getEdge(N from, N to) {
        Integer fromIndex = nodeToIndex.get(from);
        Integer toIndex = nodeToIndex.get(to);
        if(fromIndex == null || toIndex == null || !representation.hasEdge(fromIndex, toIndex)) {
            return null;
        }
        int weight = weights.get(fromIndex).get(to);
        return Edge.of(from, to, weight);
    }

    /**
     * Calculates the weight of the edge that starts at the node {@code from} and ends at the node {@code to}.
     *
     * @param from the node the edge starts at.
     * @param to   the node the edge ends at.
     * @return the weight of the edge that starts at the node {@code from} and ends at the node {@code to}, or {@code null}
     * if there is no such edge.
     */
    private Integer getWeight(N from, N to) {
        return weights.get(from).get(to);
    }

    private void checkNode(N node) {
        if (!nodeToIndex.containsKey(node)) {
            throw new IllegalArgumentException("Node %s is not part of this graph".formatted(node));
        }
    }
}
