package com.example.ce316_project;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Controller implements Initializable {
    Gson gson = new Gson();
    String configimportfilepath;
    String submissionFolderPath;
    String importedSourceFile;
    String examimportfilepath;
    String importedexamFile;
    String answerimportfilePath;
    String sourceimportfilePath;
    String sourceimportFile;
    String importedanswerFile;
    String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "IAE";
    String filePath;
    ArrayList<Configuration> configList = new ArrayList<>();
    ArrayList<Project> projectList = new ArrayList<>();
    private FileChooser fileChooser = new FileChooser();
    private String expectedOutputPath;
    List<String> submissionFiles = new ArrayList<>();
    boolean projectOpened = false;
    boolean configOpened = false;
    String importConfigPath;
    String sourceFileExtension;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langcombo.setOnMouseClicked(e -> langcombo());
        configCombo.setOnMouseClicked(e -> configcombo());
        configComboProject.setOnMouseClicked(e -> projectCombo());
        projectCombo.setOnMouseClicked(e -> projectCombo());

    }

    @FXML
    public void langcombo() {
        ArrayList<String> languageList = new ArrayList<>();
        languageList.add("Java");
        languageList.add("C++");
        languageList.add("Python");
        languageList.add("C");
        ObservableList<String> oblist = FXCollections.observableArrayList(languageList);
        langcombo.setItems(oblist);
    }

    @FXML
    public void configcombo() {
        String configFilePath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
        File cFile = new File(configFilePath);
        String[] files = cFile.list();
        ArrayList<String> nameOfFile = new ArrayList<>();
        for (String i : files) {
            String name = i.split("\\.")[0];
            nameOfFile.add(name);
        }

        ObservableList<String> oblist = FXCollections.observableArrayList(nameOfFile);
        configCombo.setItems(oblist);
    }

    public void configComboProject() {
        String configFilePath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
        File cFile = new File(configFilePath);
        String[] files = cFile.list();
        ArrayList<String> nameOfFile = new ArrayList<>();
        for (String i : files) {
            String name = i.split("\\.")[0];
            nameOfFile.add(name);
        }

        ObservableList<String> oblist = FXCollections.observableArrayList(nameOfFile);
        configComboProject.setItems(oblist);
    }

    @FXML
    public void projectCombo() {
        String projectFilePath = defaultDirectoryPath + File.separator + "Project" + File.separator;
        File pFile = new File(projectFilePath);
        String[] files = pFile.list();
        ArrayList<String> nameOfFile = new ArrayList<>();
        for (String i : files) {
            String name = i.split("\\.")[0];
            nameOfFile.add(name);
        }

        ObservableList<String> oblist = FXCollections.observableArrayList(nameOfFile);
        configComboProject.setItems(oblist); // Update configComboProject instead of projectCombo
        projectCombo.setItems(oblist); // Keep this for the Project Creation tab
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

    @FXML
    public void deleteConfig(ActionEvent event) throws IOException {
        if (configtable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Configurations Found");
            alert.setContentText("There are no configurations available to delete.");
            alert.showAndWait();
            return;
        }



        filePath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
        int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Configuration selectedConfig = configtable.getItems().get(selectedIndex);
            String jsonFileName = selectedConfig.getConfigName() + ".json";
            String jsonFilePath = filePath + jsonFileName;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Configuration");
            alert.setContentText("Are you sure you want to delete the configuration '" + selectedConfig.getConfigName() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Delete the JSON file
                    Files.deleteIfExists(Paths.get(jsonFilePath));

                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to delete file");
                    errorAlert.setContentText("An error occurred while deleting the file: " + e.getMessage());
                    errorAlert.showAndWait();
                    return;
                }

                updateTableView(event);
            }
        }
    }

    @FXML
    public void ImportsourceFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File selectedfile = fileChooser.showOpenDialog(null);
        configimportfilepath = selectedfile.getAbsolutePath();
        importedSourceFile = selectedfile.getName();
        sourceFileExtension = getFileExtension(importedSourceFile);
        sourcefileimport.setText(importedSourceFile);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    @FXML // arayüzdeki createConfigiration butonu ile aktifleşecek
    public void createConfig(ActionEvent event) throws IOException {
        String selectedLanguage = langcombo.getValue();
        if (!isExtensionCompatible(selectedLanguage, sourceFileExtension)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Source File");
            alert.setHeaderText("Source file extension does not match the selected language.");
            alert.setContentText("Please select a valid source file for the chosen language.");
            alert.showAndWait();
            return;
        }

        if (configOpened) {
            int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
                String f2 = configPath + configtable.getItems().get(selectedIndex).getConfigName() + ".json";
                File file = new File(f2);
                file.delete();
                if (configimportfilepath == null) {
                    configimportfilepath = configtable.getItems().get(selectedIndex).getFilePath();
                }
                Configuration config = new Configuration(langcombo.getValue(), configimportfilepath, configname.getText());
                configOpened = false;
                String newJson = gson.toJson(config);

                String newFilePath = configname.getText() + ".json";
                File f3 = new File(configPath, newFilePath);
                try (FileWriter fileWriter = new FileWriter(f3)) {
                    fileWriter.write(newJson);
                    System.out.println("JSON written to file successfully.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

        } else {
            File dir = new File(defaultDirectoryPath);
            dir.mkdir();
            File configFile = new File(dir, "Configuration");
            configFile.mkdir();
            Gson gson = new Gson();

            String language = langcombo.getValue();
            String fPath = configimportfilepath;
            sourcefileimport.setText(importedSourceFile);
            String configName = configname.getText();

            Configuration config = new Configuration(language, fPath, configName);

            String newJson = gson.toJson(config);
            String newFilePath = configName + ".json";
            File file = new File(configFile, newFilePath);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(newJson);
                System.out.println("JSON written to file successfully.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        updateTableView(event);
        clearConfigMenu();
    }

    private boolean isExtensionCompatible(String language, String extension) {
        switch (language) {
            case "Java":
                return "java".equalsIgnoreCase(extension);
            case "C++":
                return "cpp".equalsIgnoreCase(extension);
            case "Python":
                return "py".equalsIgnoreCase(extension);
            case "C":
                return "c".equalsIgnoreCase(extension);
            default:
                return false;
        }
    }

    @FXML
    public void updateTableView(ActionEvent event) throws IOException {
        String configFilePath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
        File cFile = new File(configFilePath);
        String[] files = cFile.list();
        // Check if there are any configurations
        if (files == null || files.length == 0) {
            configtable.getItems().clear(); // Clear table if no configurations found
            return;
        }

        for (String c : files) {
            String path = configFilePath + c;
            Configuration config1 = readJsonFile_Configuration(path);
            configList.add(config1);
        }
        configtable.getItems().clear();

        // Re-adding
        ObservableList<Configuration> configurationList = FXCollections.observableArrayList(configList);
        configtable.setItems(configurationList);
        configtablename.setCellValueFactory(new PropertyValueFactory<>("configName"));
        configtablesource.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        configtablelang.setCellValueFactory(new PropertyValueFactory<>("language"));
        configList.clear();
    }

    public void clearConfigMenu() {
        langcombo.setValue("");
        sourcefileimport.setText("Import");
        configname.setText("");
    }

    @FXML
    public void openConfig(ActionEvent event) throws IOException {
        if (configtable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Configurations Found");
            alert.setContentText("There are no configurations available to edit.");
            alert.showAndWait();
            return;
        }

        int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Configuration config = configtable.getItems().get(selectedIndex);
            langcombo.setValue(config.getLanguage());
            if (config.getFilePath() != null) {
                Path p1 = Paths.get(config.getFilePath());
                sourcefileimport.setText(p1.getFileName().toString());
            } else {
                sourcefileimport.setText("");
            }
            configname.setText(config.getConfigName());
            configOpened = true;
        }
    }
    @FXML
    void HelpButton(ActionEvent event) {

        String helpDoc = "IAE_Help_Guide.pdf";

        File file = new File(helpDoc);
        if (file.exists()){
            try {
                String filePath = new File(helpDoc).getAbsolutePath();
                Process process = Runtime.getRuntime().exec("cmd /c start \"\" \"" + filePath + "\"");
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Failed to open the file.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    public void configClearButton(ActionEvent event) {
        clearConfigMenu();
        configOpened = false;
    }

    @FXML
    public void importConfig(ActionEvent event) throws IOException {
        configOpened = false;
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        importConfigPath = selectedFile.getPath();
        Configuration config = readJsonFile_Configuration(importConfigPath);
        configname.setText(config.getConfigName());
        langcombo.setValue(config.getLanguage());
        sourcefileimport.setText(config.getFilePath());
        createConfig(event);
    }

    @FXML
    public void exportConfig(ActionEvent event) {
        Configuration selectedConfig = configtable.getSelectionModel().getSelectedItem();
        if (selectedConfig != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(selectedConfig.getConfigName() + ".json");
            File exportFile = fileChooser.showSaveDialog(null);

            if (exportFile != null) {
                if (exportFile.exists()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("File Exists");
                    alert.setHeaderText("The file already exists.");
                    alert.setContentText("Do you want to overwrite the existing file?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() != ButtonType.OK) {
                        return;
                    }
                }

                try (FileWriter writer = new FileWriter(exportFile)) {
                    gson.toJson(selectedConfig, writer);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Configuration exported successfully.");
                    alert.showAndWait();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Failed");
                    alert.setHeaderText("Failed to export configuration.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Configuration Selected");
            alert.setContentText("Please select a configuration to export.");
            alert.showAndWait();
        }
    }

    // PROJECT METHODS

    private static Project readJsonFile_Project(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath)) {
            return new Gson().fromJson(fileReader, Project.class);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Creating a new object.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void deleteProject(ActionEvent event) throws IOException {
        if (projecttable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Projects Found");
            alert.setContentText("There are no projects available to delete.");
            alert.showAndWait();
            return;
        }

        filePath = defaultDirectoryPath + File.separator + "Project" + File.separator;
        int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Project selectedProject = projecttable.getItems().get(selectedIndex);
            String jsonFileName = selectedProject.getProjectName() + ".json";
            String jsonFilePath = filePath + jsonFileName;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Project");
            alert.setContentText("Are you sure you want to delete the project '" + selectedProject.getProjectName() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Delete the JSON file
                    Files.deleteIfExists(Paths.get(jsonFilePath));

                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to delete file");
                    errorAlert.setContentText("An error occurred while deleting the file: " + e.getMessage());
                    errorAlert.showAndWait();
                    return;
                }
                updatedTableProject(event);
            }
        }
    }

    @FXML
    public void ImportexamFile(ActionEvent event) throws IOException {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File selectedfile = dirChooser.showDialog(null);
        examimportfilepath = selectedfile.getAbsolutePath();
        importedexamFile = selectedfile.getName();
        studentfileimport.setText(importedexamFile);
    }

    @FXML
    public void createProject(ActionEvent event) throws IOException {
        if (projectOpened) { // edit situation
            int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String projectPath = defaultDirectoryPath + File.separator + "Project" + File.separator;
                String f2 = projectPath + projecttable.getItems().get(selectedIndex).getProjectName() + ".json";
                File file = new File(f2);
                file.delete();
                if (examimportfilepath == null) {
                    examimportfilepath = projecttable.getItems().get(selectedIndex).getExamsPath();
                }
                Project project = new Project(projectName.getText(), configCombo.getValue(), examimportfilepath);
                projectOpened = false;
                String newJson = gson.toJson(project);

                String newFilePath = projectName.getText() + ".json";
                File f3 = new File(projectPath, newFilePath);
                try (FileWriter fileWriter = new FileWriter(f3)) {
                    fileWriter.write(newJson);
                    System.out.println("JSON written to file successfully.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else { // create from the beginning
            File dir = new File(defaultDirectoryPath);

            dir.mkdir();
            File projectFile = new File(dir, "Project");
            projectFile.mkdir();
            String config = configCombo.getValue();
            String ExamsPath = examimportfilepath;
            sourcefileimport.setText(importedexamFile);
            String projectname = projectName.getText();
            Project project = new Project(projectname, config, ExamsPath);
            String newJson = gson.toJson(project);

            String newFilePath = projectname + ".json";
            File file = new File(projectFile, newFilePath);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(newJson);
                System.out.println("JSON written to file successfully.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clearProjectMenu();
        updatedTableProject(event);
    }

    @FXML
    public void updatedTableProject(ActionEvent event) throws IOException {
        String projectFilePath = defaultDirectoryPath + File.separator + "Project" + File.separator;
        File pFile = new File(projectFilePath);
        String[] files = pFile.list();
        // Check if there are any projects
        if (files == null || files.length == 0) {
            projecttable.getItems().clear(); // Clear table if no projects found
            return;
        }

        for (String f : files) {
            String path = projectFilePath + f;
            Project project1 = readJsonFile_Project(path);
            projectList.add(project1);
        }
        projecttable.getItems().clear();

        // Re-adding
        ObservableList<Project> obProjectList = FXCollections.observableArrayList(projectList);
        projecttable.setItems(obProjectList);
        projecttableconfig.setCellValueFactory(new PropertyValueFactory<>("config"));
        projecttablesource.setCellValueFactory(new PropertyValueFactory<>("examsPath"));
        projecttablename.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectList.clear();
    }

    public void clearProjectMenu() {
        configCombo.setValue("");
        studentfileimport.setText("Import");
        projectName.setText("");
    }

    @FXML
    public void projectClearButton(ActionEvent event) {
        clearProjectMenu();
        projectOpened = false;
    }

    @FXML
    public void openProject(ActionEvent event) throws IOException {
        if (projecttable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Projects Found");
            alert.setContentText("There are no projects available to edit.");
            alert.showAndWait();
            return;
        }

        int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Project project = projecttable.getItems().get(selectedIndex);
            configCombo.setValue(project.getConfig());
            if (project.getExamsPath() != null) {
                Path p1 = Paths.get(project.getExamsPath());
                studentfileimport.setText(p1.getFileName().toString());
            } else {
                studentfileimport.setText("");
            }
            projectName.setText(project.getProjectName());
            projectOpened = true;
        }
    }

    @FXML
    public void QuizManagement(ActionEvent event) throws IOException {
        int selectedIndex = submissionsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Submission Selected");
            alert.setContentText("Please select a submission to grade.");
            alert.showAndWait();
            return;
        }
        String pPath = defaultDirectoryPath + File.separator + "Project" + File.separator + configComboProject.getValue() + ".json";
        Project project2 = readJsonFile_Project(pPath);
        String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator + project2.getConfig() + ".json";
        Configuration config = readJsonFile_Configuration(configPath);
        assert config != null;
        String submissionPath= submissionFolderPath+File.separator+submissionFiles.get(selectedIndex);

        List<String> results = QuizGrader.gradeSubmissions(submissionPath, config.getFilePath());

        if (results.size() >= 4) {
            idLabel.setText(results.get(0));
            student.setText(results.get(1));
            expected.setText(results.get(2));
            scoreLabel.setText(results.get(3));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Grading Error");
            alert.setContentText("An error occurred while grading the submission.");
            alert.showAndWait();
        }
    }

    @FXML
    public void ImporStudentSubmissions(ActionEvent event) throws IOException {
        String pPath = defaultDirectoryPath + File.separator + "Project" + File.separator + configComboProject.getValue() + ".json";
        Project project12 = readJsonFile_Project(pPath);
        submissionFolderPath = project12.getExamsPath();
        File sub = new File(submissionFolderPath);
        String[] files = sub.list();
        for (String file : files) {
            submissionFiles.add(file);
        }
        ObservableList<String> obProjectList = FXCollections.observableArrayList(submissionFiles);
        submissionsTable.setItems(obProjectList);
    }


    @FXML
    public void runAll(ActionEvent event) throws IOException {
        int length = submissionsTable.getItems().size();
        File sub = new File(submissionFolderPath);
        File[] files = sub.listFiles();
        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            String pPath = defaultDirectoryPath + File.separator + "Project" + File.separator + configComboProject.getValue() + ".json";
            Project project12 = readJsonFile_Project(pPath);
            String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator + project12.getConfig() + ".json";
            Configuration config = readJsonFile_Configuration(configPath);
            List<String> results = QuizGrader.gradeSubmissions(files[i].getPath(), config.getFilePath());
            scores.add(Integer.valueOf(results.get(3)));
        }
        ObservableList<Integer> ob_score_list = FXCollections.observableArrayList(scores);
        scoreTable.setItems(ob_score_list);
    }

    @FXML
    private ListView<String> StudentTable;

    @FXML
    private Button addAnswer;
    @FXML
    private Button addSub;

    @FXML
    private Button run;

    @FXML
    private Button projectdelete;
    @FXML
    private Button projecthelp;

    @FXML
    private Button projectedit;
    @FXML
    private Button projectupdate;

    @FXML
    private TableView<Project> projecttable;

    @FXML
    private ListView<String> submissionsTable;
    @FXML
    private ListView<Integer> scoreTable;

    @FXML
    private TableColumn<Project, String> projecttableconfig;

    @FXML
    private TableColumn<Project, String> projecttablename;

    @FXML
    private TableColumn<Project, String> projecttablesource;

    private TableColumn<File, String> submissionsName;

    @FXML
    private TableColumn<Configuration, String> configtablelang;

    @FXML
    private TableColumn<Configuration, String> configtablename;

    @FXML
    private TableColumn<Configuration, String> configtablesource;

    @FXML
    private ComboBox<String> configCombo = new ComboBox<>();
    @FXML
    private ComboBox<String> configComboProject = new ComboBox<>();
    @FXML
    private Button configdelete;

    @FXML
    private Button configedit;

    @FXML
    private TextField configname;

    @FXML
    private TableView<Configuration> configtable;

    @FXML
    private Button configupdate;

    @FXML
    private Button createconfig;

    @FXML
    private Label expected;

    @FXML
    private Button exportconfig;

    @FXML
    private Label idLabel;

    @FXML
    private Button importconfig;

    @FXML
    private ComboBox<String> langcombo = new ComboBox<>();

    @FXML
    private Button openstudent;

    @FXML
    private ComboBox<String> projectCombo = new ComboBox<>();

    @FXML
    private TextField projectName;

    @FXML
    private Button saveproject;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button sourcefileimport;

    @FXML
    private Label student;
    @FXML
    private Button projectSelect;

    @FXML
    private Button studentfileimport;

    @FXML
    private TableView<?> table;

    private void loadExistingConfigurations() {
        try {
            updateTableView(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExistingProjects() {
        try {
            updatedTableProject(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}