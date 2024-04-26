/* Algorithm : When inserting a node into Q in Dijkstra’s algorithm, use a decreasing counter
as its key value.
Author : Raveena Choudhary, 40232370
*/

package Algorithms;

import util.Metrics;
import util.ResidualGraphCommonMethods;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.GenerateOutputFile.storeResultsInFile;

public class DFSLikeAlgo extends FordFulkerson {

    private List<List<Integer>> augmentingPaths = new ArrayList<>();

    static class Vertex {

        int id;
        int counter; // key

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public Vertex(int id, int counter) {
            this.id = id;
            this.counter = counter;
        }

        static class VertexCounterComparator implements Comparator<Vertex> {
            @Override
            public int compare(Vertex Vertex1, Vertex Vertex2) {
                //smallest key
                return Integer.compare(Vertex1.getCounter(), Vertex2.getCounter());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.id && counter == vertex.counter;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, counter);
        }
    }

    public int getMaxFlowWithDFSLike(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink) {
        int maxFlow = 0;
        Map<Integer, Integer> parentMap = new HashMap<>();

        //copy graph to residual graph
        Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph = ResidualGraphCommonMethods.createResidualGraph(graph);
        List<Integer> augmentingPath = new ArrayList<>();
        augmentingPath = dijkstraToFindAugmentingPath(residualGraph, source, sink, parentMap);

        while (augmentingPath != null) {
            if (!this.augmentingPaths.contains(augmentingPath)) {
                System.out.println(augmentingPath);
                //this.augmentingPaths.add(List.copyOf(augmentingPath));
                this.augmentingPaths.add(augmentingPath.stream().collect(Collectors.toList()));
            } else {
                break;
            }

            int minCapacityAlongAugPath = Integer.MAX_VALUE;
            int target = sink;
            while (target != source) {
                int from = parentMap.get(target);
                int edgeCapacity = getEdgeCapacity(residualGraph, from, target);

                //check edge exists u>v, then find min in path
                if (residualGraph.containsKey(from) && residualGraph.get(from) != null) {
                    for (EdgeWithCapacity edgeTo : residualGraph.get(from)) {
                        if (edgeTo.getVertex() == target)
                            minCapacityAlongAugPath = Math.min(minCapacityAlongAugPath, edgeCapacity);
                    }
                }
                target = parentMap.get(target);
            }

            maxFlow += minCapacityAlongAugPath;

            System.out.println("Min capacity along augmenting path: " + minCapacityAlongAugPath);

//            System.out.println(augmentingPath);
            Map<Integer, ArrayList<EdgeWithCapacity>> updatedResidualGraph = ResidualGraphCommonMethods.updateResidualGraph(residualGraph, augmentingPath, minCapacityAlongAugPath);
            augmentingPath.clear();  //for next path
            parentMap.clear(); //clear older parent list
            Map<Integer, ArrayList<EdgeWithCapacity>> updatedCompatibleResidualGraph = ResidualGraphCommonMethods.updatedGraphAfterRemovalOfEdges(updatedResidualGraph); //get compatible graph to run algo
//            printGraph(updatedCompatibleResidualGraph.size(), updatedCompatibleResidualGraph);
            augmentingPath = dijkstraToFindAugmentingPath(updatedCompatibleResidualGraph, source, sink, parentMap);
        }

        return maxFlow;
    }

    private List<Integer> dijkstraToFindAugmentingPath(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink, Map<Integer, Integer> parentMap) {

        Map<Integer, Integer> counters = new HashMap<>();

        //set source.counter = 2 * graph.size
        counters = initialiseSingleSource(graph, source, counters);

        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(new Vertex.VertexCounterComparator());

        //add all vertices in queue
        for (Integer vertex : graph.keySet()) {
            queue.add(new Vertex(vertex, counters.get(vertex)));
        }

        List<Integer> visitedNodes = new ArrayList<Integer>();
        visitedNodes.add(source);

        //decreasing counter
        int dCounter = counters.get(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();  //max counter vertex
            int currentV = currentVertex.getId();
            //add augmenting path to paths list
            if (currentV == sink) {
                List<Integer> currentPath = new ArrayList<>();
                int target = sink;
                // Traverse parentMap to get a valid path
                while (target != source) {
                    currentPath.add(target);
                    if (!parentMap.containsKey(target)) {
                        return null;
                    }
                    target = parentMap.get(target);
                }
                currentPath.add(source);
                Collections.reverse(currentPath);

//                System.out.println(currentPath);
                return currentPath;
            }

            List<EdgeWithCapacity> neighbours = graph.get(currentV);
            if (neighbours != null && ResidualGraphCommonMethods.isEdgeExist(neighbours)) {
                for (EdgeWithCapacity neighbor : neighbours) {

                    int neighborV = neighbor.getVertex();

                    Vertex neighborVertexInQ = new Vertex(neighborV, counters.get(neighborV));
//                    int vertex = neighborVertexInQ.getId();
                    int existingCounter = neighborVertexInQ.getCounter();

                    dCounter = dCounter - 1;
                    int updatedCounter = dCounter;

                    //if updatedCounter is lesser than existingCounter and v.d = infinity, update queue
                    if (updatedCounter < existingCounter && existingCounter == Integer.MAX_VALUE && !visitedNodes.contains(neighborV)) {
//                            System.out.println("Yes,inside queue" + neighborVertexInQ.getId());
                        queue.remove(neighborVertexInQ);
                        //update counters
                        counters.put(neighborV, updatedCounter);
                        Vertex updatedNeighborVertex = new Vertex(neighborV, counters.get(neighborV));
                        queue.add(updatedNeighborVertex);    //add updated vertex to queue
                        visitedNodes.add(currentV);
                        parentMap.put(neighborV, currentV);
                    }
                }
            }
        }
        return null;  //no path found
    }

    public static Map<Integer, Integer> initialiseSingleSource(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph, int source, Map<Integer, Integer> counters) {
        for (Integer vertex : graph.keySet()) {
            counters.put(vertex, Integer.MAX_VALUE);
        }

        counters.put(source, 2 * graph.size());

        return counters;
    }

    public static void redirectOutputToFile(int paths, double ML, double MPL, int totalEdges, String filename,int maxFlow) throws IOException {
        storeResultsInFile(paths, ML, MPL, totalEdges, "DFS Like Algo Results", filename,maxFlow);
    }

    public void printMatricesAndStoreResultsInFile(List<Integer> longestPath, Map<Integer, ArrayList<EdgeWithCapacity>> dag, String filename,int maxFlow) throws IOException {

        int paths = Metrics.getNumberOfAugmentingPaths(this.augmentingPaths);
        double ML = Metrics.getML(this.augmentingPaths);
        double MPL = Metrics.getMPL(this.augmentingPaths, longestPath);
        int totalEdges = Metrics.getTotalEdges(dag);

//            System.out.println("--------------------------Metrices----------------------");
        System.out.println("Number of augmenting paths: " + paths);
        System.out.println("ML: " + ML);
        System.out.println("MPL: " + MPL);
        System.out.println("Total Edges in a graph: " + totalEdges);

        redirectOutputToFile(paths, ML, MPL, totalEdges,filename,maxFlow);
    }
}
