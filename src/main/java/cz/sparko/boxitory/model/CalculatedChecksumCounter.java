package cz.sparko.boxitory.model;

import java.util.Objects;

public class CalculatedChecksumCounter {
    private final int maxCalculatedChecksum;
    private int alreadyEnsuredChecksum;

    public CalculatedChecksumCounter(int maxEnsuredChecksum) {
        this.maxCalculatedChecksum = maxEnsuredChecksum;
        this.alreadyEnsuredChecksum = 0;
    }

    public void increment() {
        alreadyEnsuredChecksum++;
    }

    public boolean isLimitOfCalculatedChecksumExceeded() {
        return alreadyEnsuredChecksum > maxCalculatedChecksum;
    }

    @Override
    public String toString() {
        return "CalculatedChecksumCounter{" +
                "maxCalculatedChecksum=" + maxCalculatedChecksum +
                ", alreadyEnsuredChecksum=" + alreadyEnsuredChecksum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculatedChecksumCounter that = (CalculatedChecksumCounter) o;
        return maxCalculatedChecksum == that.maxCalculatedChecksum &&
                alreadyEnsuredChecksum == that.alreadyEnsuredChecksum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxCalculatedChecksum, alreadyEnsuredChecksum);
    }
}
