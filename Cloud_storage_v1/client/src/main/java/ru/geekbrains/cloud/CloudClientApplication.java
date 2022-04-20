package ru.geekbrains.cloud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class CloudClientApplication extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layout.fxml")));
        stage.setScene(new Scene(parent));
        stage.setTitle("Cloud-storage v1.0");
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }

}
