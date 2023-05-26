package ex1.commands.documentanalyzer;

import ex1.model.Document;

import java.io.File;
import java.io.IOException;

public class CountLines implements DocumentAnalyzerCommand{
    private final Document document;

    public CountLines(String documentPath){
        try {
            this.document = Document.fromFile(new File(documentPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document getDocument(){
        return this.document;
    }
}
