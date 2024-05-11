package com.example.ce316_project;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    Gson gson = new Gson();
    String configimportfilepath;
    String importedSourceFile;
    String examimportfilepath;
    String importedexamFile;
    String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.separator+"IAE";
    String filePath;
    ArrayList<Configuration> configList=new ArrayList<>();
    ArrayList<Project> projectList=new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langcombo.setOnMouseClicked(e -> langcombo());
        configCombo.setOnMouseClicked(e -> configcombo());
    }
    @FXML
    public void langcombo(){
        ArrayList<String> languageList=new ArrayList<>();
        languageList.add("Java");
        languageList.add("C++");
        languageList.add("Python");
        languageList.add("C");
        ObservableList<String> oblist= FXCollections.observableArrayList(languageList);
        langcombo.setItems(oblist);
    }
    @FXML
    public void configcombo(){
        ArrayList<String> confList=new ArrayList<>();
        for (Configuration a:configList){
            confList.add(a.getConfigName());
        }
        ObservableList<String > oblist= FXCollections.observableArrayList(confList);
        configCombo.setItems(oblist);
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
    public void deleteConfig(ActionEvent event) throws IOException{
        filePath = defaultDirectoryPath+File.separator+"Configuration"+File.separator;
        int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Configuration selectedConfig = configtable.getItems().get(selectedIndex);
            String jsonFileName = selectedConfig.getConfigName() + ".json";
            String jsonFilePath = filePath +  jsonFileName;

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
    public void ImportsourceFile(ActionEvent event) throws IOException{
        FileChooser fileChooser = new FileChooser();
        File selectedfile = fileChooser.showOpenDialog(null);
        configimportfilepath = selectedfile.getAbsolutePath();
        importedSourceFile= selectedfile.getName();
        sourcefileimport.setText(importedSourceFile);
    }
    @FXML // arayüzdeki createConfigiration butonu ile aktifleşecek
    public void createConfig(ActionEvent event) {
        File dir=new File(defaultDirectoryPath);
        dir.mkdir();
        File configFile=new File(dir,"Configuration");
        configFile.mkdir();
        Gson gson = new Gson();


        String language =langcombo.getValue();
        String fPath = configimportfilepath;
        sourcefileimport.setText(importedSourceFile);
        String configName = configname.getText();

        Configuration config = new Configuration(language,fPath,configName);

        String newJson = gson.toJson(config);
        String newFilePath=configName+".json";
        File file = new File(configFile,newFilePath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(newJson);
            System.out.println("JSON written to file successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            updateTableView(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearConfigMenu();


        /*String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        String quizzesPath = defaultDirectoryPath + File.separator + "Quizzes";
        File quizzesDirectory = new File(quizzesPath);
        if (!quizzesDirectory.exists()) {
        quizzesDirectory.mkdirs();
        }

        String studentCodePath = quizzesPath + File.separator + info.getCourseCode() + File.separator + info.getSchoolId();
        File studentDirectory = new File(studentCodePath);
        if (!studentDirectory.exists()) {
        studentDirectory.mkdirs();
        }

        String filePath = studentCodePath + File.separator + configName + ".json";
        File file = new File(filePath);

        try (FileWriter fileWriter = new FileWriter(file)) {
        fileWriter.write(newJson);
        System.out.println("JSON written to file successfully.");
        } catch (IOException e) {
        throw new RuntimeException(e);*/
    }
    @FXML
    public void updateTableView(ActionEvent event) throws IOException {
        String configFilePath=defaultDirectoryPath+File.separator+"Configuration"+File.separator;
        File cFile=new File(configFilePath);
        String [] files=cFile.list();
        for (String c:files){
            String path=configFilePath+c;
            Configuration config1=readJsonFile_Configuration(path);
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
    public void clearConfigMenu(){
        langcombo.setValue("");
        sourcefileimport.setText("Import");
        configname.setText("");

    }
    @FXML
    public void openConfig(ActionEvent event) throws IOException {  // also edit can do from there

        int selectedIndex = configtable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            filePath = defaultDirectoryPath+File.separator+"Configuration"+File.separator + configtable.getItems().get(selectedIndex).getConfigName();

            Configuration config = configtable.getItems().get(selectedIndex);
            langcombo.setValue(config.getLanguage());
            String name = config.getConfigName().split("Configuration/")[0];
            sourcefileimport.setText(name);
            configname.setText(config.getConfigName());
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
    public void deleteProject(ActionEvent event) throws IOException{
        filePath = defaultDirectoryPath+File.separator+"Project"+File.separator;
        int selectedIndex = projecttable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Project selectedProject = projecttable.getItems().get(selectedIndex);
            String jsonFileName = selectedProject.getProjectName() + ".json";
            String jsonFilePath = filePath +  jsonFileName;

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
    public void ImportexamFile(ActionEvent event) throws IOException{
        FileChooser fileChooser = new FileChooser();
        File selectedfile = fileChooser.showOpenDialog(null);
        examimportfilepath = selectedfile.getAbsolutePath();
        importedexamFile= selectedfile.getName();
        studentfileimport.setText(importedexamFile);
    }

    @FXML
    public void createProject(ActionEvent event) throws IOException {
        File dir=new File(defaultDirectoryPath);
        dir.mkdir();
        File projectFile=new File(dir,"Project");
        projectFile.mkdir();
        String config =configCombo.getValue();
        String ExamsPath = examimportfilepath;
        sourcefileimport.setText(importedexamFile);
        String projectname = projectName.getText();
        Project project = new Project(projectname,config,ExamsPath);
        String newJson = gson.toJson(project);

        String newFilePath=projectname+".json";
        File file = new File(projectFile,newFilePath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(newJson);
            System.out.println("JSON written to file successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearProjectMenu();
        updatedTableProject(event);


    }
    @FXML
    public void updatedTableProject(ActionEvent event) throws IOException {
        String projectFilePath=defaultDirectoryPath+File.separator+"Project"+File.separator;
        File pFile=new File(projectFilePath);
        String [] files=pFile.list();
        for (String f:files){
            String path=projectFilePath+f;
            Project project1=readJsonFile_Project(path);
            projectList.add(project1);
        }
        projecttable.getItems().clear();

        //re-adding
        ObservableList<Project> obProjectList=FXCollections.observableArrayList(projectList);
        projecttable.setItems(obProjectList);
        projecttableconfig.setCellValueFactory(new PropertyValueFactory<>("config"));
        projecttablesource.setCellValueFactory(new PropertyValueFactory<>("examsPath"));
        projecttablename.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectList.clear();
    }
    public void clearProjectMenu(){
        configCombo.setValue("");
        studentfileimport.setText("Import");
        projectName.setText("");

    }

    @FXML
    private Button projectdelete;

    @FXML
    private Button projectedit;
    @FXML
    private Button projectupdate;


    @FXML
    private TableView<Project> projecttable;

    @FXML
    private TableColumn<Project, String> projecttableconfig;

    @FXML
    private TableColumn<Project, String> projecttablename;

    @FXML
    private TableColumn<Project, String> projecttablesource;


    @FXML
    private TableColumn<Configuration, String> configtablelang;

    @FXML
    private TableColumn<Configuration, String> configtablename;

    @FXML
    private TableColumn<Configuration, String> configtablesource;

    @FXML
    private ComboBox<String> configCombo=new ComboBox<>();

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
    private ComboBox<?> projectCombo;

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
    private TableColumn<?, ?> studentTable;

    @FXML
    private Button studentfileimport;

    @FXML
    private TableView<?> table;
    }





