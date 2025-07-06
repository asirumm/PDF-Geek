package org.diagastika.pdfgeek;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.diagastika.pdfgeek.Helper.ApplicationConfig;

import java.io.IOException;
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ApplicationConfig config = new ApplicationConfig();
        config.createFolderForApp();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(Constant.HOME_FXML));
        Scene scene = new Scene(fxmlLoader.load(), Constant.WIDTH_SCREEN, Constant.HEIGHT_SCREEN);
        stage.setTitle("PDF-Geek");

        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();

        // for gc
        config = null;
    }

    public static void main(String[] args) {
        launch();
    }
}