package pattern.strategy;

import models.research.ResearchPaper;
import java.util.List;

public interface SortStrategy {
    void sort(List<ResearchPaper> papers);
}
