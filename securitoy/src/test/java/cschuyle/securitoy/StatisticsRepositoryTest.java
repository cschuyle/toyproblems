package cschuyle.securitoy;

import cschuyle.securitoy.StatisticsRepository;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatisticsRepositoryTest {

    static final double EPSILON = 0.0000001;

    @Test
    public void shouldReportZeroCountIfNoStats() {
        assertEquals(new StatisticsRepository().getCount(StatisticsRepository.ALARM), 0);
    }

    @Test
    public void shouldAccumulateEventCount() {
        assertEquals(new StatisticsRepository().markEvent(StatisticsRepository.IMG).getCount(), 1);
    }

    @Test
    public void shouldReportZeroProcessingTimeIfNoStats() {
        assertEquals(0.0, new StatisticsRepository().getAvgProcessingTime(), EPSILON);
    }

    @Test
    public void shouldTrackProcessingTimeByBlockTimer() {
        StatisticsRepository stats = new StatisticsRepository();
        stats.time().stop();
        assertFalse(stats.getAvgProcessingTime() == 0.0);
    }
}
