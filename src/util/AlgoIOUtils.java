package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AlgoIOUtils {
    public static void purgeDirectory(String directory)
    {
        if(directory==null || directory.isEmpty()){
            System.out.println("directory path is null or empty, can not proceed cleanup!");
        }
        try {
            List<File> filesInDirectory = Files.list(Paths.get(directory))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .collect(Collectors.toList());
            filesInDirectory.forEach(file -> {
                file.delete();
                System.out.println(file+" deleted");
            });
        } catch (IOException e) {
            System.out.println("Warning:: Exception occurred in directory cleanup!");
            System.out.println("Directory Path: "+directory);
            System.out.println("Cause: "+e.getCause());
        }
    }

    public static void createNewDirectory(String directory){
        try {
            if(Files.exists(Paths.get(directory))){
//                System.out.println(directory+" already exists! skip creating new one.");
                return;
            }
            Files.createDirectory(Paths.get(directory));
            System.out.println(directory+" created successfully!");
        } catch (IOException e) {
            System.out.println("Warning:: Exception occurred in directory creation!");
            System.out.println("Directory Path: "+directory);
            System.out.println("Cause: "+e.getCause());
        }
    }

    public static void purgeFileContent(String filename) {
        try {
            if(!Files.exists(Paths.get(filename))){
                System.out.println(filename+" file not exists yet");
                return;
            }
            // Create FileWriter with false to disable append mode
            FileWriter writer = new FileWriter(filename, false);
            // Write an empty string to clear the content
            writer.write("");
            // Flush and close the writer
            writer.flush();
            writer.close();

            System.out.println("File content cleared successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
