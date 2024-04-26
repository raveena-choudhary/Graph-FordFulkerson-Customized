package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadInputFile {

    public static class InputData {

        int n;
        double r;
        int upperCap;

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public double getR() {
            return r;
        }

        public void setR(double r) {
            this.r = r;
        }

        public int getUpperCap() {
            return upperCap;
        }

        public void setUpperCap(int upperCap) {
            this.upperCap = upperCap;
        }

        public InputData(int n, double r, int upperCap)
        {
            this.n = n;
            this.r = r;
            this.upperCap = upperCap;
        }
    }
    public static List<InputData> readInputFromCSV(String filename)
    {
        String inputFile = filename;
        ArrayList<InputData> inputDataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if(values.length == 3)
                {
                    int n = Integer.parseInt(values[0].trim().replaceAll("\"", ""));
                    double r = Double.parseDouble(values[1].trim().replaceAll("\"", ""));
                    int upperCap = Integer.parseInt(values[2].trim().replaceAll("\"", ""));

                    InputData input = new InputData(n,r,upperCap);
                    inputDataList.add(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputDataList;
    }
}


