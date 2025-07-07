module org.diagastika.pdfgeek {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires org.slf4j;



    opens org.diagastika.pdfgeek to javafx.fxml;
    exports org.diagastika.pdfgeek;
    exports org.diagastika.pdfgeek.Controller;
    opens org.diagastika.pdfgeek.Controller to javafx.fxml;
}