package org.diagastika.pdfgeek.Controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.diagastika.pdfgeek.Constant;
import org.diagastika.pdfgeek.Helper.ScreenHelper;
import org.diagastika.pdfgeek.Services.PdfServices;


import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.diagastika.pdfgeek.Constant.*;


public class HomeController extends BaseController {

    @FXML
    private TextField inputTextFilePath;

    private PdfServices pdfServices;

    @FXML
    protected Label feedbackLabel;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @FXML
    private void logButtonOnClick(){

        logger.debug("user klik log button");

        try {

            File fileLog = new File(Constant.getFolderDir().toString()+"/"+Constant.LOG_FILE);

            logger.debug("file log disimpan di - {}",fileLog);

            if (fileLog.exists()) {

                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;

                if (os.contains("win")) {
                    // Open file explorer when windows
                    pb = new ProcessBuilder("explorer.exe", "/select,", fileLog.getAbsolutePath());

                } else if (os.contains("nix") || os.contains("nux")) {
                    // linux
                    pb = new ProcessBuilder("xdg-open", fileLog.getParent());
                } else {
                    throw new UnsupportedOperationException("system operation unreconigzed");
                }

                pb.start();

            } else {
                logger.info("folder aplikasi pada komputer user tidak ditemukan");
            }

        } catch (IOException e) {
            logger.error("error membuka file log",e);
            Platform.exit();
        }
    }

    @FXML
    private void aboutButtonOnClick(){
        logger.debug("user button about");

        ScreenHelper.switchScreen(getStage(), ABOUT_FXML);
    }

    @FXML
    private void quitButtonOnClick(){
        logger.debug("user klik quit button");

        Platform.exit();
    }

    @FXML
    private void browseButtonOnClick(){
        logger.debug("user klik browse button");

        Optional<File> selectedFile = Constant.pdfFileChooser("pilih file",getStage(),false);


        selectedFile.ifPresent((file)->{
            logger.debug("user telah memilih file pdf utama {}",file.getName());

            pdfServices = new PdfServices(file);

            // set to field for showing user
            inputTextFilePath.setText(file.getName());
        });
    }


    @FXML
    private void deleteOperationButtonOnClick() {
        logger.debug("user klik delete operation button");

        if(isUserAlreadyChooseMainPdf()){
            try {
                // Kode dibawah ini dilakukan untuk passing instance PDF service

                // Membuat instance FXMLLoader, untuk memuat file FXML halaman delete
                FXMLLoader deleteOperationLoader = ScreenHelper.getLoader(DELETE_SCENE_FXML);

                // Melakukan load file FXML, hasilnya berupa root node (Parent)
                Parent root = deleteOperationLoader.load(); // Penting! load() hanya dipanggil sekali

                // Mengambil instance controller dari file FXML yang sudah diload
                DeleteController deleteController = deleteOperationLoader.getController();

                // initial
                deleteController.create(pdfServices);

                // Membuat Scene baru dengan root node yang sudah diload
                Scene deleteScene = new Scene(root);

                // Membuat stage baru (window baru) untuk menampilkan scene
                Stage stage = ScreenHelper.getStageConfig(deleteScene);

                stage.show();

                // Menutup window saat ini
                getStage().close();

            } catch (IOException e) {

                logger.error("Error loading delete operation scene ", e);
                Platform.exit();
            }
        }
    }

    @FXML
    private void insertOperationButtonOnClick(){
    }

    @FXML
    private void mergeOperationButtonOnClick(){
        logger.debug("user klik merge operation button");

        if(isUserAlreadyChooseMainPdf()){

            try {
                // Kode dibawah ini untuk passing instance ke controller lain

                // Membuat instance FXMLLoader, untuk memuat file FXML halaman merge
                FXMLLoader mergeOperationLoader = ScreenHelper.getLoader(MERGE_SCENE_FXML);

                // Melakukan load file FXML, hasilnya berupa root node (Parent)
                Parent root = mergeOperationLoader.load(); // Penting! load() hanya dipanggil sekali

                // Mengambil instance controller dari file FXML yang sudah diload
                MergeController mergeController = mergeOperationLoader.getController();

                mergeController.create(pdfServices);

                // Membuat Scene baru dengan root node yang sudah diload
                Scene mergeScene = new Scene(root);

                // Membuat stage baru (window baru) untuk menampilkan scene
                Stage stage = ScreenHelper.getStageConfig(mergeScene);

                stage.show();

                // Menutup window saat ini
                getStage().close();

            } catch (IOException e) {

                logger.error("Error loading merge operation scene", e);
                Platform.exit();
            }
        }
    }


    private boolean isUserAlreadyChooseMainPdf(){
        if(pdfServices==null){

            feedbackLabel.setText("Belum ada file pdf yang dipilih, tolong pilih terlebih dahulu sebelum melakukan operasi");

            return false;
        }else {
            return true;
        }

    }
}
