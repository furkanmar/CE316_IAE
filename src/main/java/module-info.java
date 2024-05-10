module com.example.ce316_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.text;
    requires com.google.gson;
    requires java.desktop;


    opens com.example.ce316_project to javafx.fxml;
    exports com.example.ce316_project;
}