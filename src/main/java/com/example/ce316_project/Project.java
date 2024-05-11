package com.example.ce316_project;

public class Project {
    public String projectName;
    public String config;
    public String examsPath;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getExamsPath() {
        return examsPath;
    }

    public void setExamsPath(String examsPath) {
        this.examsPath = examsPath;
    }

    public Project(String projectName, String config, String examsPath) {
        this.projectName = projectName;
        this.config = config;
        this.examsPath = examsPath;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectName='" + projectName + '\'' +
                ", config=" + config +
                ", examsPath='" + examsPath + '\'' +
                '}';
    }
}
