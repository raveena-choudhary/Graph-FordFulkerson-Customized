package Algorithms;

import java.util.*;

public class DAG extends RandomSourceSinkGraphGenerator{

    public static Map<Integer, ArrayList<EdgeWithCapacity>> createDAG(int n, int source, Map<Integer,ArrayList<EdgeWithCapacity>> graph)
    {
        // Detect cycle and get the edge that forms the cycle
        List<Integer> cycleEdge = findCycleIfExists(graph);

        while (cycleEdge != null) {
            int u = cycleEdge.get(0);
            int v = cycleEdge.get(1);

            //remove edge
            removeEdge(graph, u, v);

//            System.out.println("Cycle detected. Removed edge: " + cycleEdge.get(0) + " -> " + cycleEdge.get(1));

            cycleEdge = findCycleIfExists(graph);  //graph after removing an edge, check further cycles.

        }
//            System.out.println("No cycle detected in the graph.");

//        printGraph(n,graph);

        return graph;
    }

    private static List<Integer>  findCycleIfExists(Map<Integer, ArrayList<EdgeWithCapacity>> graph) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> stack = new HashSet<>();

        for (int vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                List<Integer>  cycleEdge = dfs(vertex, graph, visited, stack);
                if (cycleEdge != null) {
                    return cycleEdge;
                }
            }
        }

        return null;
    }

    private static List<Integer> dfs(int vertex, Map<Integer, ArrayList<EdgeWithCapacity>> graph,Set<Integer> visited,Set<Integer> stack) {
        visited.add(vertex);
        stack.add(vertex);

        List<EdgeWithCapacity>  neighbors = graph.get(vertex);
        if (neighbors != null) {
            for (EdgeWithCapacity neighbor : neighbors) {
                int neighborV = neighbor.getVertex();
                if (!visited.contains(neighborV)) {
                    List<Integer>  cycleEdge = dfs(neighborV, graph, visited, stack);
                    if (cycleEdge != null) {
                        return cycleEdge;
                    }
                } else if (stack.contains(neighborV)) {
                    return Arrays.asList(vertex, neighborV);    //edge forming cycle
                }
            }
        }

        stack.remove(vertex);
        return null;
    }

    private static void removeEdge(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int target) {
        List<EdgeWithCapacity> neighbors = graph.get(source);
        if (neighbors != null) {
            EdgeWithCapacity edgeToRemove = null;
            for (EdgeWithCapacity neighbor : neighbors) {
                if (neighbor.getVertex() == target) {
                    edgeToRemove = neighbor;
                    break;
                }
            }

            if (edgeToRemove != null) {
                neighbors.remove(edgeToRemove);
            }
        }
    }

}
