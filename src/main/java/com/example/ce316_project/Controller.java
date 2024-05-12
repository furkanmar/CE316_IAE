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
    private FileChooser FileChooser = new FileChooser();
    private String expectedOutputPath;
    ArrayList<File> submissionFiles = new ArrayList<>();
    boolean projectOpened=false;
    boolean configOpened=false;
    String importConfigPath;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langcombo.setOnMouseClicked(e -> langcombo());
        configCombo.setOnMouseClicked(e -> configcombo());
        configComboProject.setOnMouseClicked(e -> configComboProject());
        projectCombo.setOnMouseClicked(e ->projectCombo());
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
    public void projectCombo(){
        String projectFilePath = defaultDirectoryPath + File.separator + "Project" + File.separator;
        File pFile = new File(projectFilePath);
        String[] files = pFile.list();
        ArrayList<String> nameOfFile = new ArrayList<>();
        for (String i : files) {
            String name = i.split("\\.")[0];
            nameOfFile.add(name);
        }

        ObservableList<String> oblist = FXCollections.observableArrayList(nameOfFile);
        projectCombo.setItems(oblist);
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
        sourcefileimport.setText(importedSourceFile);
    }



    @FXML // arayüzdeki createConfigiration butonu ile aktifleşecek
    public void createConfig(ActionEvent event) throws IOException {
        if (configOpened){
            int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
                String f2 = configPath + configtable.getItems().get(selectedIndex).getConfigName()+".json";
                File file = new File(f2);
                file.delete();
                if (configimportfilepath==null) {
                    configimportfilepath=configtable.getItems().get(selectedIndex).getFilePath();
                }
                Configuration config=new Configuration(langcombo.getValue(),configimportfilepath,configname.getText());
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

        }
        else {
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

    @FXML
    public void updateTableView(ActionEvent event) throws IOException {
        String configFilePath = defaultDirectoryPath + File.separator + "Configuration" + File.separator;
        File cFile = new File(configFilePath);
        String[] files = cFile.list();
        //is there a another config check
        if (files == null || files.length == 0) {
            //no config
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Configurations Found");
            alert.setContentText("There are no configurations available. Please create one first.");
            alert.showAndWait();
            return;
        }

        for (String c : files) {
            String path = configFilePath + c;
            Configuration config1 = readJsonFile_Configuration(path);
            configList.add(config1);
        }
        configtable.getItems().clear();

        //re-adding
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
    public void openConfig(ActionEvent event) throws IOException {  // also edit can do from there

        int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {

            Configuration config = configtable.getItems().get(selectedIndex);
            langcombo.setValue(config.getLanguage());
            if (config.getFilePath()!=null){
                Path p1= Paths.get(config.getFilePath());
                sourcefileimport.setText(p1.getFileName().toString());
            }
            else sourcefileimport.setText("");
            configname.setText(config.getConfigName());
            configOpened=true;
        }
    }
    @FXML
    public void configClearButton(ActionEvent event){
        clearConfigMenu();
        configOpened=false;

    }
    @FXML
    public void importConfig(ActionEvent event) throws IOException {
        configOpened=false;
        FileChooser fileChooser=new FileChooser();
        File selectedFile=fileChooser.showOpenDialog(null);
        importConfigPath=selectedFile.getPath();
        Configuration config=readJsonFile_Configuration(importConfigPath);
        configname.setText(config.getConfigName());
        langcombo.setValue(config.getLanguage());
        sourcefileimport.setText(config.getFilePath());
        createConfig(event);


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
        FileChooser fileChooser = new FileChooser();
        File selectedfile = fileChooser.showOpenDialog(null);
        examimportfilepath = selectedfile.getAbsolutePath();
        importedexamFile = selectedfile.getName();
        studentfileimport.setText(importedexamFile);
    }

    @FXML
    public void createProject(ActionEvent event) throws IOException {
        if(projectOpened==true){//edit situation
            int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String projectPath= defaultDirectoryPath + File.separator + "Project" + File.separator;
                String f2 = projectPath + projecttable.getItems().get(selectedIndex).getProjectName()+".json";
                File file = new File(f2);
                file.delete();
                if (examimportfilepath==null) {
                    examimportfilepath=projecttable.getItems().get(selectedIndex).getExamsPath();
                }
                Project project=new Project(projectName.getText(),configCombo.getValue(),examimportfilepath);
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
        }
        else {//create from the beginning
            File dir = new File(defaultDirectoryPath);
            System.out.println(defaultDirectoryPath);
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
        if (files == null || files.length == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Projects Found");
            alert.setContentText("There are no project available. Please create one first.");
            alert.showAndWait();
            return;
        }
        for (String f : files) {
            String path = projectFilePath + f;
            Project project1 = readJsonFile_Project(path);
            projectList.add(project1);
        }
        projecttable.getItems().clear();

        //re-adding
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
    public void projectClearButton(ActionEvent event){
        clearProjectMenu();
        projectOpened=false;
    }


    @FXML
    public void openProject(ActionEvent event) throws IOException {

        int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Project project = projecttable.getItems().get(selectedIndex);
            configCombo.setValue(project.getConfig());
            if (project.getExamsPath()!=null){
                Path p1= Paths.get(project.getExamsPath());
                studentfileimport.setText(p1.getFileName().toString());
            }
            else studentfileimport.setText("");
            projectName.setText(project.getProjectName());
            projectOpened=true;
        }
    }

    @FXML
    public void QuizManagement(ActionEvent event) throws IOException{
        int selectedIndex = submissionsTable.getSelectionModel().getSelectedIndex();
        String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator + configComboProject.getValue() + ".json";
        Configuration config = readJsonFile_Configuration(configPath);
        assert config != null;
        List<String> results = QuizGrader.gradeSubmissions(submissionFiles.get(selectedIndex).getPath(), config.getFilePath());
        expected.setText(results.get(2));
        student.setText(results.get(1));
        idLabel.setText(results.get(0));
        scoreLabel.setText(results.get(3));
    }

    @FXML
    public void ImporStudentSubmissions(ActionEvent event) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            submissionFolderPath = selectedDirectory.getAbsolutePath();
            String importedSubmissionFile = selectedDirectory.getName();
            updatedSubmissionsTable(event);
        }
    }

    @FXML
    public void updatedSubmissionsTable(ActionEvent event) throws IOException {
        File pFile = new File(submissionFolderPath);
        submissionFiles = new ArrayList<>(Arrays.asList(pFile.listFiles()));
        List<String> fileNames = submissionFiles.stream()
                .map(File::getName)
                .collect(Collectors.toList());
        ObservableList<String> obProjectList = FXCollections.observableArrayList(fileNames);
        submissionsTable.setItems(obProjectList);
    }
    @FXML
    public void runAll(ActionEvent event) throws IOException {
        int length=submissionsTable.getItems().size();
        ArrayList<Integer> scores=new ArrayList<>();
        for (int i=0;i<length;i++){
            String configPath = defaultDirectoryPath + File.separator + "Configuration" + File.separator + configComboProject.getValue() + ".json";
            Configuration config = readJsonFile_Configuration(configPath);
            assert config != null;
            List<String> results = QuizGrader.gradeSubmissions(submissionFiles.get(i).getPath(), config.getFilePath());
            scores.add(Integer.valueOf(results.get(3)));
        }
        ObservableList<Integer> ob_score_list=FXCollections.observableArrayList(scores);
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
    private ComboBox<String> configCombo=new ComboBox<>();
    @FXML
    private ComboBox<String> configComboProject =new ComboBox<>();
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
    private ComboBox<String> langcombo=new ComboBox<>();

    @FXML
    private Button openstudent;

    @FXML
    private ComboBox<String> projectCombo=new ComboBox<>();

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
    }





