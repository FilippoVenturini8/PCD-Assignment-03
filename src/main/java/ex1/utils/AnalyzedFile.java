package ex1.utils;

public class AnalyzedFile implements Comparable<AnalyzedFile> {

    @Override
    public int compareTo(AnalyzedFile o) {
        return Integer.compare(o.lines, this.lines);
    }

    private String filePath;
    private int lines;

    public AnalyzedFile(String filePath, int lines) {
        this.filePath = filePath;
        this.lines = lines;
    }

    public String filePath() {
        return filePath;
    }

    public int lines() {
        return lines;
    }

    @Override
    public String toString() {
        return String.format("%5d | %s", lines, filePath);
    }
}
