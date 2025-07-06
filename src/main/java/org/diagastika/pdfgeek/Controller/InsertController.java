package org.diagastika.pdfgeek.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.diagastika.pdfgeek.Constant;
import org.diagastika.pdfgeek.Data.InsertData;
import org.diagastika.pdfgeek.Helper.ScreenHelper;
import org.diagastika.pdfgeek.Services.PdfServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class InsertController extends BaseController{
    private int currentPage=0;
    private int totalPage;

    @FXML
    private Label pageInformationLabel;

    @FXML
    private Label pdfNameLabel;

    @FXML
    private ImageView pdfViewImage;

    @FXML
    private TextField inputFindPageTextField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private ListView<String> filesInsertListView = new ListView<>();

    private Stack<InsertData> listOfFileWantToInsert = new Stack<>();

    private short counter=0;


    @FXML
    public void initialize(){
        super.initialize();

        // memastikan user hanya input pencarian halaman dengan angka
        inputFindPageTextField
                .textProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {
                        inputFindPageTextField.setText(newValue.replaceAll("[^\\d]", ""));

                        feedbackLabel.setText("pencarian halaman hanya menerima input angka");
                    }
                });
    }

    /**
     * Pengganti konstruktor yang di blok javafx
     */
    public void create(PdfServices pdfServices){
        this.pdfServices = pdfServices;

        totalPage = pdfServices.getTotalPages();

        showPdfPageInformation(0);

        renderPdfToImageView();

        pdfNameLabel.setText(pdfServices.getPdfName());

        showFilesWantToInsert();
    }

    /**
     * menampilkan informasi halaman
     */
    private void showPdfPageInformation(int pageIndex)  {
        if (pageIndex < 0 || pageIndex >= totalPage) {
            return; // Tidak valid, keluar
        }

        StringBuilder text = new StringBuilder();
        text.append("Halaman ke ");
        text.append(pageIndex+1);
        text.append(" dari ");
        text.append(totalPage);

        pageInformationLabel.setText(text.toString());
    }

    private void renderPdfToImageView(){
        logger.debug("memulai render pdf di image view");

        try {
            pdfViewImage.setImage(pdfServices.getPdfImage(currentPage));

            pdfViewImage.setPreserveRatio(true);

        } catch (IOException e) {
            logger.error("error saat preview pdf ke user",e);
            Platform.exit();
        }
    }

    @FXML
    public void previousPageButtonOnClick(){
        logger.debug("user klik previous button");


        if (currentPage > 0) {
            currentPage--;

            logger.debug("berpindah ke halaman sebelumnya. Halaman saat ini: {}", currentPage);

            showPdfPageInformation(currentPage);
            renderPdfToImageView();
        }
    }

    @FXML
    public void nextPageButtonOnClick(){
        logger.debug("user klik next button");

        if (currentPage < totalPage - 1) {
            currentPage++;

            logger.debug("berpindah ke halaman selanjutnya. Halaman saat ini: {}", currentPage);

            showPdfPageInformation(currentPage);
            renderPdfToImageView();
        }
    }


    @FXML
    public void findPageButtonOnClick(){
        logger.debug("user klik tombol pencarian");

        // jika text field input cari halaman tidak kosong
        if (!inputFindPageTextField.getText().isEmpty()){

            int userInput = Integer.parseInt(inputFindPageTextField.getText());

            if (userInput>totalPage || userInput<0){
                feedbackLabel.setText("Input tidak sesuai, melebihi halaman yang ada");
            }
            else {
                // penyesuaian pdf mulai dari 0, user tidak
                int pageIndex = userInput -1;

                currentPage = pageIndex;

                showPdfPageInformation(pageIndex);

                renderPdfToImageView();
            }

        }
    }

    @FXML
    public void backButtonOnClick(){
        logger.info("back to home");

        pdfServices.closePDDocument();

        ScreenHelper.switchScreen(getStage(), Constant.HOME_FXML);
    }

    @FXML
    public void insertFileButtonOnAction(){
        logger.debug("user klik tombol tambah halaman yang ingin disisipkan");

        if(counter>=5){
            feedbackLabel.setText("maaf maksimum 5 kali sisipkan halaman");
            return;
        }

        Optional<File> fileChooser = Constant.pdfFileChooser("pilih file",getStage(),false);

        fileChooser.ifPresent((file)->{
            logger.info("Menambah file untuk di-insert: {}", file.getName());

            // buat instance
            InsertData data = new InsertData(file,currentPage);

            listOfFileWantToInsert.add(data);

            showFilesWantToInsert();
        });

    }

    private void showFilesWantToInsert(){

        filesInsertListView.setItems(FXCollections.observableArrayList(
                listOfFileWantToInsert.stream()
                        .map(InsertData::getName)
                        .collect(Collectors.toList())
        ));

    }

    @FXML
    public void cancelInsertButtonOnClick(){
        String userSelectedFile =  filesInsertListView.getSelectionModel().getSelectedItem();

        // melakukan pencarian insert data instance berdasarkan nama
        InsertData data = listOfFileWantToInsert.stream()
                .filter(insertData -> insertData.getName().equals(userSelectedFile))
                .findFirst()
                .orElse(null);

        if (data != null) {
            listOfFileWantToInsert.remove(data);
            logger.debug("file berhasil dihapus {}",userSelectedFile);

            showFilesWantToInsert();
        }

    }

    @FXML
    private void saveFileButtonOnAction(){
        logger.info("user klik save button");

        if (listOfFileWantToInsert.empty()){
            feedbackLabel.setText("belum ada halaman yang anda ingin sisipkan");

            return;
        }


        Optional<File> userFileSave = Constant.pdfFileChooser("simpan file",getStage(),true);

       userFileSave.ifPresent(file->{

           // Pastikan ekstensi .pdf ditambahkan jika user tidak mengetik manual
           if (!file.getName().toLowerCase().endsWith(".pdf")) {
               file = new File(file.getAbsolutePath() + ".pdf");
           }

           try {
               pdfServices.insertFilesToMainPdf(listOfFileWantToInsert,file);

               StringBuilder text = new StringBuilder();
               text.append("operasi insert berhasil, file ");
               text.append(file.getName());
               text.append(" telah disimpan");

               Constant.MESSAGE_OPERATION_SUCCESS = text.toString();

               logger.info("berhasil melakukan operasi sisipkan halaman");

               pdfServices.closePDDocument();

               ScreenHelper.switchScreen(getStage(),Constant.HOME_FXML);

           }catch (RuntimeException|IOException a){
               logger.error(a.getMessage(),a.getCause());

               Platform.exit();
           }
       });
    }
}
