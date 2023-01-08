package link.botwmcs.samchai.coolstuff.util;

public class SleepSkipTimeMath {
    public static final int DAY_LENGTH = 24000;
    public static final int AWAKE_TIME = 24000;

    public static int calNightTimeStepPerTick(double sleepingRatio, double multiplier) {
        return (int) Math.round(sleepingRatio * multiplier);
    }

    public static int calTicksToTimeOfDay(int timeOfDay, int targetTimeOfDay) {
        return targetTimeOfDay - timeOfDay;
    }

    public static int calTicksUntilAwake(int currentTimeOfDay) {
        return calTicksToTimeOfDay(currentTimeOfDay, AWAKE_TIME);
    }

    public static int calSecondsUntilAwake(int currentTimeOfDay, double timeStepPerTick, double tps) {
        return (int) Math.round(calTicksUntilAwake(currentTimeOfDay) / timeStepPerTick / tps);
    }

    public static double getRandomNumberInRange(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
}
