package cschuyle.securitoy;

public class StatisticsTicker implements Runnable {

    private final StatisticsRepository stats;

    public StatisticsTicker(StatisticsRepository stats) {
        this.stats = stats;
    }

    @Override
    public void run() {
        for(;;) {
            System.out.println(getStatsLine());
            sleep();
        }
    }

    private String getStatsLine() {
        return String.format("EventCnt: %d, ImgCnt:%d, AlarmCnt:%d, avgProcessingTime: %.1fms",
                stats.getCount(), stats.getCount(StatisticsRepository.IMG),
                stats.getCount(StatisticsRepository.ALARM), stats.getAvgProcessingTime());
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException _) {}
    }
}
