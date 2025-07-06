package org.diagastika.pdfgeek.Helper;

import com.sun.tools.jconsole.JConsoleContext;
import javafx.application.Platform;
import org.diagastika.pdfgeek.Constant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
