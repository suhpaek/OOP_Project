package academic;

import java.io.Serializable;

public class Mark implements Serializable {
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark() {
    }

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public void setFirstAttestation(double firstAttestation) {
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public void setSecondAttestation(double secondAttestation) {
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(double finalExam) {
        this.finalExam = finalExam;
    }

    public double getTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }

    public boolean isPassed() {
        return getTotal() >= 50;
    }

    public String getLetterGrade() {
        double total = getTotal();

        if (total >= 95) return "A";
        if (total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";
        return "F";
    }
    @Override
    public String toString() {
        return "Mark{" +
                "firstAttestation=" + firstAttestation +
                ", secondAttestation=" + secondAttestation +
                ", finalExam=" + finalExam +
                ", total=" + getTotal() +
                ", letterGrade='" + getLetterGrade() + '\'' +
                '}';
    }
}
