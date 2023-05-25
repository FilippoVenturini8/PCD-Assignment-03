package ex1.model;

public record Interval(int min, int max) implements Comparable<Interval>{

    public boolean contains(int value){
        return value >= min && value < max;
    }

    @Override
    public String toString() {
        return "[" + min + ", " + max + '[';
    }

    @Override
    public int compareTo(Interval o) {
        return Integer.compare(this.min, o.min);
    }
}
