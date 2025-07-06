package org.diagastika.pdfgeek;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Constant {
    public static final String ABOUT_FXML = "About.fxml";
    public static final String HOME_FXML = "Main.fxml";
    public static final String DELETE_SCENE_FXML = "DeleteOperationScreen.fxml";
    public static final String MERGE_SCENE_FXML = "MergeOperationScreen.fxml";
    public static final String INSERT_SCENE_FXML = "InsertOperationScreen.fxml";
    public static final String LOG_FILE = "application.log";

    public static String MESSAGE_OPERATION_SUCCESS;

    public static final int WIDTH_SCREEN  = 900;
    public static final int HEIGHT_SCREEN = 720;

    public static Path getFolderDir(){
        String home = System.getProperty("user.home");
        Path folderDir = Paths.get(home, "PDF-Geek");

        return folderDir;
    }


    /**
     * Memilih file pdf dengan menerapkan filter pdf
     */
    public static Optional<File> pdfFileChooser(String title, Stage stage, boolean isSave) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("File PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile;
        if (isSave) {
            selectedFile = fileChooser.showSaveDialog(stage);
        } else {
            selectedFile = fileChooser.showOpenDialog(stage);
        }

        return Optional.ofNullable(selectedFile);
    }
}
