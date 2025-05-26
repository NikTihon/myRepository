module pet.project.realtyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires commons.math3;

    opens pet.project1.realtyapp to javafx.fxml;
    exports pet.project1.realtyapp;
    exports pet.project1.realtyapp.entity;
    opens pet.project1.realtyapp.entity to javafx.fxml;
}