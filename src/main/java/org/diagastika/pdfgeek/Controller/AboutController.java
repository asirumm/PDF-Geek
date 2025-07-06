package org.diagastika.pdfgeek.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.diagastika.pdfgeek.Helper.ScreenHelper;

import static org.diagastika.pdfgeek.Constant.HOME_FXML;

public class AboutController extends BaseController {
    @FXML
    private void backOnAction(ActionEvent event){
        ScreenHelper.switchScreen(ScreenHelper.getStage(event), HOME_FXML);
    }
}
