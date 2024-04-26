package Algorithms;

import util.Metrics;
import util.ResidualGraphCommonMethods;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.GenerateOutputFile.storeResultsInFile;

/*Modify Dijkstra’s algorithm to find the augmenting
        path with the maximum capacity. Note that the capacity of the critical edge (cf (p) of
        the augmenting path p) will be the value of t.d when the modified algorithm completes.
        Author : Raveena Choudhary, 40232370*/
public class MaximumCapacity extends FordFulkerson {

    private List<List<Integer>> augmentingPaths = new ArrayList<>();
    private static int distanceOfSink = Integer.MIN_VALUE;

    static class Vertex {

        int id;
        int capacity;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public Vertex(int id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }

        static class VertexCapacityComparator implements Comparator<Vertex> {
            @Override
            public int compare(Vertex Vertex1, Vertex Vertex2) {
                // max capacity from source to vertex
                return Integer.compare(Vertex2.getCapacity(), Vertex1.getCapacity());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.id && capacity == vertex.capacity;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, capacity);
        }
    }

    public int getMaxFlowWithMaximumCapacity(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink)
    {
        int maxFlow = 0;

        Map<Integer,Integer> parentMap = new HashMap<>();

        //copy graph to residual graph
        Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph = ResidualGraphCommonMethods.createResidualGraph(graph);
        List<Integer> augmentingPath = new ArrayList<>();
        augmentingPath = dijkstraToFindAugmentingPath(residualGraph, source, sink,parentMap);

        while(augmentingPath!=null) {
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
//                System.out.println(edgeCapacity);

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

            Map<Integer, ArrayList<EdgeWithCapacity>> updatedResidualGraph = ResidualGraphCommonMethods.updateResidualGraph(residualGraph, augmentingPath, minCapacityAlongAugPath);

            //check capacity of the critical edge (cf (p) of the augmenting path p) will be the value of t.d when the modified algorithm completes.
            if (distanceOfSink != minCapacityAlongAugPath) {
                System.out.println(minCapacityAlongAugPath + ":" + distanceOfSink);
                throw new RuntimeException("distance of sink is not equal to capacity of the critical edge");
            }

            augmentingPath.clear();  //for next path
            parentMap.clear(); //clear older parent list
            Map<Integer, ArrayList<EdgeWithCapacity>> updatedCompatibleResidualGraph = ResidualGraphCommonMethods.updatedGraphAfterRemovalOfEdges(updatedResidualGraph); //get compatible graph to run algo
//            printGraph(updatedCompatibleResidualGraph.size(), updatedCompatibleResidualGraph);
            augmentingPath = dijkstraToFindAugmentingPath(updatedCompatibleResidualGraph, source, sink, parentMap);
        }

        return maxFlow;
    }

    private List<Integer> dijkstraToFindAugmentingPath(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink,Map<Integer,Integer> parentMap) {

        Map<Integer, Integer> capacities = new HashMap<>();
        //set source.d to infinity
        capacities = initialiseSingleSource(graph, source, capacities);

        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(new Vertex.VertexCapacityComparator());

        //add vertex with id, capacities (source) in queue
//        queue.add(new Vertex(source, capacities.get(source)));

        //add all vertices in queue
        for(Integer vertex: graph.keySet())
        {
            queue.add(new Vertex(vertex,capacities.get(vertex)));
        }

        List<Integer> visitedNodes = new ArrayList<Integer>();
        visitedNodes.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();  //max capacity vertex from source
            int currentV = currentVertex.getId();

            //add augmenting path to paths list
            if (currentV == sink) {
                List<Integer> currentPath = new ArrayList<>();
                int target = sink;
                // Traverse parentMap to get a valid path
                while (target != source) {
                    currentPath.add(target);
                    if(!parentMap.containsKey(target))
                    {
//                        System.out.println(target);
                        return null;
                    }
                    target = parentMap.get(target);
                }
                currentPath.add(source);
                Collections.reverse(currentPath);

                distanceOfSink = capacities.get(currentPath.get(currentPath.size()-1));
                return currentPath;
            }


            List<EdgeWithCapacity> neighbours = graph.get(currentV);
            //traverse adj list for edges
            if (neighbours!=null && ResidualGraphCommonMethods.isEdgeExist(neighbours)) {
                for (EdgeWithCapacity neighbor : neighbours) {
                    int neighborV = neighbor.getVertex();

                    Vertex existingNeighborVertexInQ = new Vertex(neighborV, capacities.get(neighborV));
                    int existingCapacity = existingNeighborVertexInQ.getCapacity();
//                    System.out.println(neighborV + " with current capacity " + existingCapacity);

                    //capacities updation
                    int edgeCapacity = getEdgeCapacity(graph,currentV,neighborV);
                    int updatedCapacity = Math.min(capacities.get(currentV), edgeCapacity);
                    Vertex updatedNeighborVertex = new Vertex(neighborV, updatedCapacity);
                    //change key if updated(updatedCapacity > existingCapacity)
                    if (updatedCapacity > existingCapacity && !visitedNodes.contains(neighborV)) {
                        queue.remove(existingNeighborVertexInQ);
                        queue.add(updatedNeighborVertex);    //add updated vertex to queue
                        capacities.put(neighborV,updatedCapacity);
                        parentMap.put(neighborV,currentV); //update parent of neighbor which increased its capacity
                        visitedNodes.add(currentV);

                    }
                }
            }
        }
        return null;  //no path found
    }

    private static Map<Integer,Integer> initialiseSingleSource(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, Map<Integer,Integer> capacity)
    {
        for(Integer vertex : graph.keySet())
        {
            capacity.put(vertex,Integer.MIN_VALUE);
        }

        capacity.put(source,Integer.MAX_VALUE);

        return capacity;
    }

    public static void redirectOutputToFile(int paths,double ML,double MPL,int totalEdges,String filename,int maxFlow) throws IOException {
        storeResultsInFile(paths,ML,MPL,totalEdges,"Maximum Capacity Algo Results",filename,maxFlow);
    }
    public void printMatricesAndStoreResultsInFile(List<Integer> longestPath, Map<Integer,ArrayList<EdgeWithCapacity>> dag,String filename,int maxFlow) throws IOException {

            int paths = Metrics.getNumberOfAugmentingPaths(this.augmentingPaths);
            double ML = Metrics.getML(this.augmentingPaths);
            double MPL = Metrics.getMPL(this.augmentingPaths,longestPath);
            int totalEdges = Metrics.getTotalEdges(dag);

//            System.out.println("--------------------------Metrices----------------------");
            System.out.println("Number of augmenting paths: " + paths);
            System.out.println("ML: "+ ML);
            System.out.println("MPL: "+ MPL);
            System.out.println("Total Edges in a graph: "+ totalEdges);

            redirectOutputToFile(paths,ML,MPL,totalEdges,filename,maxFlow);
        }
}
