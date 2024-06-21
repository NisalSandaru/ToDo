module project1.project1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens project1.project1 to javafx.fxml;
    exports project1.project1;
    exports controller;
    opens controller to javafx.fxml;
}