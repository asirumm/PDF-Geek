package org.diagastika.pdfgeek.Helper;

import javafx.application.Platform;
import org.diagastika.pdfgeek.Constant;

import java.io.IOException;
import java.nio.file.Files;

public class ApplicationConfig {
    public void createFolderForApp() {
        if (!Files.exists(Constant.getFolderDir())) {

            try {
                Files.createDirectories(Constant.getFolderDir());

            } catch (IOException e) {

                Platform.exit();
            }
        }
    }
}
