package Algorithms;

import util.Metrics;
import util.ResidualGraphCommonMethods;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.GenerateOutputFile.storeResultsInFile;

/*Algorithm: Run Dijkstra’s algorithm while treating the edge
        capacities as unit capacities other than their actual capacities. This will be effectively
        the same as running the BFS algorithm.
        Author : Raveena Choudhary, 40232370*/

public class ShortestAugmentingPath extends FordFulkerson {

    private List<List<Integer>> augmentingPaths = new ArrayList<>();

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

        public Vertex(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        static class VertexDistanceComparator implements Comparator<Vertex> {
            @Override
            public int compare(Vertex Vertex1, Vertex Vertex2) {
                // min dist from source to vertex
                return Integer.compare(Vertex1.getDistance(), Vertex2.getDistance());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.id && distance == vertex.distance;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, distance);
        }
    }

    public int getMaxFlowWithSAP(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink)
    {
        int maxFlow = 0;
        Map<Integer,Integer> parentMap = new HashMap<>();

        //copy graph to residual graph
        Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph = createResidualGraph(graph);
        List<Integer> augmentingPath = new ArrayList<>();
        augmentingPath = dijkstraToFindAugmentingPath(residualGraph, source, sink,parentMap);
        while(augmentingPath!=null)
        {
            if(!this.augmentingPaths.contains(augmentingPath))
            {
                System.out.println(augmentingPath);
                //this.augmentingPaths.add(List.copyOf(augmentingPath));
                this.augmentingPaths.add(augmentingPath.stream().collect(Collectors.toList()));
            }
            else {
                break;
            }

            //since, edge capacities are 1 only.
//            int minCapacity = 1;
            int pathFlow = Integer.MAX_VALUE;

            int target = sink;
            while(target!=source)
            {
                int from = parentMap.get(target);
                int edgeCapacity = getEdgeCapacity(residualGraph,from,target);

                //check edge exists u>v, then find min pathFlow
                if(residualGraph.containsKey(from) && residualGraph.get(from)!=null)
                {
                    for(EdgeWithCapacity neighbor : residualGraph.get(from))
                    {
                        if(neighbor.getVertex() == target)
                            pathFlow = Math.min(pathFlow, edgeCapacity);
                    }
                }
                target = parentMap.get(target);
            }

            maxFlow += pathFlow;

            System.out.println("Min capacity along augmenting path: " + pathFlow);

            Map<Integer, ArrayList<EdgeWithCapacity>> updatedResidualGraph = updateResidualGraph(residualGraph,augmentingPath,pathFlow);

            augmentingPath.clear();  //for next path
            parentMap.clear(); //clear older parent list
            Map<Integer, ArrayList<EdgeWithCapacity>> updatedCompatibleResidualGraph = ResidualGraphCommonMethods.updatedGraphAfterRemovalOfEdges(updatedResidualGraph); //get compatible graph to run algo

//            System.out.println("Updted residual graph...........");
//            printGraph(updatedCompatibleResidualGraph.size(),updatedCompatibleResidualGraph);
            augmentingPath = dijkstraToFindAugmentingPath(updatedCompatibleResidualGraph, source, sink,parentMap);

        }

        return maxFlow;
    }

    //deep copy of map to avoid changes in actual graph
    private static Map<Integer,ArrayList<EdgeWithCapacity>> createResidualGraph(Map<Integer,ArrayList<EdgeWithCapacity>> graph)
    {
        //deep copy of the graph
        Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<EdgeWithCapacity>> entry : graph.entrySet()) {
            ArrayList<EdgeWithCapacity> originalList = entry.getValue();
            ArrayList<EdgeWithCapacity> copyList = new ArrayList<>(originalList.size());

            // Copy each element in the list
            for (EdgeWithCapacity edge : originalList) {
                if(edge.getCapacity()!=-1) {
                    copyList.add(new EdgeWithCapacity(edge.getVertex(), 1));
                }
            }

            residualGraph.put(entry.getKey(), copyList);
        }

