package link.botwmcs.samchai.coolstuff.mixin.sleepskiptime;

import link.botwmcs.samchai.coolstuff.util.SleepSkipTimeMath;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static link.botwmcs.samchai.coolstuff.util.SleepSkipTimeMath.DAY_LENGTH;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @Shadow
    @Final
    List<ServerPlayer> players;
    @Shadow
    @Final
    private ServerLevelData serverLevelData;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    @Final
    private SleepStatus sleepStatus;
//    @Final
//    private ServerChunkCache serverChunkCache;
//    @Shadow
//    @Final
//    protected Raids raids;

    // @Shadow public abstract void levelEvent(@Nullable Player pPlayer, int pType, BlockPos pPos, int pData);

    protected ServerLevelMixin(WritableLevelData pLevelData,
                               ResourceKey<Level> pDimension,
                               Holder<DimensionType> pDimensionTypeRegistration,
                               Supplier<ProfilerFiller> pProfiler,
                               boolean pIsClientSide,
                               boolean pIsDebug,
                               long pBiomeZoomSeed) {
        super(pLevelData, pDimension, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I"))
    public void tickInject(BooleanSupplier shouldKeepTicking, CallbackInfo callbackInfo) {
        // check if anyone is sleeping
        int sleepingPlayerCount = sleepStatus.amountSleeping();
        if (sleepingPlayerCount <= 0) {
            return;
        }

        // do some mathworks
        int playerCount = server.getPlayerCount();
        double sleepingRatio = (double) sleepingPlayerCount / playerCount;
        double sleepingPercentage = sleepingRatio * 100;
        int nightTimeStepPerTick = SleepSkipTimeMath.calNightTimeStepPerTick(sleepingRatio, 25); // remember this multiplier need to config
//        int blockEntityTickSpeedMultiplier = (int) Math.round((double) 25);
//        int chunkTickSpeedMultiplier = (int) Math.round((double) 25);
//        int raidTickSpeedMultiplier = (int) Math.round((double) 25);
        boolean doDayLightCycle = server.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
        int playersRequiredToSleepPercentage = server.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        // 1.0 * gamerule: no loss of precision
        double playersRequiredToSleepRatio = 1.0 * server.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) / 100;
        int playersRequiredToSleep = (int) Math.ceil(playersRequiredToSleepRatio * playerCount);

        // check if the required percentage of players are sleeping
        if (sleepingPercentage < playersRequiredToSleepPercentage) {
            for (ServerPlayer player : players) {
                // this chat need i18n.
                player.displayClientMessage(Component.nullToEmpty(sleepingPlayerCount + "/" + playerCount + " players are sleeping... " + playersRequiredToSleep + "/" + playerCount + " players are required to sleep through the night."), true);
            }
            return;
        }

        // Advance time
        serverLevelData.setGameTime(serverLevelData.getGameTime() + nightTimeStepPerTick);
        if (doDayLightCycle) {
            serverLevelData.setDayTime(serverLevelData.getDayTime() + nightTimeStepPerTick);
        }

        // Tick be and chunks (REAL TIME WARPING)
//        for (int i = blockEntityTickSpeedMultiplier; i > 1; i--) {
//            this.tickBlockEntities();
//        }
//
//        for (int i = chunkTickSpeedMultiplier; i > 1; i--) {
//            serverChunkCache.tick(shouldKeepTicking, true);
//        }
//
//        for (int i = raidTickSpeedMultiplier; i > 1; i--) {
//            raids.tick();
//        }

        // send new time to all players in the overworld (network packet)
        server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(serverLevelData.getGameTime(), serverLevelData.getDayTime(), doDayLightCycle), dimension());

        // send HUD message to all players
        int secondsUntilAwake = Math.abs(SleepSkipTimeMath.calSecondsUntilAwake((int) serverLevelData.getDayTime() % 24000, nightTimeStepPerTick, 20));
        if (secondsUntilAwake >= 2) {
            for (ServerPlayer player : players) {
                // msg need i18n
                if (serverLevelData.isThundering()) {
                    player.displayClientMessage(Component.nullToEmpty(sleepingPlayerCount + "/" + playerCount + " players are sleeping through this thunderstorm (time until dawn: " + secondsUntilAwake + "s)"), true);
                } else {
                    player.displayClientMessage(Component.nullToEmpty(sleepingPlayerCount + "/" + playerCount + " players are sleeping through this night (time until dawn: " + secondsUntilAwake + "s)"), true);
                }
            }
        }

        // check if its dawn
        if (secondsUntilAwake < 2) {
            // check if world is raining or thundering
            if (serverLevelData.isRaining() && serverLevelData.isThundering()){
                // Clear weather and reset weather clock
                serverLevelData.setRaining(false);
                serverLevelData.setThundering(false);
                serverLevelData.setClearWeatherTime((int) (DAY_LENGTH * SleepSkipTimeMath.getRandomNumberInRange(0.5, 7.5)));
            }

//            // Check if dawn message isn't set to nothing
//            if (!config.dawnMessage.equals("")) {
//                // Send HUD message to all players
//                for (ServerPlayerEntity player : players) {
//                    player.sendMessage(Text.of(config.dawnMessage), true);
//                }
//            }
        }
    }

    /**
     * @author Sam_Chai
     * @reason Method's HUD messages conflicts with my custom HUD messages
     */
    @Overwrite
    private void announceSleepStatus() {
    }
}
