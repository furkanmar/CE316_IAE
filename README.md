# CE316 Project Team 5
This repository is the continuation of our project, which was started from "https://github.com/cansuarslangiray/CE316GroupProject" since we were having trouble running the project when we accessed our repository without the JavaFX plugin and subsequently installed JavaFX.
# Integrated Assignment Environment
## Goal
The primary objective of the Integrated Assignment Environment (IAE) is to enhance the effectiveness and efficiency of programming education by providing a comprehensive solution that simplifies and automates the cumbersome aspects of assignment management. This includes:
- Enabling educators to create assignments by defining environments, compilation processes, execution parameters, and expected outcomes.
- Automating all stages of the submission evaluation process, encompassing the analysis of student work, code examination, and comparison of results against predefined criteria.
- Generating detailed reports on each student's performance, outlining their achievements, areas for improvement, and potential areas of growth.
  
By utilizing the IAE, educators can focus more on teaching and less on administrative tasks, as the platform is designed to alleviate them from the manual and time-consuming aspects of assignment management.
## Application Details
The 'Integrated Assignment Environment' relies on Java, a widely used and adaptable programming language known for its dependable performance and ability to function across different platforms. Java was selected for its smooth file handling capabilities and JSON processing, while JavaFX was chosen for its modern graphical user interface (GUI), ensuring a user-friendly and responsive experience. This standalone application operates solely with the Java Runtime Environment (JRE), eliminating the need for external servers or databases. By avoiding intricate installation processes, this method ensures straightforward deployment on any Windows system equipped with the necessary Java setup.
### Application Overview
The aim of the Integrated Assignment Environment (IAE) is to offer educators a versatile solution for crafting, distributing, and evaluating programming tasks. It consolidates features such as output validation, compilation, execution, and feedback provision into a user-friendly platform. With its support for both compiled and interpreted languages, the IAE ensures broad suitability across diverse programming courses. Resilience is a fundamental aspect of its design, enabling it to adeptly manage errors and sustain the assessment process even when confronted with challenging submissions.
## Some Basic Requirements of the App
#### Users should have the capability to initiate a project employing either an existing configuration or a new one.
The createProject() method in Controller class manages new project creation. It determines if it's a fresh project or an edit operation, creates a new directory if needed, retrieves configuration and exam file paths, constructs a Project object, and saves it as JSON.
#### Users should have the capability to create, modify, and delete configurations as needed.
The updateTableView() method in the Controller class allows the configurations to be displayed by reading the existing configuration files and adding them to the table. When a new configuration is created or an existing configuration is edited, it retrieves the relevant information and creates a new file in JSON format or updates the existing file. When the user wants to remove a configuration, the deleteConfig() method deletes the selected configuration from the table and deletes the associated JSON file.
#### The software should be capable of compiling or interpreting source code using the project's configuration.
- Compiling and Running C and C++:
The compileAndRun and compileAndRunCPlus methods are used to compile and run C and C++ programs. First, a compile command is created with the specified compiler and source code. This command contains the name of the compiler, the name of the output file, and the path to the source code. The generated build command is initialized and run as a process via the ProcessBuilder class. The output generated during the compilation process (compileOutput) is captured and error checked. If the build fails, the build output is returned as an error message. After a successful build, the created executable is run and the runOutput is captured. Finally, the execution output is returned.
- Compiling and Running Java:
The compileAndRunJava method is used to compile and run Java programs. First, a build command is created with the specified compiler and source code. This command contains the name and output directory of the Java file. The output generated during the compilation process is checked. If compilation fails, an appropriate error message is returned. After successful compilation, the main class name is found and a MANIFEST file is created with this class name. Along with the MANIFEST file and other files, the JAR file is created and run. The output generated during the execution process is captured and returned.
- Compiling and Running Python:
The compileAndRunPython method is used to interpret Python scripts. First, a process is started with the specified Python command and script file. The output generated during the interpretation process is captured. If the interpretation process fails, an error is thrown and an appropriate error message is displayed.
#### The program needs to be capable of comparing the output generated by the student's code with the expected output.
The gradeSubmissions method first assigns two directory names, one indicating the folder where students' submitted files are located ("submissions_folder"), and the other indicating the location of the file containing the teacher's correct solution ("teachDirectory_folder"). Then, it extracts the students' submitted files from a .zip archive, creating a list of these files and initializing another list to store the results. Next, the method determines the correct solution provided by the teacher based on the file type. For example, it calls the compileAndRunJava method for Java files and compileAndRunCPlus for C++ files. Each student submission's results are compared. The student's code is processed similarly and normalized to the expected output. Then, these outputs are compared, and a score is calculated. The results list contains information such as the name of each student's submission, its output, the expected output, and the score.
## Classes
* Compiler Class:
This class contains methods for Java, Python, C and C++ to compile and run source codes. It also provides a function for packaging Java programs as JAR files.
* Configuration Class:
This class represents a project configuration and contains properties such as language, file path, and configuration name. It also contains methods that return and set properties of the object.
* Controller Class:
This class acts as a JavaFX controller and manages the interactions of the user interface. It provides functions such as selecting files and folders, creating, editing and deleting projects and configurations. It also stores and processes data by reading and writing JSON files.
![image](https://github.com/furkanmar/CE316_IAE/assets/105562039/ba002cbb-056a-48a0-9df0-285e50850a95)
##### Figure 1: Configuration screen view
* Project Class:
This class is used to represent projects. Each project contains information such as a project name, configuration name, and exam files path (examsPath). This class is used to create, edit and store projects. It can also be used to store and process the project's information by converting it to a JSON format or reading it from a JSON file.
![image](https://github.com/furkanmar/CE316_IAE/assets/105562039/89683dbf-5ce7-4bac-9403-dd11a7407d50)
##### Figure 2: Project Creation screen view
* QuizGrader Class:
This class is used to evaluate student exam files. The student exam files are located in a zip archive and there is a code file that is considered the teacher's solution. Each student file is checked to see if it produces an output similar to the teacher's solution and is scored according to the similarity rate. Assessment results are added to a list containing information such as the name of student files, output produced, expected output, and score.
![WhatsApp Image 2024-05-25 at 23 25 28](https://github.com/furkanmar/CE316_IAE/assets/105562039/e89aa625-879c-43cc-b455-7c0742636b28)
##### Figure 3: Quiz Management screen view
By choosing the desired Project type and pressing the Open button, you may view the list of student contributions on this screen. A submission of the Configuration type is launched and its output, together with the student's number and score, are shown on the screen when it is chosen from this list and the Run button is hit. When the "Run All" button is selected and the user sees the pertinent scores on the screen, all submissions are executed.
## Tests
#### Compiler 
###### Test Scenario 1
In this project, the compileAndRun method of the Compiler class is tested. Below is the test scenario and a JUnit test implementation steps containing this scenario.

@Test
    void testCompileAndRun() {
        String compilerPath = "gcc";
        String sourceCodePath = "test.c";
        String outputExecutablePath = "test";

        
        try {
            Files.write(Paths.get(sourceCodePath), ("#include <stdio.h>\nint main() { printf(\"Hello, World!\\n\"); return 0; }").getBytes());
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        }

        Compiler compiler = new Compiler();
        String output = compiler.compileAndRun(compilerPath, sourceCodePath, outputExecutablePath);

        // Expected output
        String expectedOutput = "Hello, World!\n";

        // Test if the output matches the expected output
        assertEquals(expectedOutput, output, "Output should be 'Hello, World!'.");
    }
}
##### Test Implementation
1. A C source file named test.c is created with the content that prints "Hello, World!".
2. The compileAndRun method of the Compiler class is called with the appropriate compiler path, source code path, and output executable path.
3. The output of the method is captured and compared to the expected output "Hello, World!".
4. The test verifies that the actual output matches the expected output, ensuring the system works as expected.
##### QuizGrader
###### Test Scenario 2
In this project, the gradeSubmissions method of the QuizGrader class is tested. Below is the test scenario and a JUnit test implementation steps containing this scenario.

@Test
    void testGradeSubmissions() {
        String zipFilePath = "test_submissions.zip";
        String teacherCodePath = "teacher_test.c";
        createZipWithSingleCFile(zipFilePath, "student_test.c", "#include <stdio.h>\nint main() { printf(\"Hello, Student!\\n\"); return 0; }");
      
        try {
            Files.write(Paths.get(teacherCodePath), ("#include <stdio.h>\nint main() { printf(\"Hello, World!\\n\"); return 0; }").getBytes());
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        }

        try {
            List<String> results = QuizGrader.gradeSubmissions(zipFilePath, teacherCodePath);

            assertEquals(4, results.size(), "Results should contain 4 elements.");
            assertEquals("test_submissions", results.get(0), "Submission name should match zip file name without extension.");
            assertTrue(results.get(1).contains("Hello, Student!"), "Student output should contain 'Hello, Student!'.");
            assertTrue(results.get(2).contains("Hello, World!"), "Expected output should contain 'Hello, World!'.");
            assertTrue(Integer.parseInt(results.get(3)) < 100, "Score should be less than 100 as outputs differ.");

        } catch (IOException e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    private void createZipWithSingleCFile(String zipFilePath, String fileName, String fileContent) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = fileContent.getBytes();
            zipOut.write(bytes, 0, bytes.length);
            zipOut.closeEntry();
        } catch (IOException e) {
            fail("Failed to create zip file: " + e.getMessage());
        }
    }
}
##### Test Implementation
1. Programmatically create a ZIP file named test_submissions.zip that contains a single C file named student_test.c.
2. Create a C source file named teacher_test.c with content that prints "Hello, World!".
3. Call the gradeSubmissions method of the QuizGrader class.
4. The test verifies that the actual output matches the expected output, ensuring the system works as expected.
##### Controller
###### Test Scenario 3
In this project, the readJsonFile_Configuration method of the Controller class is tested. Below is the test scenario and a JUnit test implementation steps containing this scenario.

 private static final String TEST_JSON_PATH = "test_config.json";
 private static final String NON_EXISTENT_JSON_PATH = "non_existent_config.json";


    @Test
    void testReadJsonFile_Configuration() {
        // Step 1: Create a JSON configuration file
        createTestJsonFile(TEST_JSON_PATH, "{ \"setting1\": \"value1\", \"setting2\": 42, \"setting3\": true }");

        // Step 2: Read the configuration file
        Configuration config = null;
        try {
            config = readJsonFile_Configuration(TEST_JSON_PATH);
        } catch (IOException e) {
            fail("IOException should not occur: " + e.getMessage());
        }

   
        assertNotNull(config, "Configuration object should not be null.");
        assertEquals("value1", config.getSetting1(), "setting1 should be 'value1'.");
        assertEquals(42, config.getSetting2(), "setting2 should be 42.");
        assertTrue(config.isSetting3(), "setting3 should be true.");

        // Step 4: Handle file not found scenario
        try {
            config = readJsonFile_Configuration(NON_EXISTENT_JSON_PATH);
        } catch (IOException e) {
            fail("IOException should not occur: " + e.getMessage());
        }
        assertNull(config, "Configuration object should be null for non-existent file.");
    }

    private void createTestJsonFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            fail("Failed to create test JSON file: " + e.getMessage());
        }
    }

    private static Configuration readJsonFile_Configuration(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath)) {
            return new Gson().fromJson(fileReader, Configuration.class);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Creating a new object.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    class Configuration {
        private String setting1;
        private int setting2;
        private boolean setting3;

        public String getSetting1() {
            return setting1;
        }

        public int getSetting2() {
            return setting2;
        }

        public boolean isSetting3() {
            return setting3;
        }
    }
 ##### Test Implementation
1. Programmatically create a JSON file named test_config.json with content that matches the Configuration class structure.
2. Call the readJsonFile_Configuration method with the path of the JSON file (test_config.json).
3. Assert that the Configuration object contains the expected values.
4. Call the readJsonFile_Configuration method with a non-existent file path (non_existent_config.json).
5. Capture the returned Configuration object (expected to be null) and check for console output indicating the file was not found.
6. The test verifies that the actual output matches the expected output, ensuring the system works as expected.
  