        return residualGraph;
    }

    private Map<Integer, ArrayList<EdgeWithCapacity>> updateResidualGraph(Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph, List<Integer> path, int minCapacity) {
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
//            System.out.println("u:" + u);
            int v = path.get(i + 1);
//            System.out.println("v:" + v);

            // Update forward edge
            List<EdgeWithCapacity> forwardEdges = residualGraph.get(u);
            int edgeCapacity = getEdgeCapacity(residualGraph,u,v);
//            System.out.println("Edge capacity:" + edgeCapacity);
            for (EdgeWithCapacity neighbor : forwardEdges) {
//                System.out.println("neighbor:" + neighbor.getVertex());
                if (neighbor.getVertex() == v) {
                    residualGraph = setEdgeCapacityInGivenGraph(residualGraph,u,v,edgeCapacity - minCapacity);
                    residualGraph = ResidualGraphCommonMethods.removeEdgeWithZeroCapacity(residualGraph,edgeCapacity,u,v);
                    break;
                }
            }
        }

        return residualGraph;
    }

    private List<Integer> dijkstraToFindAugmentingPath(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink,Map<Integer,Integer> parentMap) {

//        printGraph(graph.size(), graph);

        Map<Integer, Integer> distance = new HashMap<>();
        //set source.d to 0 and other nodes to infinity
        distance = initialiseSingleSource(graph, source, distance);

        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(new Vertex.VertexDistanceComparator());

        //add all vertices in queue
        for(Integer vertex: graph.keySet())
        {
            queue.add(new Vertex(vertex,distance.get(vertex)));
        }

        List<Integer> visitedNodes = new ArrayList<Integer>();
        visitedNodes.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();  //min distance vertex from source
            int currentV = currentVertex.getId();
//            System.out.println("Current " + currentV);

            //add augmenting path to paths list
            if (currentV == sink) {
                List<Integer> currentPath = new ArrayList<>();
                int target = sink;
                // Traverse parentMap to get a valid path
                while (target != source) {
                    currentPath.add(target);
//                    System.out.println("target:" + target);
                    if(!parentMap.containsKey(target))
                    {
                        return null;
                    }
                    target = parentMap.get(target);
                }
                currentPath.add(source);
                Collections.reverse(currentPath);
                return currentPath;
            }

            List<EdgeWithCapacity> neighbours = graph.get(currentV);
            //traverse adj list for edges
            if (neighbours!=null && ResidualGraphCommonMethods.isEdgeExist(neighbours)) {
                for (EdgeWithCapacity neighbor : neighbours) {

                        int neighborV = neighbor.getVertex();
//                        System.out.println(neighborV);
//                    System.out.println(currentV);
//                        int edgeCapacity = 1;
                        int edgeCapacity = getEdgeCapacity(graph,currentV,neighborV);
                        Vertex existingNeighborVertexInQ = new Vertex(neighborV, distance.get(neighborV));
//                    int vertex = existingNeighborVertexInQ.getId();
                        int currentDistance = existingNeighborVertexInQ.getDistance();
//                    System.out.println(vertex + " current distance " + currentDistance);

                        int updatedDistance = distance.get(currentV) + edgeCapacity;
                        Vertex updatedNeighborVertex = new Vertex(neighborV, updatedDistance);
                        if (updatedDistance < currentDistance && !visitedNodes.contains(neighborV)) {
//                        System.out.println("Yes,inside queue" + existingNeighborVertexInQ.getId());
                            queue.remove(existingNeighborVertexInQ);
//                        System.out.println("Remove success" + remvoed);
                            queue.add(updatedNeighborVertex);    //add updated vertex to queue
                            distance.put(neighborV, updatedDistance);
                            parentMap.put(neighborV, currentV);
                            visitedNodes.add(currentV);
                        }
                    }
                }
        }
        return null;  //no path found
    }

    public static void redirectOutputToFile(int paths,double ML,double MPL,int totalEdges,String filename,int maxFlow) throws IOException {
        storeResultsInFile(paths,ML,MPL,totalEdges,"SAP Algo Results",filename,maxFlow);
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
    public static  Map<Integer,Integer> initialiseSingleSource(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, Map<Integer,Integer> distance)
    {
        for(Integer vertex : graph.keySet())
        {
            distance.put(vertex,Integer.MAX_VALUE);
        }

        distance.put(source,0);

        return distance;
    }
}
