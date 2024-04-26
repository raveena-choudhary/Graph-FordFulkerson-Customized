package Algorithms;

/*Author: Raveena Choudhary, 40232370 */

import util.ResidualGraphCommonMethods;

import java.util.*;

public class LongestAcyclicPath extends RandomSourceSinkGraphGenerator {
    static class Vertex {
        int id;
        int distance;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        Vertex(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        static class VertexComparator implements Comparator<Vertex> {
            @Override
            public int compare(Vertex Vertex1, Vertex Vertex2) {
                // distance from source -> max first
                return Integer.compare(Vertex2.getDistance(), Vertex1.getDistance());
            }
        }
    }

    public static List<Integer> findLongestAcyclicPath(int n, Map<Integer, ArrayList<EdgeWithCapacity>> graph) {
//        printGraph(n,graph);

        Set<Integer> selectedSources = new HashSet<>();
        List<Integer> longestPath = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            //randomly generated source
            int source = getRandomSource(n, selectedSources);
            selectedSources.add(source);
            int[] distance = new int[n];  //maintain distance of all nodes from source, choose farthest distance for sink
            Arrays.fill(distance,-1);

            distance[source] = 0;

            List<Integer> path = new ArrayList<>();
            path = bfs(source, graph, distance);
//            path = bfs(source, graph);


            if (longestPath.size() <= path.size()) {
                longestPath.clear(); // remove all the previous vertices from longestPath
                longestPath.addAll(path);
            }
        }
        return longestPath;
    }

    public static List<Integer> bfs(int source, Map<Integer, ArrayList<EdgeWithCapacity>> graph, int[] distance) {
        PriorityQueue<Vertex> queue = new PriorityQueue<>(new Vertex.VertexComparator());
        Map<Integer, Integer> parentMap = new HashMap<>();
        List<Integer> visitedNodes = new ArrayList<>();   // stores visited path
        List<Integer> path = new ArrayList<>();

        Vertex sourceVertex = new Vertex(source, distance[source]);
        queue.add(sourceVertex);

        visitedNodes.add(source);

        int target = source;

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            int u = current.getId();

            List<EdgeWithCapacity> neighbours = graph.get(u);

            if (neighbours != null && ResidualGraphCommonMethods.isEdgeExist(neighbours)) {
                for (EdgeWithCapacity neighbor : neighbours) {
                    int v = neighbor.getVertex();

                    if(v == -1)
                        continue;

                    if (!visitedNodes.contains(v) && distance[v] < distance[u] + 1) {
                        Vertex updatedVertex = new Vertex(v, distance[u] + 1);
                        distance[v] = distance[u] + 1;
                        queue.add(updatedVertex);

                        visitedNodes.add(u);
//                        System.out.println(visitedNodes);
                        parentMap.put(v, u);
                        target = v;
                    }
                }
            }
        }

// Traverse parentMap to get the longest path
        int current = target;
        while (current != source) {
            path.add(current);
            current = parentMap.get(current);
        }

        path.add(source);
        Collections.reverse(path);

        return path;


    }
}
