package org.diagastika.pdfgeek.Data;

import java.io.File;

public class InsertData {
    private File file;
    private int index;
    private String name;

    public InsertData(File file, int index) {
        this.file = file;
        this.index = index;

        StringBuilder text = new StringBuilder();
        text.append(file.getName());
        text.append(" [");
        text.append(index+1);// ingat user melihat tidak berdasarkan index (0)
        text.append("]");
        this.name = text.toString();
    }

    public File getFile() {
        return file;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
