package ex1.model;

public class SetupInfo{

    private String directory;
    private int nFiles;
    private int nIntervals;
    private int lastIntervalLowerBound;

    public SetupInfo(String directory, int nFiles, int nIntervals, int lastIntervalLowerBound) {
        this.directory = directory;
        this.nFiles = nFiles;
        this.nIntervals = nIntervals;
        this.lastIntervalLowerBound = lastIntervalLowerBound;
    }

    public String directory() {
        return directory;
    }

    public int nFiles() {
        return nFiles;
    }

    public int nIntervals() {
        return nIntervals;
    }

    public int lastIntervalLowerBound() {
        return lastIntervalLowerBound;
    }
}