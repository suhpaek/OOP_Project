package models.academic;

import java.io.Serializable;
import java.util.Objects;

public class Mark implements Serializable {
    private static final double MAX_FIRST_ATTESTATION = 30.0;
    private static final double MAX_SECOND_ATTESTATION = 30.0;
    private static final double MAX_FINAL_EXAM = 40.0;

    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark() {
    }

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        setFirstAttestation(firstAttestation);
        setSecondAttestation(secondAttestation);
        setFinalExam(finalExam);
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public void setFirstAttestation(double firstAttestation) {
        validateComponent(firstAttestation, MAX_FIRST_ATTESTATION, "First attestation");
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public void setSecondAttestation(double secondAttestation) {
        validateComponent(secondAttestation, MAX_SECOND_ATTESTATION, "Second attestation");
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(double finalExam) {
        validateComponent(finalExam, MAX_FINAL_EXAM, "Final exam");
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

    private void validateComponent(double value, double maxValue, String fieldName) {
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and " + maxValue);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;
        Mark mark = (Mark) o;
        return Double.compare(mark.firstAttestation, firstAttestation) == 0
                && Double.compare(mark.secondAttestation, secondAttestation) == 0
                && Double.compare(mark.finalExam, finalExam) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstAttestation, secondAttestation, finalExam);
    }
}
