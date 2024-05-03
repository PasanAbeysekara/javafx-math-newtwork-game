module org.example.networkmathgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.networkmathgame to javafx.fxml;
    exports org.example.networkmathgame;
}