package org.diagastika.pdfgeek.Helper;

import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.diagastika.pdfgeek.Constant;
import org.diagastika.pdfgeek.HelloApplication;


import java.io.IOException;

public class ScreenHelper {
    public  static Stage getStage(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        return stage;
    }

    public static void switchScreen(Stage stage, String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));


            Scene newScene = new Scene(loader.load(),Constant.WIDTH_SCREEN,Constant.HEIGHT_SCREEN);


            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.show();
            newStage.setTitle("PDF-Geek");


            newStage.setResizable(false);


            stage.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FXMLLoader getLoader(String fxml){
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));

        return loader;
    }

    /**
     * Membuat stage instance untuk switch screen
     * - resizeable = false
     */
    public static Stage getStageConfig(Scene scene){
        Stage stage = new Stage();

        stage.setWidth(Constant.WIDTH_SCREEN);
        stage.setHeight(Constant.HEIGHT_SCREEN);

        stage.setScene(scene);
        stage.setTitle("PDF-Geek");
        stage.setResizable(false);

        return stage;
    }
}
