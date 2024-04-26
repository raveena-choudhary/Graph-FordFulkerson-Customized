package Tests;

import Algorithms.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.FileConstants.outputFilePath;

public class TestGraphWithCycle {

    private static Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> adjancencyList = new HashMap<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>>();
    private static String filename = outputFilePath + "Graph_With_Cycle_";
    private static void createGraph()
    {

        Graph graph = new Graph();

        // Adding vertices
        graph.addVertex(0);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        // Adding edges
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(1, 4);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(5, 2);
        graph.addEdge(5, 6);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> a = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity one = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(1,10);
        a.add(one);

        adjancencyList.put(0, a);


        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> b = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity two = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(2,9);
        RandomSourceSinkGraphGenerator.EdgeWithCapacity three = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(4,8);
        b.add(two);
        b.add(three);

        adjancencyList.put(1, b);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> c = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity four = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(3,9);
        c.add(four);

        adjancencyList.put(2, c);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> d = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity five = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(1,6);
        d.add(five);
        d.add(new RandomSourceSinkGraphGenerator.EdgeWithCapacity(4,7));
        d.add(new RandomSourceSinkGraphGenerator.EdgeWithCapacity(5,9));


        adjancencyList.put(3, d);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> e = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity six = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(5,6);
        RandomSourceSinkGraphGenerator.EdgeWithCapacity seven = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(6,7);
        e.add(six);
        e.add(seven);

        adjancencyList.put(4, e);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> f = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity eight = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(6,10);
        f.add(eight);

        adjancencyList.put(5, f);
        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> g = new ArrayList<>();
        adjancencyList.put(6, g);
    }

    private static void printGraph(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph)
    {
        RandomSourceSinkGraphGenerator.printGraph(graph.size(),graph);
    }

    public static void main(String[] args) throws IOException {
        //create graph
        createGraph();
        //print graph
        System.out.println("Generated Graph: ");
        printGraph(adjancencyList);

        //create DAG
        int n = adjancencyList.size();
        Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> dag = DAG.createDAG(n,0, adjancencyList);
        System.out.println("Directed Acyclic graph:");
        printGraph(dag);

        //findLongestAcyclicPath - using bfs
        List<Integer> longestPath = LongestAcyclicPath.findLongestAcyclicPath(n,dag);
        System.out.println("-------------------------------Longest Acyclic Path----------------------------");
        int sink = longestPath.get(longestPath.size()-1);
        int source = longestPath.get(0);
        System.out.print("Longest Acyclic Path: "+ longestPath + " ");
        List<Integer> expectedPath = new ArrayList<>();
        expectedPath.add(0);
        expectedPath.add(1);
        expectedPath.add(2);
        expectedPath.add(3);
        expectedPath.add(5);
        System.out.print("source: "+ source + " ");
        System.out.println("sink: "+ sink);

        //SAP algo
        System.out.println();
        System.out.println();
        System.out.println("--------------------------SAP Algo---------------------------------");
        ShortestAugmentingPath sap = new ShortestAugmentingPath();
        int maxFlowFromSAP = sap.getMaxFlowWithSAP(dag,source,sink);
        System.out.println("Max flow from SAP:" + maxFlowFromSAP);
        sap.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromSAP);

        //DFS Like algo
        System.out.println();
        System.out.println();
        System.out.println("-------------------------DFS Like Algo---------------------------------");
        DFSLikeAlgo dfsLikeAlgo = new DFSLikeAlgo();
        int maxFlowFromDFSLike= dfsLikeAlgo.getMaxFlowWithDFSLike(dag,source,sink);
        System.out.println("Max flow from DFS Like:" + maxFlowFromDFSLike);
        dfsLikeAlgo.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromDFSLike);

        //Maximum Capacity
        System.out.println();
        System.out.println();
        System.out.println("-------------------------Maximum Capacity Algo---------------------------------");
        MaximumCapacity maxCap = new MaximumCapacity();
        int maxFlowFromMaxCap= maxCap.getMaxFlowWithMaximumCapacity(dag,source,sink);
        System.out.println("Max flow from Maximum Capacity:" + maxFlowFromMaxCap);
        maxCap.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromMaxCap);

        //Random Algo
        System.out.println();
        System.out.println();
        System.out.println("-------------------------Random Algo---------------------------------");
        RandomAlgo randomAlgo = new RandomAlgo();
        int maxFlowFromRandom= randomAlgo.getMaxFlowWithRandom(dag,source,sink);
        System.out.println("Max flow from Random:" + maxFlowFromRandom);
        randomAlgo.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromRandom);
    }
}
