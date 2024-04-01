module com.example.clienttcp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires json.simple;

    opens com.example.clienttcp to javafx.fxml;
    exports com.example.clienttcp;
}