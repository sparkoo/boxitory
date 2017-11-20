package cz.sparko.boxitory.model;

import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@SpringBootTest
public class CalculatedChecksumCounterTest {

    private static final int MAX_ENSURED_CHECKSUM = 5;
    private static final int LESS_THEN_MAX_ENSURED_CHECKSUM = 4;
    private static final int EQUALS_TO_MAX_ENSURED_CHECKSUM = 5;
    private static final int GREATER_THEN_MAX_ENSURED_CHECKSUM = 6;

    @Test
    public void givenCalculatedChecksumCounter_whenAlreadyEnsuredChecksumIsLessThenMaxCalculatedChecksum_thenLimitIsNotExceeded() {
        CalculatedChecksumCounter calculatedChecksumCounter = new CalculatedChecksumCounter(MAX_ENSURED_CHECKSUM);
        for (int increments = 1; increments <= LESS_THEN_MAX_ENSURED_CHECKSUM; increments++) {
            assertFalse(calculatedChecksumCounter.isLimitOfCalculatedChecksumExceeded());
            calculatedChecksumCounter.increment();
        }
        assertFalse(calculatedChecksumCounter.isLimitOfCalculatedChecksumExceeded());
    }

    @Test
    public void givenCalculatedChecksumCounter_whenAlreadyEnsuredChecksumIsEqualToMaxCalculatedChecksum_thenLimitIsNotExceeded() {
        CalculatedChecksumCounter calculatedChecksumCounter = new CalculatedChecksumCounter(MAX_ENSURED_CHECKSUM);
        for (int increments = 1; increments <= EQUALS_TO_MAX_ENSURED_CHECKSUM; increments++) {
            assertFalse(calculatedChecksumCounter.isLimitOfCalculatedChecksumExceeded());
            calculatedChecksumCounter.increment();
        }
        assertFalse(calculatedChecksumCounter.isLimitOfCalculatedChecksumExceeded());
    }

    @Test
    public void givenCalculatedChecksumCounter_whenAlreadyEnsuredChecksumIsGreaterThenMaxCalculatedChecksum_thenLimitIsExceeded() {
        CalculatedChecksumCounter calculatedChecksumCounter = new CalculatedChecksumCounter(MAX_ENSURED_CHECKSUM);
        for (int increments = 1; increments <= GREATER_THEN_MAX_ENSURED_CHECKSUM; increments++) {
            calculatedChecksumCounter.increment();
        }
        assertTrue(calculatedChecksumCounter.isLimitOfCalculatedChecksumExceeded());
    }
}
