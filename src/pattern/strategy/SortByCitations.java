package pattern.strategy;

import java.util.Comparator;
import java.util.List;

import models.research.ResearchPaper;

public class SortByCitations implements SortStrategy {

    @Override
    public void sort(List<ResearchPaper> papers) {
        papers.sort(Comparator.comparingInt(ResearchPaper::getCitations).reversed());
    }
}
