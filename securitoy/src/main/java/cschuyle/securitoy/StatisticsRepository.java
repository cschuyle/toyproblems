package cschuyle.securitoy;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class StatisticsRepository {

    public final static String IMG = "Img";
    public final static String ALARM = "Alarm";

    private final static Set<String> STATISTICS = new HashSet<String>();
    {
        STATISTICS.add(IMG);
        STATISTICS.add(ALARM);
    }

    private final MetricRegistry metrics = new MetricRegistry();
    private long count = 0;

    public StatisticsRepository markEvent(String name) {
        if(! STATISTICS.contains(name)) {
            throw new IllegalArgumentException("'" + name + "' is not a valid statistic.");
        }
        meterFor(name).mark();
        return markEvent();
    }

    public StatisticsRepository markEvent() {
        ++ count;
        return this;
    }

    public long getCount() {
        return count;
    }

    public long getCount(String name) {
        return meterFor(name).getCount();
    }

    public double getAvgProcessingTime() {
        return timerFor("").getSnapshot().getMean()/1000000.0; // Why can't I pass a unit when I get the mean??
    }

    public Timer.Context time() {
        return timerFor("").time();
    }

    private Timer timerFor(String name) {
        return metrics.timer(name + "Timer");
    }

    private Meter meterFor(String name) {
        return metrics.meter(name + "Meter");
    }

}
