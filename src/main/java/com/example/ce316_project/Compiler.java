package com.example.ce316_project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
    public static String compileAndRun(String compilerPath, String sourceCodePath, String outputExecutablePath) {
        try {
            List<String> compileCommand = Arrays.asList(compilerPath, "-o", outputExecutablePath, sourceCodePath);
            System.out.println("Compile command: " + String.join(" ", compileCommand));  // Log the actual command being run

            ProcessBuilder compileBuilder = new ProcessBuilder(compileCommand);
            File gccBinDir = new File(compilerPath).getParentFile();
            compileBuilder.environment().put("PATH", gccBinDir.getAbsolutePath() + File.pathSeparator + System.getenv("PATH"));
            compileBuilder.redirectErrorStream(true);
            Process compileProcess = compileBuilder.start();

            BufferedReader compileOutputReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
            StringBuilder compileOutput = new StringBuilder();
            String compileLine;
            while ((compileLine = compileOutputReader.readLine()) != null) {
                compileOutput.append(compileLine).append("\n");
            }
            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                return "Compilation failed:\n" + compileOutput.toString();
            }

            ProcessBuilder runBuilder = new ProcessBuilder(outputExecutablePath);
            runBuilder.redirectErrorStream(true);
            Process runProcess = runBuilder.start();

            BufferedReader runOutputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            String runLine;
            while ((runLine = runOutputReader.readLine()) != null) {
                runOutput.append(runLine).append("\n");
            }
            int runExitCode = runProcess.waitFor();
            if (runExitCode != 0) {
                return "Execution failed:\n" + runOutput.toString();
            }

            return runOutput.toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error during compilation or execution: " + e.getMessage();
        }
    }

    public static String compileAndRunCPlus(String compilerPath, String sourceCodePath, String outputExecutablePath) {
        try {
            List<String> compileCommand = Arrays.asList(compilerPath, "-o", outputExecutablePath, sourceCodePath);
            System.out.println("Compile command: " + String.join(" ", compileCommand));  // Log the actual command being run
            ProcessBuilder compileBuilder = new ProcessBuilder(compileCommand);
            File gppBinDir = new File(compilerPath).getParentFile();
            compileBuilder.environment().put("PATH", gppBinDir.getAbsolutePath() + File.pathSeparator + System.getenv("PATH"));
            compileBuilder.redirectErrorStream(true);
            Process compileProcess = compileBuilder.start();
            BufferedReader compileOutputReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
            StringBuilder compileOutput = new StringBuilder();
            String compileLine;
            while ((compileLine = compileOutputReader.readLine()) != null) {
                compileOutput.append(compileLine).append("\n");
            }
            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                System.err.println("Compilation error: " + compileOutput.toString());
                return "Compilation error:\n" + compileOutput.toString();
            }
            ProcessBuilder runBuilder = new ProcessBuilder(outputExecutablePath);
            runBuilder.environment().put("PATH", gppBinDir.getAbsolutePath() + File.pathSeparator + System.getenv("PATH"));
            runBuilder.redirectErrorStream(true);
            Process runProcess = runBuilder.start();
            BufferedReader runOutputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            String runLine;
            while ((runLine = runOutputReader.readLine()) != null) {
                runOutput.append(runLine).append("\n");
            }
            runProcess.waitFor();
            int runExitCode = runProcess.exitValue();
            if (runExitCode != 0) {
                System.err.println("Execution error: Exit code " + runExitCode);
                return "Execution error: Exit code " + runExitCode;
            }
            return runOutput.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error during compilation or execution: " + e.getMessage();
        }
    }
    public static String compileAndRunJava(String compilerPath, String sourceCodePath, String outputExecutablePath) {
        try {

            Process compileProcess = Runtime.getRuntime().exec(compilerPath + " -d " + outputExecutablePath + " " + sourceCodePath);
            compileProcess.waitFor();
            if (compileProcess.exitValue() != 0) {
                return "Compilation error";
            }
            String mainClassName = findMainClassName(sourceCodePath);
            if (mainClassName == null) {
                return "Main class not found";
            }
            createManifestFile(outputExecutablePath, mainClassName);
            ProcessBuilder jarProcessBuilder = new ProcessBuilder("jar", "cvfm", mainClassName + ".jar", "META-INF/MANIFEST.MF", ".");
            jarProcessBuilder.directory(new File(outputExecutablePath));
            Process jarProcess = jarProcessBuilder.start();
            jarProcess.waitFor();
            if (jarProcess.exitValue() != 0) {
                BufferedReader jarErrorReader = new BufferedReader(new InputStreamReader(jarProcess.getErrorStream()));
                StringBuilder errorMessage = new StringBuilder();
                String line;
                while ((line = jarErrorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                return "Jar creation error: " + errorMessage.toString();
            }

            Process runProcess = Runtime.getRuntime().exec("java -jar " + outputExecutablePath + "/" + mainClassName + ".jar");
            BufferedReader runOutputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            String line;
            while ((line = runOutputReader.readLine()) != null) {
                runOutput.append(line).append("\n");
            }
            runProcess.waitFor();

            String output = runOutput.toString().trim();
            return  output;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error during compilation, JAR creation, or execution: " + e.getMessage();
        }
    }
    public static String compileAndRunPython(String pythonCommand, String scriptPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, scriptPath);
        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("Python process interrupted", e);
        }
        if (exitCode != 0) {
            throw new RuntimeException("Python process returned non-zero exit code: " + exitCode);
        }
        return output.toString();
    }

    private static String findMainClassName(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String mainClassName = extractMainClassName(line);
                if (mainClassName != null) {
                    return mainClassName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String extractMainClassName(String line) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*\\{");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void createManifestFile(String outputExecutablePath, String mainClassName) throws IOException {
        String manifestContent = "Manifest-Version: 1.0\n"
                + "Main-Class: " + mainClassName + "\n";

        Path manifestFilePath = Paths.get(outputExecutablePath, "META-INF", "MANIFEST.MF");
        Files.createDirectories(manifestFilePath.getParent());
        Files.write(manifestFilePath, manifestContent.getBytes());
    }
}

