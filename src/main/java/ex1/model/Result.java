package ex1.model;

import java.util.*;
import java.util.stream.Collectors;

public class Result {
    private final Set<AnalyzedFile> ranking = new TreeSet<>();
    private final Map<Interval, Integer> distribution = new TreeMap<>();
    private final int nIntervals;
    private final int lastIntervalLowerBound;
    private int n;

    public Result(int nIntervals, int lastIntervalLowerBound, int n) {
        this.nIntervals = nIntervals;
        this.lastIntervalLowerBound = lastIntervalLowerBound;
        if(nIntervals == 1){
            distribution.put(new Interval(0, Integer.MAX_VALUE), 0);
        }else {
            final int intervalSize = lastIntervalLowerBound / (nIntervals - 1);
            for (int i = 0; i < nIntervals - 2; i++) {
                distribution.put(new Interval(intervalSize * i, intervalSize * (i + 1)), 0);
            }
            distribution.put(new Interval(intervalSize * (nIntervals - 2), lastIntervalLowerBound), 0);
            distribution.put(new Interval(lastIntervalLowerBound, Integer.MAX_VALUE), 0);
        }
        this.n = n;
    }

    public synchronized List<AnalyzedFile> getRanking(int n) {
        return ranking.stream().limit(n).collect(Collectors.toList());
    }

    public synchronized List<AnalyzedFile> getRanking() {
        return this.getRanking(this.n);
    }

    public synchronized Set<AnalyzedFile> getFiles() {
        return ranking;
    }

    public synchronized Map<Interval, Integer> getDistribution() {
        return new HashMap<>(distribution);
    }

    public synchronized void add(AnalyzedFile item) {
        for(Map.Entry<Interval, Integer> entry : distribution.entrySet()){
            if(entry.getKey().contains(item.lines())){
                entry.setValue(entry.getValue() + 1);
            }
        }
        this.ranking.add(item);
    }

    public synchronized void merge(Result result2) {
        for(AnalyzedFile entry : result2.getFiles()){
            this.add(entry);
        }
    }
    
    public synchronized Result accumulate(AnalyzedFile item) {
        this.add(item);
        return this;
    }
}
