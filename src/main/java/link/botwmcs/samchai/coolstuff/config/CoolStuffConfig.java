package link.botwmcs.samchai.coolstuff.config;




import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "botwmcs_coolstuff")
public class CoolStuffConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("sleep_skip_time")
    public SleepSkipTimeConfig sleepSkipTimeConfig = new SleepSkipTimeConfig();
}

