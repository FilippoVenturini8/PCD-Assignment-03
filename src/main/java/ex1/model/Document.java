package ex1.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Document {
    private final File file;

    private Document(File file){
        this.file = file;
    }
    
    public static Document fromFile(File file) throws IOException {
        return new Document(file);
    }

    public int countLines() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            reader.close();
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath(){
        return this.file.getPath();
    }
}
