package Algorithms;

import java.io.*;
import java.util.*;

public class RandomSourceSinkGraphGenerator {
        static class Vertex {

            int id;
            double x;
            double y;

            public Vertex(int id,double x, double y) {
                this.id=id;
                this.x = x;
                this.y = y;
            }

            @Override
            public String toString() {
                return "n "+String.format("%,.2f", this.x)+" "+ String.format("%,.2f", this.y)+" "+this.id;
            }
        }

        static class DirectedEdge {
            int source;
            int destination;
            int capacity;

            public DirectedEdge(int source, int destination,int capacity) {
                this.source = source;
                this.destination = destination;
                this.capacity = capacity;
            }

            @Override
            public String toString() {
                return "e "+this.source+" "+ this.destination+" "+this.capacity;
            }
        }

        public static class EdgeWithCapacity {
            int vertex;
            int capacity;

            public int getVertex() {
                return vertex;
            }

            public void setVertex(int vertex) {
                this.vertex = vertex;
            }

            public int getCapacity() {
                return capacity;
            }

            public void setCapacity(int capacity) {
                this.capacity = capacity;
            }

            public EdgeWithCapacity(int vertex, int capacity) {
                this.vertex = vertex;
                this.capacity = capacity;
            }

            // Copy constructor
            public EdgeWithCapacity(EdgeWithCapacity other) {
                this.vertex = other.vertex;
                this.capacity = other.capacity;
            }

        }

    private static int graphNumber=0;

    private static Map<Integer, ArrayList<EdgeWithCapacity>> createNewAdjList(int n)
    {
        return new HashMap<Integer, ArrayList<EdgeWithCapacity>>(n);
    }

    //Generate n vertices with random (x, y) coordinates
    private static List<Vertex> addVertices(int n,Map<Integer, ArrayList<EdgeWithCapacity>> adjList)
    {
//        System.out.println("n for vertice : " + n);
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (int v = 0; v <n; v++) {
            double x=generateRandomNumberBetweenGivenRange(0,1);
            double y=generateRandomNumberBetweenGivenRange(0,1);
            vertices.add(new Vertex(v,x,y));
            adjList.put(vertices.get(v).id, new ArrayList<EdgeWithCapacity>()); //put all vertices in adjacency list
        }
//        System.out.println(vertices);
        return vertices;
    }

    public static double generateRandomNumberBetweenGivenRange(double min, double max)
    {
        double range = (max - min) + 1;
        return Math.random() * range + min;
    }

    private static List<DirectedEdge> addEdge(int n, double r,int upperCap,List<Vertex> vertices,Map<Integer, ArrayList<EdgeWithCapacity>> adjList) {

        List<DirectedEdge> edges = new ArrayList<>();

        // Create directed edges within distance r
        for (int u = 0; u <n; u++) {
            for (int v = 0; v<n; v++) {
                if (u != v) {
                    double distance = getEuclideanDistanceBetweenVertices(u, v,vertices); //get distance between edge from u to v
                    if (distance <= r) {
                        double rand = generateRandomNumberBetweenGivenRange(0,1);
                        int randomCapacity = getRandomCapacity(upperCap);
                        if (rand < 0.5)
                        {
                            if(!isEdgeExists(u,v,edges) && !isEdgeExists(v,u,edges))                    //method returns false when edge does not exists
                            {
                                edges.add(new DirectedEdge(u, v,randomCapacity));
                                // Updating the neighbours in adjacency list for vertex u
                                updateNeighborsForGivenVertex(u, v,randomCapacity,adjList);  //method takes source and destination as parameter
                            }

                        }
                        else {
                            if(!isEdgeExists(u,v,edges) && !isEdgeExists(v,u,edges))
                            {
                                edges.add(new DirectedEdge(v, u,randomCapacity));
                                // Updating the neighbours in adjacency list for vertex v
                                updateNeighborsForGivenVertex(v, u,randomCapacity,adjList);
                            }
                        }
                    }
                }
            }
        }

//        System.out.println(edges);
        return edges;

    }

    private static void updateNeighborsForGivenVertex(int source, int destination, int capacity, Map<Integer,ArrayList<EdgeWithCapacity>> adjList) {
        if (adjList.containsKey(source)) {
            //EdgeWithCapacity: v, cap
            EdgeWithCapacity newEdge = new EdgeWithCapacity(destination,capacity);
            ArrayList<EdgeWithCapacity> neighbors = adjList.get(source);

            //check if destination exists, not then add
            if(!neighbors.contains(newEdge))
            {
                neighbors.add(newEdge);
            }
        }
    }

    private static int getRandomCapacity(int upperCap) {
        //assign capacity as random number between 1..upperCap to each edge in the graph
        Random random = new Random();
        return random.nextInt(upperCap) + 1;
    }

    private static boolean isEdgeExists(int source, int destination, List<DirectedEdge> edges) {
        boolean edgeExists = false;
        for (DirectedEdge edge : edges) {
            if ((edge.source == source && edge.destination == destination)) {
                edgeExists = true;
                break;
            }
        }
        return edgeExists;
    }

    private static double getEuclideanDistanceBetweenVertices(int u, int v, List<Vertex> vertices) {
        return Math.sqrt(Math.pow(vertices.get(u).x - vertices.get(v).x, 2) +
                Math.pow(vertices.get(u).y - vertices.get(v).y, 2));
    }

    public static int getEdgeCapacity(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int destination)
    {
        if(graph.containsKey(source) && graph.get(source)!=null)
        {
            for(EdgeWithCapacity neighbor : graph.get(source))
            {
                if(neighbor.getVertex() == destination)
                {
                    return neighbor.getCapacity();
                }
            }
        }

        return 0;
    }

    public static Map<Integer, ArrayList<EdgeWithCapacity>> setEdgeCapacityInGivenGraph(Map<Integer, ArrayList<EdgeWithCapacity>> graph, int source, int destination, int capacity)
    {
        if(graph.containsKey(source) && graph.get(source)!=null)
        {
            for(EdgeWithCapacity neighbor : graph.get(source))
            {
                if(neighbor.getVertex() == destination)
                {
                    neighbor.setCapacity(capacity);
//                    System.out.println("neighbor capacity set: " + neighbor.getCapacity());
                }
            }
        }

        return graph;
    }

    public static void printGraph(int nodes, Map<Integer,ArrayList<EdgeWithCapacity>> graph)
    {
        for (int i = 0; i < nodes; i++) {
            System.out.print(i + " -> ");
            if(graph.get(i)!=null) {
                for (EdgeWithCapacity neighbor : graph.get(i)) {
                    System.out.print(neighbor.getVertex() + " ");
                }
                System.out.println();
            }
        }
    }

    public static int getRandomSource(int n, Set<Integer> selectedSources){
        Random random = new Random();
        int source;
        do {
            source = random.nextInt(n);
        } while (selectedSources.contains(source));
        return source;
    }

    public static Map<Integer,ArrayList<EdgeWithCapacity>> generateGraph(int n, double r, int upperCap) throws IOException {

        Map<Integer, ArrayList<EdgeWithCapacity>> adjList = createNewAdjList(n);

        //add vertices to make graph
        List<Vertex> vertices = addVertices(n,adjList);

        if(n>0 && r>0 && upperCap>0)
        {
            //add Edges
            addEdge(n,r,upperCap,vertices,adjList);
        }
        else {
            throw new InvalidObjectException("n, r and upperCap should be >0 ");
        }
        return adjList;
    }
}