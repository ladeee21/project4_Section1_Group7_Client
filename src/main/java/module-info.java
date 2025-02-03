module com.example.group7fileflix {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.group7fileflix to javafx.fxml;
    exports com.example.group7fileflix;
}