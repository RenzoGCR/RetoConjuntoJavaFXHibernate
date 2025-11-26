package org.example.retoconjuntojavafxhibernate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.retoconjuntojavafxhibernate.utils.JavaFXUtil;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        JavaFXUtil.initStage(stage);
        JavaFXUtil.setScene("/org/example/retoconjuntojavafxhibernate/login-view.fxml");
    }
}
