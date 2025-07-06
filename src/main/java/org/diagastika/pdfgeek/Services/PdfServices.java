package org.diagastika.pdfgeek.Services;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfServices {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private PDDocument userPdf;
    private PDFRenderer renderer;
    private String fileName;
    private File userFile;


    public PdfServices(File file) {
        fileName = file.getName();
        userFile = file;

        try {
            logger.info("start load user pdf");

            logger.info("user file {}",file.toString());

            this.userPdf = PDDocument.load(file);
            renderer = new PDFRenderer(userPdf);

        }catch (IOException e){
            logger.error("error when create instace PDDOCUMENT",e);
            Platform.exit();
        }

    }

    public int getTotalPages(){
        return userPdf.getNumberOfPages();
    }

    public Image getPdfImage(int pageIndex) throws IOException {
        BufferedImage bim = renderer.renderImageWithDPI(pageIndex, 150); // Render dengan 150 DPI
        Image img = SwingFXUtils.toFXImage(bim, null);
        return img;
    }

    public String getPdfName(){
        return fileName;
    }

    public void deletingPagesFromMainPdf(List<Integer> pagesWantToDelete, File fileName) throws IOException {

        logger.info("memulai proses hapus halaman");

            // Loop mundur supaya index tetap stabil saat menghapus
            for (int i = userPdf.getNumberOfPages() - 1; i >= 0; i--) {
                if (pagesWantToDelete.contains(i)) {

                    userPdf.removePage(i);

                    logger.debug("hapus halaman {}",i);
                }
            }

        // Cek sisa halaman
        if (userPdf.getNumberOfPages() == 0) {

            logger.warn("Semua halaman dihapus, tidak menyimpan file kosong.");
            
        }else {
            userPdf.save(fileName);

        }

    }


    public void mergeFilesToMainPdf(List<File> filesWantToMergeWithMainFile, File wheresToSave) throws IOException {
        logger.info("memulai untuk menggabungkan files");

        PDFMergerUtility merger = new PDFMergerUtility();

        merger.setDestinationFileName(wheresToSave.getAbsolutePath());

        // jangan lupa menambahkan file utama
        filesWantToMergeWithMainFile.addFirst(userFile);

            // Tambahkan semua file ke merger
            for (File pdf : filesWantToMergeWithMainFile) {

                logger.info("menggabungkan file dengan {}",pdf.getName());

                merger.addSource(pdf);
            }

            // Lakukan penggabungan
            merger.mergeDocuments(null);
    }
}
