package link.botwmcs.samchai.coolstuff.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "sleep_skip_time")
public class SleepSkipTimeConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableModule = true;
    @ConfigEntry.Gui.Tooltip
    public boolean enableSleepingStatusLog = true;
    @ConfigEntry.Gui.Tooltip
    public int sleepSpeedMultiplier = 25;
}
