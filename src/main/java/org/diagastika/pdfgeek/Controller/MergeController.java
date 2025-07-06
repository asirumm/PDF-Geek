package org.diagastika.pdfgeek.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.diagastika.pdfgeek.Constant;
import org.diagastika.pdfgeek.Helper.ScreenHelper;
import org.diagastika.pdfgeek.Services.PdfServices;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Stack;

public class MergeController extends BaseController {
    private PdfServices pdfServices;
    @FXML
    private Label mainPdfNameLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ListView<File> pdfWantToMergeListView = new ListView<>();

    private Stack<File> pdfFilesToMerge = new Stack<>();


    @FXML
    public void initialize(){
       super.initialize();
    }

    /**
     * Pengganti konstruktor yang di blok javafx
     */
    public void create(PdfServices pdfServices){
        this.pdfServices = pdfServices;

        mainPdfNameLabel.setText(pdfServices.getPdfName());
    }

    @FXML
    public void addFileButtonOnClick(){
        logger.debug("user klik add file button");

        if (pdfFilesToMerge.size()>5){
            feedbackLabel.setText("maaf tidak dapat merge lebih dari 5 file");
            return;
        }

        Optional<File> fileChooser = Constant.pdfFileChooser("pilih file",getStage(),false);

        fileChooser.ifPresent((file)->{
            logger.info("Menambah file untuk di-merge: {}", file.getName());

            pdfFilesToMerge.add(file);

            showListOfFilesWantMergeToUser();
        });

    }

    private void showListOfFilesWantMergeToUser(){
        pdfWantToMergeListView.setItems(FXCollections.observableArrayList(
                pdfFilesToMerge
        ));
    }

    @FXML
    private void deleteFileButtonOnAction(){
        logger.debug("user klik delete file from list button");

       File userSelectedFile =  pdfWantToMergeListView.getSelectionModel().getSelectedItem();

       if(pdfFilesToMerge.contains(userSelectedFile)){

           logger.info("Hapus file yang dipilih dalam list {}", userSelectedFile.getName());

            pdfFilesToMerge.remove(userSelectedFile);
            showListOfFilesWantMergeToUser();
        }
    }

    @FXML
    private void deleteAllListFileButtonOnClick(){
        logger.info("Menghapus semua daftar yang ada");

        pdfFilesToMerge.clear();
        showListOfFilesWantMergeToUser();

        feedbackLabel.setText("berhasil hapus semua daftar");
    }

    @FXML
    private void mergeOnAction(){
       Optional<File> fileChooser = Constant.pdfFileChooser("simpan file",getStage(),true);

       fileChooser.ifPresent((file)->{

           // Pastikan ekstensi .pdf ditambahkan jika user tidak mengetik manual
           if (!file.getName().toLowerCase().endsWith(".pdf")) {
               file = new File(file.getAbsolutePath() + ".pdf");
           }

           try {
               pdfServices.mergeFilesToMainPdf(pdfFilesToMerge,file);

               StringBuilder text = new StringBuilder();
               text.append("file ");
               text.append(file.getName());
               text.append(" berhasil disimpan");

               feedbackLabel.setText(text.toString());

               logger.info("sukses menggabungkan dokumen nama file baru {}",file.getName());

           } catch (IOException e) {

               logger.error("gagal simpan file, hasil operasi merge files, user pdf file {}",pdfServices.getPdfName(),e);

               Platform.exit();
           }
       });

    }

    @FXML
    private void backButtonOnClick(){
        logger.info("back to home");

        ScreenHelper.switchScreen(getStage(), Constant.HOME_FXML);
    }
}
