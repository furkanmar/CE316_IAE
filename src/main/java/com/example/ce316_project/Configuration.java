package com.example.ce316_project;

import java.util.Arrays;

public class Configuration {

    //variables
    public String language;  //c,java etc
    public String filePath;  // projenin yolu
    public String configName;


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


    public void setConfigName(String configName) {
        this.configName = configName;
    }


    public String getConfigName() {
        return configName;
    }
    //constructor


    public Configuration(String language, String filePath,  String configName) {
        this.language = language;
        this.filePath = filePath;
        this.configName = configName;
    }


    @Override
    public String toString() {
        return "Configiration{" +
                "language='" + language + '\'' +
                ", filePath='" + filePath + '\'' +

                ", configName='" + configName + '\'' +
                '}';
    }
}
