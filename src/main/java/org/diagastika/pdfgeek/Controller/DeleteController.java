package org.diagastika.pdfgeek.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.diagastika.pdfgeek.Constant;
import org.diagastika.pdfgeek.Helper.ScreenHelper;
import org.diagastika.pdfgeek.Services.PdfServices;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Stack;

public class DeleteController extends BaseController {
    private int currentPage=0;
    private int totalPage;
    private Stack<Integer> listPageToDelete=new Stack<>();

    @FXML
    private Label pageInformationLabel;

    @FXML
    private TextField pathFileSaveTextField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Label selectedPagesToDeleteLabel;

    @FXML
    private Label pdfNameLabel;

    @FXML
    private TextField inputFindPageTextField;

    @FXML
    private ImageView pdfViewImage;

    @Override
    protected void initialize() {
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
    public void addPageWantToDeleteButtonOnClick(){
        logger.debug("user klik tombol tambah halaman yang ingin didelete");

        // memeriksa agar tidak ada duplikasi halaman yang akan dihapus
        if (!listPageToDelete.contains(currentPage)){
            listPageToDelete.add(currentPage);

            showListOfPagesWantToDelete();
        }

    }

    @FXML
    public void saveFileButtonOnClick(){
        logger.debug("user klik tombol simpan file");

        if (listPageToDelete.empty()){
            feedbackLabel.setText("tidak ada halaman yang ingin dihapus");
            return;
        }

        Optional<File> pdfFileChooser = Constant.pdfFileChooser("simpan file",getStage(),true);

        pdfFileChooser.ifPresent((file)->{

            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            try {
                pdfServices.deletingPagesFromMainPdf(listPageToDelete,file);

                StringBuilder text = new StringBuilder();
                text.append("operasi delete berhasil, file ");
                text.append(file.getName());
                text.append(" telah disimpan");

                Constant.MESSAGE_OPERATION_SUCCESS = text.toString();

                logger.info("sukses operasi hapus halaman");

                pdfServices.closePDDocument();

                ScreenHelper.switchScreen(getStage(),Constant.HOME_FXML);

            } catch (IOException e) {

                logger.error("gagal simpan file, hasil operasi hapus halaman user pdf file {}",pdfServices.getPdfName(),e);

                Platform.exit();
            }
        });
    }

    @FXML
    public void undoLastIndexPageWantToDeleteButtonOnClick(){
        logger.debug("user klik tombol undo halaman yang ingin dihapus");

        if (!listPageToDelete.empty()){

            listPageToDelete.pop();

            showListOfPagesWantToDelete();

        }
    }

    @FXML
    public void backButtonOnClick(){
        logger.info("back to home");

        pdfServices.closePDDocument();

        ScreenHelper.switchScreen(getStage(), Constant.HOME_FXML);
    }

    /**
     * untuk set text pada daftar halaman yang akan di hapus
     */
    private void showListOfPagesWantToDelete(){
        StringBuilder string = new StringBuilder();

        if (listPageToDelete.empty()){
            string.append("");
        }else {
            listPageToDelete.forEach(e -> {
                string.append(e + 1).append(",");
            });
        }

        selectedPagesToDeleteLabel.setText(string.toString());
    }
}
