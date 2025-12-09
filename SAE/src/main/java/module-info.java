module org.example.sae {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.example.sae to javafx.fxml;
    exports org.example.sae;
}