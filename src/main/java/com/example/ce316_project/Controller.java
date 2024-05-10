package com.example.ce316_project;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    String configimportfilepath;
    String importedSourceFile;
    String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.separator+"IAE";
    String filePath;
    ArrayList<Configuration> configList=new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langcombo.setOnMouseClicked(e -> langcombo());
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



    private static Configuration readJsonFile(String filePath) throws IOException {
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
    public void ImportFile(ActionEvent event) throws IOException{
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
        configList.add(config);
        String newJson = gson.toJson(config);
        String newFilePath=configName+".json";
        File file = new File(configFile,newFilePath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(newJson);
            System.out.println("JSON written to file successfully.");
            } catch (IOException e) {
                throw new RuntimeException(e);
        }
        ObservableList<Configuration> configurationList=FXCollections.observableArrayList(configList);
        configtable.setItems(configurationList);
        configtablename.setCellValueFactory(new PropertyValueFactory<>("configName"));
        configtablesource.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        configtablelang.setCellValueFactory(new PropertyValueFactory<>("language"));
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

    @FXML
    private TableColumn<Configuration, String> configtablelang;

    @FXML
    private TableColumn<Configuration, String> configtablename;

    @FXML
    private TableColumn<Configuration, String> configtablesource;

    @FXML
    private ComboBox<?> configCombo;

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





