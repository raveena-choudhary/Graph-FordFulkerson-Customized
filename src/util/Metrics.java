package util;

import Algorithms.RandomSourceSinkGraphGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Metrics {

    //the number of augmenting paths required until Ford-Fulkerson completes
    public static int getNumberOfAugmentingPaths(List<List<Integer>> paths){
        return paths.size();
    }

    public static double getML(List<List<Integer>> paths){

        int numberOfEdges = 0;
        for(List<Integer> path : paths)
        {
            numberOfEdges+=path.size()-1;
        }
//        System.out.print("Number of edges: " + numberOfEdges);
        return (int)(numberOfEdges/getNumberOfAugmentingPaths(paths));
    }

    //the average length of the augmenting path as a fraction of the longest acyclic path from s to t
    public static double getMPL(List<List<Integer>> paths,List<Integer> longestPath){
            double mean_length = getML(paths);
            int longest_path_edge_length = longestPath.size()-1;
            if(longest_path_edge_length>0)
                return (mean_length/longest_path_edge_length);
            return 0;
    }

    //the total number of edges in the graph
    public static int getTotalEdges(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph){

        int totalEdges = 0;

        for(Integer u: graph.keySet())
        {
            for(RandomSourceSinkGraphGenerator.EdgeWithCapacity neighbor: graph.get(u))
            {
                totalEdges++;
            }
        }

        return totalEdges;
    }

}
