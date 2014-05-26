package cschuyle.securitoy;

public class Main {
    public static void main(String[] _) {
        try {
            StatisticsRepository stats = new StatisticsRepository();
            new Thread(new StatisticsTicker(stats)).start();
            EventMonitor eventMonitor = new EventMonitor(new EventCategorizer(), stats);
            Thread folderMonitor = eventMonitor.monitorFolder("./input");
            folderMonitor.start();
            folderMonitor.join();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Sorry, fatal error.  Someone messed up.");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
