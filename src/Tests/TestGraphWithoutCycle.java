package Tests;

import Algorithms.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.FileConstants.outputFilePath;

public class TestGraphWithoutCycle {

    private static Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> adjancencyList = new HashMap<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>>();
    private static String filename = outputFilePath + "Graph_Without_Cycle_results.csv";

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

        // Adding edges
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> a = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity one = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(1,10);
        RandomSourceSinkGraphGenerator.EdgeWithCapacity two = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(2,8);
        a.add(one);
        a.add(two);

        adjancencyList.put(0, a);


        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> b = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity three = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(2,20);
        RandomSourceSinkGraphGenerator.EdgeWithCapacity four = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(3,2);
        b.add(three);
        b.add(four);


        adjancencyList.put(1, b);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> c = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity five = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(3,5);
        RandomSourceSinkGraphGenerator.EdgeWithCapacity six = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(4,5);
        c.add(five);
        c.add(six);


        adjancencyList.put(2, c);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> d = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity seven = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(5,8);
        d.add(seven);

        adjancencyList.put(3, d);

        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> e = new ArrayList<>();
        RandomSourceSinkGraphGenerator.EdgeWithCapacity eight = new RandomSourceSinkGraphGenerator.EdgeWithCapacity(5,10);
        e.add(eight);

        adjancencyList.put(4, e);
        ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> f = new ArrayList<>();
        adjancencyList.put(5, f);
    }

    private static void printGraph(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph)
    {
        RandomSourceSinkGraphGenerator.printGraph(graph.size(),graph);
    }

    public static void runAlgo() throws IOException {
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
        assert (longestPath == expectedPath) : "Longest path is not correct";
        System.out.print("source: "+ source + " ");
        System.out.println("sink: "+ sink);

        //SAP algo
        System.out.println();
        System.out.println();
        System.out.println("--------------------------SAP Algo---------------------------------");
        ShortestAugmentingPath sap = new ShortestAugmentingPath();
        int maxFlowFromSAP = sap.getMaxFlowWithSAP(dag,source,sink);
        if(maxFlowFromSAP != 2)
            System.out.println("Max flow calculated from SAP algo is not correct");
        System.out.println("Max flow from SAP:" + maxFlowFromSAP);

        sap.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromSAP);

        //DFS Like algo
        System.out.println();
        System.out.println();
        System.out.println("-------------------------DFS Like Algo---------------------------------");
        DFSLikeAlgo dfsLikeAlgo = new DFSLikeAlgo();
        int maxFlowFromDFSLike= dfsLikeAlgo.getMaxFlowWithDFSLike(dag,source,sink);
        if(maxFlowFromDFSLike != 12)
            System.out.println("Max flow calculated from DFS like is not correct");
        System.out.println("Max flow from DFS Like:" + maxFlowFromDFSLike);

        dfsLikeAlgo.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromDFSLike);

        //Maximum Capacity
        System.out.println();
        System.out.println();
        System.out.println("-------------------------Maximum Capacity Algo---------------------------------");
        MaximumCapacity maxCap = new MaximumCapacity();
        int maxFlowFromMaxCap= maxCap.getMaxFlowWithMaximumCapacity(dag,source,sink);
        System.out.println("Max flow from Maximum Capacity:" + maxFlowFromMaxCap);
        if(maxFlowFromMaxCap != 12)
            System.out.println("Max flow calculated from Maximum capacity Algo is not correct");
        maxCap.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromMaxCap);

        //Random Algo
        System.out.println();
        System.out.println();
        System.out.println("-------------------------Random Algo---------------------------------");
        RandomAlgo randomAlgo = new RandomAlgo();
        int maxFlowFromRandom= randomAlgo.getMaxFlowWithRandom(dag,source,sink);
        System.out.println("Max flow from Random:" + maxFlowFromRandom);
        if(maxFlowFromRandom != 12)
            System.out.println("Max flow calculated from Random Algo is not correct");
        randomAlgo.printMatricesAndStoreResultsInFile(longestPath, dag,filename,maxFlowFromRandom);
    }

    public static void main(String[] args) throws IOException {
        runAlgo();
    }

}
