package p3.graph;

import java.util.HashSet;
import java.util.Set;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A light wrapper around a 2D array of boolean that represents an adjacency matrix.
 * <p>
 * The adjacency matrix is a square matrix that represents a directed graph. The entry at index (i, j) is {@code true}, iff
 * a connection from the node with index {@code i} to the node with index {@code j} exists. Otherwise, the entry is
 * {@code false}.
 *
 * @see AdjacencyRepresentation
 */
public class AdjacencyMatrix implements AdjacencyRepresentation {

    /**
     * A factory that creates an empty {@link AdjacencyMatrix} with the given initial size.
     */
    public static final Factory FACTORY = AdjacencyList::new;

    /**
     * The underlying array that stores the adjacency matrix.
     */
    private boolean[][] matrix;

    /**
     * Constructs a new {@link AdjacencyMatrix} with the given size.
     * <p>
     * Initially, the matrix is empty, i.e., no connections between nodes exist.
     *
     * @param size The amount of nodes in the graph.
     */
    public AdjacencyMatrix(int size) {
        matrix = new boolean[size][size];
    }

    @Override
    public void addEdge(int from, int to) {
        matrix[from][to] = true;
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return matrix[from][to];
    }

    @Override
    public Set<Integer> getAdjacentIndices(int index) {
        Set<Integer> adjacentIndices = new HashSet<>();
        for (int i = 0; i < matrix.length; i++) {
            if(matrix[index][i]) {
                adjacentIndices.add(i);
            }
        }
        return adjacentIndices;
    }

    @Override
    public int size() {
        return matrix.length;
    }

    @Override
    public void grow() {

        // Create a new matrix with one more row and column
        int newSize = matrix.length + 1;
        boolean[][] newMatrix = new boolean[newSize][newSize];

        // Copy the old matrix into the new matrix
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix[i].length);
        }

        matrix = newMatrix;
    }
}
