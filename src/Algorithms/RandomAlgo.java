package Algorithms;

import util.Metrics;
import util.ResidualGraphCommonMethods;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.GenerateOutputFile.storeResultsInFile;

/*Algorithm: When inserting a node into Q in Dijkstra’s algorithm, use a random value as
        its key value.
        Author: Raveena Choudhary, 40232370*/
public class RandomAlgo extends FordFulkerson {

    private List<List<Integer>> augmentingPaths = new ArrayList<>();

    static class Vertex {

        int id;
        int key; //random value

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public Vertex(int id, int key) {
            this.id = id;
            this.key = key;
        }

        static class VertexKeyComparator implements Comparator<Vertex> {
            @Override
            public int compare(Vertex Vertex1, Vertex Vertex2) {
                //following greedy approach, so taking key with smallest value
                return Integer.compare(Vertex1.getKey(), Vertex2.getKey());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.id && key == vertex.key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, key);
        }
    }

    public int getMaxFlowWithRandom(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink)
    {
        int maxFlow = 0;
        Map<Integer,Integer> parentMap = new HashMap<>();

        //copy graph to residual graph
        Map<Integer, ArrayList<EdgeWithCapacity>> residualGraph = ResidualGraphCommonMethods.createResidualGraph(graph);
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

            int minCapacityAlongAugPath = Integer.MAX_VALUE;

            int target = sink;
            while(target!=source)
            {
                int from = parentMap.get(target);
                int edgeCapacity = getEdgeCapacity(residualGraph,from,target);
//                System.out.println(edgeCapacity);

                //check edge exists u>v, then find min in path
                if(residualGraph.containsKey(from) && residualGraph.get(from)!=null)
                {
                    for(EdgeWithCapacity edgeTo : residualGraph.get(from))
                    {
                        if(edgeTo.getVertex() == target)
                            minCapacityAlongAugPath = Math.min(minCapacityAlongAugPath, edgeCapacity);
                    }
                }
                target = parentMap.get(target);
            }

            maxFlow += minCapacityAlongAugPath;

            System.out.println("Min capacity along augmenting path: " + minCapacityAlongAugPath);

            Map<Integer, ArrayList<EdgeWithCapacity>> updatedResidualGraph = ResidualGraphCommonMethods.updateResidualGraph(residualGraph,augmentingPath,minCapacityAlongAugPath);
            augmentingPath.clear();  //for next path
            parentMap.clear(); //clear older parent list
            Map<Integer, ArrayList<EdgeWithCapacity>> updatedCompatibleResidualGraph = ResidualGraphCommonMethods.updatedGraphAfterRemovalOfEdges(updatedResidualGraph); //get compatible graph to run algo
//            printGraph(updatedCompatibleResidualGraph.size(), updatedCompatibleResidualGraph);
            augmentingPath = dijkstraToFindAugmentingPath(updatedCompatibleResidualGraph, source, sink,parentMap);
        }

        return maxFlow;
    }

    private List<Integer> dijkstraToFindAugmentingPath(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int sink,Map<Integer,Integer> parentMap) {

//        Map<Integer, Integer> randomValues = new HashMap<>();
        Map<Integer, Integer> randomValues = new HashMap<>();
        //set source.d = 0
        randomValues = initialiseSingleSource(graph, source, randomValues);

        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(new Vertex.VertexKeyComparator());

        //add all vertices in queue
        for(Integer vertex: graph.keySet())
        {
            queue.add(new Vertex(vertex,randomValues.get(vertex)));
        }

        List<Integer> visitedNodes = new ArrayList<Integer>();
        visitedNodes.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();  //min random key vertex
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
                        return null;
                    }
                    target = parentMap.get(target);
                }
                currentPath.add(source);
                Collections.reverse(currentPath);
                return currentPath;
            }


            List<EdgeWithCapacity> neighbours = graph.get(currentV);
            if (neighbours!=null && ResidualGraphCommonMethods.isEdgeExist(neighbours)) {
                for (EdgeWithCapacity neighbor : neighbours) {
                    int neighborV = neighbor.getVertex();

                    //vertex if already visited
                    Vertex neighborVertexInQ = new Vertex(neighborV, randomValues.get(neighborV));
                    int existingKey = neighborVertexInQ.getKey();

                    int updatedKey = (int) Math.random();
                    //if updatedKey is smaller than existingKey, update queue
                    if (updatedKey < existingKey && !visitedNodes.contains(neighborV)) {
                        queue.remove(neighborVertexInQ);
                        //update counters
                        randomValues.put(neighborV, updatedKey);
                        Vertex updatedNeighborVertex = new Vertex(neighborV, randomValues.get(neighborV));
                        queue.add(updatedNeighborVertex);    //add updated vertex to queue
                        parentMap.put(neighborV, currentV);
                        visitedNodes.add(currentV);
                    }
                }
            }
        }
        return null;  //no path found
    }

    public static void redirectOutputToFile(int paths,double ML,double MPL,int totalEdges,String filename,int maxFlow) throws IOException {
        storeResultsInFile(paths,ML,MPL,totalEdges,"Random Algo Results",filename,maxFlow);
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
    public static  Map<Integer,Integer> initialiseSingleSource(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph, int source, Map<Integer,Integer> randomValues)
    {
        for(Integer vertex : graph.keySet())
        {
            randomValues.put(vertex,Integer.MAX_VALUE);
        }

        randomValues.put(source,(int) Math.random());

        return randomValues;
    }
}
