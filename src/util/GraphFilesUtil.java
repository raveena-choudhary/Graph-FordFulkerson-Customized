package util;

import Algorithms.RandomSourceSinkGraphGenerator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Algorithms.RandomSourceSinkGraphGenerator.getEdgeCapacity;
import static util.FileConstants.directoryPath;

public class GraphFilesUtil {
    public static void createFile(Map<Integer, ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph, int graphNumber) throws IOException
    {
        String filename = "Graph_";
        File file = new File(directoryPath + File.separator + filename+graphNumber+".csv");
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
        writer.write("source"+","+"target"+","+"capacity"+"\n");
        for(Integer u : graph.keySet()) {
//            System.out.println(u);
            if(graph.get(u)==null || graph.get(u).isEmpty()){ //no neighbour exists
                writer.write(u + ",");
                writer.write("\n");
            }else { //neighbours exits
                for (RandomSourceSinkGraphGenerator.EdgeWithCapacity neighbor : graph.get(u)) {
                    Integer v = neighbor.getVertex();
                    writer.write(u + "," + String.join(",", v.toString()) + ","+ getEdgeCapacity(graph,u,v));
                    writer.write("\n");
                }
            }
        }

        writer.flush();
        writer.close();
    }

    public static Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> getGraphFromCSVFile(String filename)
    {
                Map<Integer,ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity>> graph = new HashMap<>();

                String csvFile = filename;
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    br.readLine();
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        int vertex1 = Integer.parseInt(values[0].trim());
                        int vertex2 = -1;
                        int capacity = -1;
                        if(values.length == 3)
                        {
                            vertex2 = Integer.parseInt(values[1].trim());
                            capacity = Integer.parseInt(values[2].trim());
                        }

//                        System.out.println("Vertex1: " + vertex1 + ", Vertex2: " + vertex2 + ", Capacity: " + capacity);

                        RandomSourceSinkGraphGenerator.EdgeWithCapacity vertexWithCapacity =  new RandomSourceSinkGraphGenerator.EdgeWithCapacity(vertex2,capacity);
                        if(graph.containsKey(vertex1))
                        {
                            ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> edges = new ArrayList<>(graph.get(vertex1));
                            edges.add(vertexWithCapacity);
                            graph.put(vertex1,edges);
                        }
                        else {
                            ArrayList<RandomSourceSinkGraphGenerator.EdgeWithCapacity> edges = new ArrayList<>();
                            edges.add(vertexWithCapacity);
                            graph.put(vertex1,edges);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        return graph;
    }

}
