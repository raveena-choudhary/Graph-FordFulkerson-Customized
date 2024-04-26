package Algorithms;

/*Author: Raveena Choudhary, 40232370 */

import Tests.TestGraphWithoutCycle;
import util.AlgoIOUtils;
import util.ReadInputFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

//import static util.GenerateOutputFile.clearOutputDirectory;
import static util.AlgoIOUtils.createNewDirectory;
import static util.FileConstants.*;
import static util.GraphFilesUtil.createFile;
import static util.GraphFilesUtil.getGraphFromCSVFile;

public class FordFulkerson extends RandomSourceSinkGraphGenerator{


//    public static int n=100;
//    public static double r=0.5;
//    public static int upperCap = 50;

     public static Map<Integer,ArrayList<EdgeWithCapacity>> generatedGraph =  new HashMap<Integer,ArrayList<EdgeWithCapacity>>();
    private static int graphNumber=0;
     public static void main(String[] args) throws IOException, InterruptedException {


             System.out.println("Clearing previous output files...");
             //clearOutputDirectory
             AlgoIOUtils.purgeDirectory(outputFilePath);
             System.out.println("Clearing previous input files...");
             //clearInputDirectory
             AlgoIOUtils.purgeDirectory(directoryPath);

            System.out.println("COMP 6651 Project --> Ford Fulkerson");
            Scanner sc = new Scanner(System.in);
            int input = 0;
            do {
                System.out.println("");
                System.out.println("1. Generate Graph with Input File (Only InputFile.csv supported)");
                System.out.println("2. Run Ford Fulkerson");
                System.out.println("3. Enter values manually to generate graph");
                System.out.println("4. Run Ford Fulkerson with Test Graph without cycle in Tests");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                input = sc.nextInt();

                switch (input) {
                    case 1:
                    {
                        System.out.println("Generating graphs with input file(InputFile.csv) in InputFiles directory........");
                        String filename = inputFileCsvPath +File.separator+ "InputFile.csv";
                        List<ReadInputFile.InputData> inputs = ReadInputFile.readInputFromCSV(filename);
                        for(ReadInputFile.InputData ip: inputs)
                        {
                            int n = ip.getN();
                            double r = ip.getR();
                            int upperCap = ip.getUpperCap();

//                            createNewDirectory(directoryPath);
                            generateGraphForGivenInputs(n, r, upperCap);
                        }
                        break;
                    }
                    case 2: {
                        System.out.println("This function will read all graph file in GraphFiles folder one by one to run Ford fulkerson");
                        File folderPath = new File(directoryPath);

                        File filesList[] = folderPath.listFiles();
                        System.out.println("List of files and directories in the specified directory:");
                        for(File file : filesList) {
                            String fileName = file.getName();
                            System.out.println("---------------------------------------------------------------------------------------------");
                            System.out.println("File name: " + fileName);

                            String filePath = directoryPath + fileName;

                            if(file.exists())
                            {
                                Map<Integer,ArrayList<EdgeWithCapacity>> graph = getGraphFromCSVFile(filePath);
                                int n = graph.size();
//                                printGraph(n, graph);

                                createNewDirectory(outputFilePath);
                                //
                                String resultFileName = fileName.replace(".csv","");
                                resultFileName=outputFilePath+ resultFileName + "_results.csv";
                                //clear existing file content if running multiple times
                                AlgoIOUtils.purgeFileContent(resultFileName);
                                //run ford fulkerson
                                runFordFulkerson(graph,n,resultFileName);
                                System.out.println();
                                System.out.println("!!!Ford fulkerson completed!!!");


                            }
                        }
                        break;
                    }
                    case 3:
                    {
                        System.out.println("Please input values -->");
                        System.out.print("n: ");
                        int n = sc.nextInt();
                        System.out.print("r: ");
                        double r = sc.nextDouble();
                        System.out.print("upperCap: ");
                        int upperCap = sc.nextInt();

                        generateGraphForGivenInputs(n, r, upperCap);

                        break;
                    }
                    case 4: {
                        TestGraphWithoutCycle.runAlgo();
                        break;
                    }
                    case 5: {
                        System.out.println("Exiting the program..");
                        System.exit(0);
                        break;
                    }
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }

            } while (input != 0);

        }

