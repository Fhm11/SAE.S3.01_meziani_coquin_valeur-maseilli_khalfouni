module org.example.sae {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens source to javafx.fxml;
    exports source;
}