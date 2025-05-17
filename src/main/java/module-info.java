module pet.project1.realtyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;

    opens pet.project1.realtyapp to javafx.fxml;
    exports pet.project1.realtyapp;
}