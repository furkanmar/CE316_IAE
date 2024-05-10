package com.example.ce316_project;

public class Project {
    private String projectName;
    private Configuration config;
    private String examsPath;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public String getExamsPath() {
        return examsPath;
    }

    public void setExamsPath(String examsPath) {
        this.examsPath = examsPath;
    }

    public Project(String projectName, Configuration config, String examsPath) {
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
