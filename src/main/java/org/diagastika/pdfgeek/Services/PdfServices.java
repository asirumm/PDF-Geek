package org.diagastika.pdfgeek.Services;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.diagastika.pdfgeek.Data.InsertData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfServices {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private PDDocument mainPdf;
    private PDFRenderer renderer;
    private String fileName;
    private File userFile;


    public PdfServices(File file) {
        fileName = file.getName();
        userFile = file;

        try {
            logger.info("start load user pdf");

            logger.info("user file {}",file.toString());

            this.mainPdf = PDDocument.load(file);
            renderer = new PDFRenderer(mainPdf);

        }catch (IOException e){
            logger.error("error when create instace PDDOCUMENT",e);
            Platform.exit();
        }

    }

    public void closePDDocument(){
        try {
            mainPdf.close();

            logger.info("berhasil menutup PDDocument");

        } catch (IOException e) {
            logger.error("gagal menutup resources",e);
            Platform.exit();
        }
    }

    public int getTotalPages(){
        return mainPdf.getNumberOfPages();
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
            for (int i = mainPdf.getNumberOfPages() - 1; i >= 0; i--) {
                if (pagesWantToDelete.contains(i)) {

                    mainPdf.removePage(i);

                    logger.debug("hapus halaman {}",i);
                }
            }

        // Cek sisa halaman
        if (mainPdf.getNumberOfPages() == 0) {

            logger.warn("Semua halaman dihapus, tidak menyimpan file kosong.");
            
        }else {
            mainPdf.save(fileName);
        }

    }

    public void insertFilesToMainPdf(List<InsertData> insertData,File wheresSave) throws IOException {
        // ini dibuat agar tidak kena COSStream has been closed and cannot be read.
        // kita harus menahannay sampai main pdf di save
        ArrayList<PDDocument> listOfPDDocumentShouldClose = new ArrayList<>();

        insertData.forEach(value->{
            try {
                logger.info("menyisipkan file {} ke halaman {}",value.getFile().getName(),value.getIndex());

                PDDocument temp = insertPageToMainPdf(value.getFile(),value.getIndex());

                listOfPDDocumentShouldClose.add(temp);

            } catch (IOException e) {
               throw new RuntimeException("gagal melakukan operasi sisipkan",e);
            }
        });

        mainPdf.save(wheresSave);

        listOfPDDocumentShouldClose.forEach(data->{
            try {
                data.close();
            } catch (IOException e) {
                logger.error("gagal menutup PDDocument",e);
                Platform.exit();
            }
        });
    }


    private PDDocument insertPageToMainPdf(File fileToInsert, int whereToInsert) throws IOException {

        PDDocument fileWantToInsert = PDDocument.load(fileToInsert);

        int totalPagesToInsert = fileWantToInsert.getNumberOfPages();

        for (int i = 0; i < totalPagesToInsert; i++) {
            PDPage halamanSisip = fileWantToInsert.getPage(i);

            // Solusi aman: Import halaman ke mainPdf
            PDPage importedPage = mainPdf.importPage(halamanSisip);

            mainPdf.getPages().insertBefore(importedPage, mainPdf.getPage(whereToInsert));
            whereToInsert++;
        }

        return fileWantToInsert;
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
