package com.example.ce316_project;

public class Controller {

   /* private static Configiration readJsonFile(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            return new Gson().fromJson(fileReader, Configiration.class);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Creating a new object.");
            return new Configiration();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML // arayüzdeki createConfigiration butonu ile aktifleşecek
    public void createConfig(Info info) {
    Gson gson = new Gson();

    String language = languageid.getText();
    String fPath = filePathid.getText();
    String[] commands = commandsid.getText();
    String projectName = projectNameid.getText();
    String output = outputid.getText();
    String configName = configNameid.getText();

    Configuration config = new Configuration(language, fPath, commands, projectName, output, configName);

    String newJson = gson.toJson(config);


    String defaultDirectoryPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
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
        throw new RuntimeException(e);
    }
}

    @FXML  //edit configiration sahnesi açıldıktan sonra edit butonu ile aktifleşecek
    public void openConfig(String filePath){  // also edit can do from there

        //sahne geçişi buraya yazılacak
        // açılır bir liste içerisinde tüm configirasyonlar listelenecek ardından seçildikten sonra


        Configiration config=readJsonFile(filePath);
        languageid.setText(config.getLanguage()); //text fieldden veri gelecek id sine göre
        filePathid.setText(config.getFilePath()); //text fieldden veri gelecek id sine göre
        commandsid.setText(config.getCommands()); //text fieldden veri gelecek id sine göre
        projectNameid.setText(config.getProjectName()); //text fieldden veri gelecek id sine göre
        outputid.setText(config.getOutput()); //text fieldden veri gelecek id sine göre


        //show scene olacak

    }

    @FXML
    public void editButton(ActionEvent event) throws IOException {
        File file=new File(filePath);
        file.delete();  // daha sonra yazılacak
        createConfig();
    }

    String filePath;
*/
}
