package Algorithms;

import java.util.*;

public class Graph {
    private Map<Integer, List<Integer>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // Add a vertex to the graph
    public void addVertex(int vertex) {
        adjacencyList.put(vertex, new ArrayList<>());
    }

    // Add an edge to the graph
    public void addEdge(int source, int destination) {
        if (!adjacencyList.containsKey(source)) {
            addVertex(source);
        }
        if (!adjacencyList.containsKey(destination)) {
            addVertex(destination);
        }

        adjacencyList.get(source).add(destination);
    }

    // Get the adjacency list of a vertex
    public List<Integer> getAdjacentVertices(int vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyList());
    }

    // Print the graph
    public void printGraph(Map<Integer,ArrayList<Integer>> adjacencyList) {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : adjacencyList.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            List<Integer> neighbors = entry.getValue();
            for (Integer neighbor : neighbors) {
                System.out.print(neighbor + " ");
            }
            System.out.println();
        }
    }

//    public static void main(String[] args) {
//        Graph graph = new Graph();
//
//        // Adding vertices
//        graph.addVertex(1);
//        graph.addVertex(2);
//        graph.addVertex(3);
//
//        // Adding edges
//        graph.addEdge(1, 2);
//        graph.addEdge(2, 3);
//        graph.addEdge(3, 1);
//
//        // Print the graph
//        System.out.println("Graph:");
//        graph.printGraph();
//    }
}

