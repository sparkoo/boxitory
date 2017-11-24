package cz.sparko.boxitory.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatedChecksumCounter {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatedChecksumCounter.class);

    private final int maxCalculatedChecksum;
    private int alreadyEnsuredChecksum;

    public CalculatedChecksumCounter(int maxEnsuredChecksum) {
        this.maxCalculatedChecksum = maxEnsuredChecksum;
        this.alreadyEnsuredChecksum = 0;
    }

    public void increment() {
        alreadyEnsuredChecksum++;
        LOG.trace("Calculated checksum for {}/{} boxes.", alreadyEnsuredChecksum, maxCalculatedChecksum);
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
}
