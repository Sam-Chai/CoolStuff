package link.botwmcs.samchai.coolstuff.mixin.sleepskiptime;

import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SleepStatus.class)
public class SleepOverrideMixin {
    /**
     * @author Sam_Chai
     * @reason https://github.com/Steveplays28/realisticsleep/blob/main/src/main/java/com/github/steveplays28/realisticsleep/mixin/SleepOverrideMixin.java
     */
    @Overwrite
    public boolean areEnoughSleeping(int percentage){
        return false;
    }
}
