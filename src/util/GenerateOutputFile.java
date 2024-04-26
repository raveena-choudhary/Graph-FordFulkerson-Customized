package util;

import java.io.*;

public class GenerateOutputFile {

    public static void storeResultsInFile(int paths, double ML, double MPL, int totalEdges,String algoName,String filename,int maxFlow) throws IOException
    {
        //filename = filename.replace(".csv","");
        File file = new File(filename);
        Writer writer = new FileWriter(file, true);

        writer.write("------------------------------" + algoName + "----------------------------");
        writer.write("\n");
        writer.write("Max Flow: " + maxFlow);
        writer.write("\n");
        writer.write("Number of augmenting paths: " + paths);
        writer.write("\n");
        writer.write("ML: " + ML);
        writer.write("\n");
        writer.write("MPL: " + MPL);
        writer.write("\n");
        writer.write("Total Edges in a graph: " + totalEdges);
        writer.write("\n");

        writer.flush();
        writer.close();

    }
}