    private static void generateGraphForGivenInputs(int n, double r, int upperCap) throws IOException {
        generatedGraph = generateGraph(n, r, upperCap);
        createNewDirectory(directoryPath);
        createFile(generatedGraph,++graphNumber);
        if(isGraphFileCreationSuccess())
        {
            System.out.println("Graph_" + graphNumber + ".csv generated successfully!");
        }
        else {
            System.out.println("Graph_" + graphNumber + " not generated..");
        }
    }

    public static void runFordFulkerson(Map<Integer,ArrayList<EdgeWithCapacity>> graph,int n,String filename) throws IOException {
            //remove cycles in a graph, create DAG
            //take first vertex as source
//            Map<Integer,ArrayList<EdgeWithCapacity>> dag = DAG.createDAG(n,getFirstKey(graph), graph);

//            System.out.println("Directed Acyclic graph:");
//            printGraph(n,graph);

            List<Integer> longestPath = LongestAcyclicPath.findLongestAcyclicPath(n,graph);
            System.out.println("-------------------------------Longest Acyclic Path----------------------------");
            int sink = longestPath.get(longestPath.size()-1);
            int source = longestPath.get(0);
            System.out.print("Longest Acyclic Path: "+ longestPath + " ");
            System.out.print("source: "+ source + " ");
            System.out.println("sink: "+ sink);
//
            System.out.println();
            System.out.println();
            //first algo SAP
            System.out.println("--------------------------SAP Algo----------------------------------------------");
            ShortestAugmentingPath sap = new ShortestAugmentingPath();
            int maxFlowFromSAP = sap.getMaxFlowWithSAP(graph,source,sink);
            System.out.println("Max flow from SAP:" + maxFlowFromSAP);
            System.out.println("---------------------------Metrices----------------------------------------------");
            sap.printMatricesAndStoreResultsInFile(longestPath, graph,filename,maxFlowFromSAP);

            System.out.println();
            System.out.println();
            System.out.println("-------------------------DFS Like Algo------------------------------------------");
            DFSLikeAlgo dfsLikeAlgo = new DFSLikeAlgo();
            int maxFlowFromDFSLike= dfsLikeAlgo.getMaxFlowWithDFSLike(graph,source,sink);
            System.out.println("Max flow from DFS Like:" + maxFlowFromDFSLike);
            System.out.println("----------------------------------Metrices---------------------------------------");
            dfsLikeAlgo.printMatricesAndStoreResultsInFile(longestPath, graph,filename,maxFlowFromDFSLike);

            System.out.println();
            System.out.println();
            System.out.println("-------------------------Maximum Capacity Algo-----------------------------------");
            MaximumCapacity maxCap = new MaximumCapacity();
            int maxFlowFromMaxCap= maxCap.getMaxFlowWithMaximumCapacity(graph,source,sink);
            System.out.println("Max flow from Maximum Capacity:" + maxFlowFromMaxCap);
            System.out.println("----------------------------------Metrices----------------------------------------");
            maxCap.printMatricesAndStoreResultsInFile(longestPath, graph,filename,maxFlowFromMaxCap);

            System.out.println();
            System.out.println();
            System.out.println("-------------------------Random Algo----------------------------------------------");
            RandomAlgo randomAlgo = new RandomAlgo();
            int maxFlowFromRandom= randomAlgo.getMaxFlowWithRandom(graph,source,sink);
            System.out.println("Max flow from Random:" + maxFlowFromRandom);
            System.out.println("----------------------------------Metrices----------------------------------------");
            randomAlgo.printMatricesAndStoreResultsInFile(longestPath, graph,filename,maxFlowFromRandom);

        }

    public static int getFirstKey(Map<Integer,ArrayList<EdgeWithCapacity>> map) {
        if (!map.isEmpty()) {
            return map.entrySet().stream()
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(-1);
        } else {
            return -1;
        }
    }

    public static boolean isGraphFileCreationSuccess() {
        String fileName = directoryPath + File.separator +"Graph_" + graphNumber + ".csv";
        File file = new File(fileName);
        if(file.exists())
        {
            return true;
        }
        return false;
    }
}
