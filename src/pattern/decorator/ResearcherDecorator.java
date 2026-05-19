package pattern.decorator;

public abstract class ResearcherDecorator implements Researcher {
    protected Researcher researcher;

    public ResearcherDecorator(Researcher researcher) {
        this.researcher = researcher;
    }

    @Override
    public void conductResearch() {
        researcher.conductResearch();
    }
}