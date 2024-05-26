package com.example.ce316_project;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.text.similarity.LevenshteinDistance;

import javax.swing.filechooser.FileSystemView;

public class QuizGrader {
    static String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "IAE";

    public static List<String> gradeSubmissions(String zipFilePath, String teacherCodePath) throws IOException {
        String subDirectory = defaultDirectoryPath + File.separator +  "out";
        File parentDir = new File(subDirectory);
        parentDir.mkdir();
        File dir = new File(subDirectory + File.separator + "generatedExes");
        dir.mkdir();

        Path projectRoot = FileSystems.getDefault().getPath("").toAbsolutePath();
        String gccPath = Paths.get(projectRoot.toString(), "compilers", "gcc", "bin", "gcc.exe").toString();
        String gppPath = Paths.get(projectRoot.toString(), "compilers", "gcc", "bin", "g++.exe").toString();
        String pythonPath = Paths.get(projectRoot.toString(), "compilers", "python", "python.exe").toString();

        List<File> submissions = unzip(zipFilePath, subDirectory);
        List<String> results = new ArrayList<>();
        File answer = new File(teacherCodePath);
        String teacherOutputRaw;

        if (answer.getName().endsWith(".java")) {
            File javaDir = new File(dir.getPath() + File.separator + "java");
            javaDir.mkdir();
            teacherOutputRaw = Compiler.compileAndRunJava("javac", answer.getPath(), javaDir + File.separator + "executable");
        } else if (answer.getName().endsWith(".cpp")) {
            File cppDir = new File(dir.getPath() + File.separator + "cpp");
            cppDir.mkdir();
            teacherOutputRaw = Compiler.compileAndRunCPlus(gppPath, answer.getPath(), cppDir + File.separator + "executable");
        } else if (answer.getName().endsWith(".c")) {
            File cDir = new File(dir.getPath() + File.separator + "c");
            cDir.mkdir();
            teacherOutputRaw = Compiler.compileAndRun(gccPath, answer.getPath(), cDir + File.separator + "executable");
        } else if (answer.getName().endsWith(".py")) {
            teacherOutputRaw = Compiler.compileAndRunPython(pythonPath, answer.getPath());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + answer.getName());
        }

        for (File submission : submissions) {
            String studentOutputRaw;
            String output;
            if (submission.getName().endsWith(".java")) {
                File javaDir = new File(dir.getPath() + File.separator + "java");
                javaDir.mkdir();
                studentOutputRaw = Compiler.compileAndRunJava("javac", submission.getPath(), javaDir + File.separator + "executable");
                output = normalizeOutput(studentOutputRaw);
            } else if (submission.getName().endsWith(".cpp")) {
                File cppDir = new File(dir.getPath() + File.separator + "cpp");
                cppDir.mkdir();
                studentOutputRaw = Compiler.compileAndRunCPlus(gppPath, submission.getPath(), cppDir + File.separator + "executable");
                output = normalizeOutput(studentOutputRaw);
            } else if (submission.getName().endsWith(".c")) {
                File cDir = new File(dir.getPath() + File.separator + "c");
                cDir.mkdir();
                studentOutputRaw = Compiler.compileAndRun(gccPath, submission.getPath(), cDir + File.separator + "executable");
                output = normalizeOutput(studentOutputRaw);
            } else if (submission.getName().endsWith(".py")) {
                studentOutputRaw = Compiler.compileAndRunPython(pythonPath, submission.getPath());
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

    public static List<File> unzip(String zipFilePath, String destDirectory) throws IOException {
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
