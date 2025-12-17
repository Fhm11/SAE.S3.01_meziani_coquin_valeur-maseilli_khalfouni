module org.example.sae {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;

    opens org.example.sae to javafx.fxml;
    exports org.example.sae;

    opens source to javafx.fxml;
    exports source;
}