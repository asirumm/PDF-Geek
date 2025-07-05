module org.diagastika.pdfgeek {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.diagastika.pdfgeek to javafx.fxml;
    exports org.diagastika.pdfgeek;
}