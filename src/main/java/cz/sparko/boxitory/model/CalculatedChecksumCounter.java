package cz.sparko.boxitory.model;

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
}
