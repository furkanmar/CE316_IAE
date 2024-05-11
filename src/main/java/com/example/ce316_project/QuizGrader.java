package com.example.ce316_project;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class QuizGrader {

    public static List<String> gradeSubmissions(String zipFilePath, String teacherCodePath) throws IOException {
        String subDirectory = "submissions_folder";
        String teachDirectory = "teachDirectory_folder";

        List<File> submissions = unzip(zipFilePath, subDirectory);
        List<String> results = new ArrayList<>();
        File answer = new File(teacherCodePath);
            String teacherOutputRaw;
            if (answer.getName().endsWith(".java")) {
                teacherOutputRaw = Compiler.compileAndRunJava("javac", answer.getPath(), "my_prgrm2");
            } else if (answer.getName().endsWith(".cpp")) {
                teacherOutputRaw = Compiler.compileAndRunCPlus("g++", answer.getPath(), "my_prgrm");
            } else if (answer.getName().endsWith(".c")) {
                teacherOutputRaw = Compiler.compileAndRun("gcc", answer.getPath(), "hello");
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + answer.getName());
            }

        for (File submission : submissions) {
            String studentOutputRaw;
            String output;
            if (submission.getName().endsWith(".java")) {
                studentOutputRaw = Compiler.compileAndRunJava("javac", submission.getPath(), "my_prgrm2");
                output = normalizeOutput(studentOutputRaw);
            } else if (submission.getName().endsWith(".cpp")) {
                studentOutputRaw = Compiler.compileAndRunCPlus("g++", submission.getPath(), "my_prgrm");
                output = normalizeOutput(studentOutputRaw);
            } else if (submission.getName().endsWith(".c")) {
                System.out.println("Here");
                studentOutputRaw = Compiler.compileAndRun("gcc", submission.getPath(), "hello");
                output = normalizeOutput(studentOutputRaw);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + submission.getName());
            }
            String expectedOutput = normalizeOutput(teacherOutputRaw);
            int score = calculateScore(output, expectedOutput);
            results.add(new File(zipFilePath).getName().replaceAll("\\.zip$", ""));
            results.add(output);
            results.add(expectedOutput);
            results.add(Integer.toString(score));
            //logOutputComparison(submission.getName(), output, expectedOutput, score);
        }
        return results;
    }


    public static int calculateScore(String actualOutput, String expectedOutput) {
        LevenshteinDistance levDist = new LevenshteinDistance();
        int distance = levDist.apply(actualOutput, expectedOutput);
        int maxLength = Math.max(actualOutput.length(), expectedOutput.length());
        double similarity = (maxLength - distance) / (double) maxLength;
        return (int) (similarity * 100);
    }

    private static String normalizeOutput(String output) {
        return output.replaceAll("\\s+", " ").replaceAll("\\r\\n", "\n").replaceAll("\\n", " ").trim();
    }

    private static void logOutputComparison(String studentName, String actualOutput, String expectedOutput, int score) {
        System.out.println("Comparing outputs for " + studentName + ":");
        System.out.println("Student Output: " + actualOutput);
        System.out.println("Expected Output: " + expectedOutput);
        System.out.println("Score: " + score);
    }

    private static List<File> unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        List<File> files = new ArrayList<>();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
                files.add(new File(filePath));
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return files;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
