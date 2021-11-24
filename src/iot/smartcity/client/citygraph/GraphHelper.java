package iot.smartcity.client.citygraph;

import java.util.ArrayList;

/**
 * Since the city is seen as a graph, this class
 * helps with everything related to the graph management
 * like updating weights or finding the shortest path
 */

public class GraphHelper {

    private final int[][] cityMatrix;
    private final int size;
    private int[] parents;
    private int[] distances;
    Dijkstra dijkstra;

    public GraphHelper(int[][] adjMatrix){
        this.cityMatrix = adjMatrix;
        this.size = cityMatrix[0].length;
        this.dijkstra = new Dijkstra(adjMatrix);
    }

    // Set graph weight
    public void setWeight(int nodeA, int nodeB, int weight) {
        if ((nodeA > size) || (nodeB > size)) {
            System.err.println("Node does not exists in the matrix");
            System.exit(1);
        }

        cityMatrix[nodeA][nodeB] = weight;
        cityMatrix[nodeB][nodeA] = weight;

    }

    public void findPath(int start) {
        dijkstra.shortestPath(start);
        parents = dijkstra.getParents();
        distances = dijkstra.getDistances();
    }

    public String pathToString(int vertex) {
        StringBuilder builder = new StringBuilder();
            {
                builder.append("location: ").append(vertex).append(" | ");
                builder.append("traffic:    ").insert(23, distances[vertex]).insert(26,"| ");
                builder.insert(28, " path:   ").insert(35, singleNodePath(vertex, parents)).append("\n");
            }
        return builder.toString();
    }

    private static String singleNodePath(int currentVertex, int[] parents) {
        ArrayList<Integer> vertexes = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        while (currentVertex != -1) {
            vertexes.add(currentVertex);
            currentVertex = parents[currentVertex];
        }
        builder.append(vertexes.get(vertexes.size()-1));
        for (int i = vertexes.size() - 2; i >= 0; i--) {
            builder.append(" -> ").append(vertexes.get(i));
        }
        return builder.toString();
    }
}
