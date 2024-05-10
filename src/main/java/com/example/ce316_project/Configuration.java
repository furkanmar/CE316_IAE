package com.example.ce316_project;

import java.util.Arrays;

public class Configuration {

    //variables
    private String language;  //c,java etc
    private String filePath;  // projenin yolu
    private String[] commands ; //java için javac , c nin css gibi onların compiler ı
    private String configName;


    //setter-getter
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }



    public void setConfigName(String configName) {
        this.configName = configName;
    }


    public String getConfigName() {
        return configName;
    }
    //constructor


    public Configuration(String language, String filePath, String[] commands, String configName) {
        this.language = language;
        this.filePath = filePath;
        this.commands = commands;
        this.configName = configName;
    }


    @Override
    public String toString() {
        return "Configiration{" +
                "language='" + language + '\'' +
                ", filePath='" + filePath + '\'' +
                ", commands=" + Arrays.toString(commands) +
                ", configName='" + configName + '\'' +
                '}';
    }
}
