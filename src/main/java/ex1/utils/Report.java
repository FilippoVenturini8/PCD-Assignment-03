package ex1.utils;

import java.util.List;
import java.util.Map;

public record Report(List<AnalyzedFile> ranking, Map<Interval, Integer> distribution) {
}