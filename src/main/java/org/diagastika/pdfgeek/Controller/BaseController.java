package org.diagastika.pdfgeek.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.diagastika.pdfgeek.Services.PdfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @FXML
    protected Pane container;

    protected PdfServices pdfServices;

    protected void setPdfServices(PdfServices pdfServices){
        this.pdfServices = pdfServices;
    }

    @FXML
    protected void initialize(){
        logger.info("Memulai screen {}",getClass().getName());
    }

    protected Stage getStage() {
        return (Stage) container.getScene().getWindow();
    }
}
