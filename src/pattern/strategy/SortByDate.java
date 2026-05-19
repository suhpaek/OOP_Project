package pattern.strategy;

import models.research.ResearchPaper;
import java.util.Comparator;
import java.util.List;

public class SortByDate implements SortStrategy {

    @Override
    public void sort(List<ResearchPaper> papers) {
        papers.sort(Comparator.comparing(ResearchPaper::getPublishDate));
    }
}
