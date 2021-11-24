package iot.smartcity.client.citygraph;

public class Dijkstra {

    private static final int ORIGIN = -1;
    private final int[][] adjMatrix;
    private final int[] distances;
    private final boolean[] visited;
    private final int[] parents;
    private final int size;

    public Dijkstra(int[][] adjMatrix) {
        this.size = adjMatrix[0].length;
        this.adjMatrix = adjMatrix;
        distances = new int[size];
        visited = new boolean[size];
        parents = new int[size];
    }

    /**
     * Method used to return the shortest path from start vertex and
     * update parents and distances
     * @param start starting vertex
     */
    public void shortestPath(int start) {

        for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
        {
            distances[vertexIndex] = Integer.MAX_VALUE;
            visited[vertexIndex] = false;
        }

        // Distance of source vertex from itself set to 0
        distances[start] = 0;
        parents[start] = ORIGIN;

        // Find shortest path for all vertices
        for (int i = 1; i < size; i++)
        {

            // Pick shortestDistance vertex from the ones not yet processed
            int nearestVertex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
            {
                if (!visited[vertexIndex] && distances[vertexIndex] < shortestDistance)
                {
                    nearestVertex = vertexIndex;
                    shortestDistance = distances[vertexIndex];
                }
            }

            // Mark the picked vertex as processed
            visited[nearestVertex] = true;

            for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
            {
                int edgeDistance = adjMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < distances[vertexIndex]))
                {
                    // Update parents and distances arrays to make the print possible
                    parents[vertexIndex] = nearestVertex;
                    distances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

    }

    public int[] getDistances() {
        return distances;
    }

    public int[] getParents() {
        return parents;
    }
}
