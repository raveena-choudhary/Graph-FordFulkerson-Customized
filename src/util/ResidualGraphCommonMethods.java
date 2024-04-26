package util;

import Algorithms.RandomSourceSinkGraphGenerator;

import java.util.*;

import static Algorithms.RandomSourceSinkGraphGenerator.getEdgeCapacity;
import static Algorithms.RandomSourceSinkGraphGenerator.setEdgeCapacityInGivenGraph;

public class ResidualGraphCommonMethods {

    public static Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> updateResidualGraph(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> residualGraph, List<Integer> path, int minCapacity) {
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
//            System.out.println("u:" + u);
            int v = path.get(i + 1);

            // Update forward edge
            List<RandomSourceSinkGraphGenerator.EdgeWithCapacity> forwardEdges = residualGraph.get(u);
            int edgeCapacity = getEdgeCapacity(residualGraph,u,v);
            for (RandomSourceSinkGraphGenerator.EdgeWithCapacity neighbor : forwardEdges) {
//                System.out.println("neighbor:" + neighbor.getVertex());
                if (neighbor.getVertex() == v) {
                    residualGraph = setEdgeCapacityInGivenGraph(residualGraph,u,v,edgeCapacity - minCapacity);
                    residualGraph = removeEdgeWithZeroCapacity(residualGraph,edgeCapacity,u,v);
//                    }
                    break;
                }
            }
        }

        return residualGraph;
    }

    public static Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> removeEdgeWithZeroCapacity(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> residualGraph, int edgeCapacity, int u, int v)
    {
        edgeCapacity = getEdgeCapacity(residualGraph,u,v);
        if (edgeCapacity <= 0) {
//            System.out.println("Removing edge: " + u +"->" +v + "capacity cant be less than 0");
            List<RandomSourceSinkGraphGenerator.EdgeWithCapacity> neighbors = residualGraph.get(u);
            if (neighbors != null) {
                Iterator<RandomSourceSinkGraphGenerator.EdgeWithCapacity> iterator = neighbors.iterator();
                while (iterator.hasNext()) {
                    RandomSourceSinkGraphGenerator.EdgeWithCapacity edgeTo = iterator.next();
                    if (edgeTo.getVertex() == v && edgeTo.getCapacity() == 0) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        return residualGraph;
    }

    public static Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> createResidualGraph(Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph)
    {
        //deep copy of the graph
        Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> residualGraph = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> entry : graph.entrySet()) {
            ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> originalList = entry.getValue();
            ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> copyList = new ArrayList<>(originalList.size());

            // Copy each element in the list
            for (RandomSourceSinkGraphGenerator.EdgeWithCapacity edge : originalList) {
                copyList.add(new RandomSourceSinkGraphGenerator.EdgeWithCapacity(edge));
            }

            residualGraph.put(entry.getKey(), copyList);
        }

        return residualGraph;
    }

    public static boolean isEdgeExist(List<RandomSourceSinkGraphGenerator.EdgeWithCapacity> edges){
        if(!edges.isEmpty() && edges.size()==1){
            RandomSourceSinkGraphGenerator.EdgeWithCapacity onlyEdge = edges.get(0); //beacuse only this edge exists
            if(onlyEdge.getVertex()==-1 && onlyEdge.getCapacity()==-1){
                return false;
            }
            return true;
        }
        return true;
    }

    public static Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> updatedGraphAfterRemovalOfEdges(Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph)
    {
        Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> residualGraph = new HashMap<>();
        //add vertex as -1 to all edges that no longer have paths, traverse updatedResidual graph
        for(Integer vertex : graph.keySet())
        {
            ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> neighbors = graph.get(vertex);
            if(neighbors!=null && neighbors.size()>0)
            {
                residualGraph.put(vertex,neighbors);
            }
            else {
                ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> nonExistenceNeighbor = new ArrayList<>();
                nonExistenceNeighbor.add(new RandomSourceSinkGraphGenerator.EdgeWithCapacity(-1,-1));
                residualGraph.put(vertex,nonExistenceNeighbor);
            }
        }
        return residualGraph;
    }
}

