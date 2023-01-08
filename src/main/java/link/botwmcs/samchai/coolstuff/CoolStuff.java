package link.botwmcs.samchai.coolstuff;

import link.botwmcs.samchai.coolstuff.config.CoolStuffConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("botwmcs_coolstuff")
public class CoolStuff {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static CoolStuffConfig config;

    public CoolStuff() {
        LOGGER.info("[BotWMCS CoolStuff] Loading...");
        AutoConfig.register(
                CoolStuffConfig.class,
                PartitioningSerializer.wrap(Toml4jConfigSerializer::new)
        );
        config = AutoConfig.getConfigHolder(CoolStuffConfig.class).getConfig();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("[BotWMCS CoolStuff] Initializing...");

    }
//
//    private void enqueueIMC(final InterModEnqueueEvent event) {
//        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("CoolStuff", "helloworld", () -> {
//            LOGGER.info("Hello world from the MDK");
//            return "Hello world";
//        });
//    }
//
//    private void processIMC(final InterModProcessEvent event) {
//        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m -> m.messageSupplier().get()).
//                collect(Collectors.toList()));
//    }
//
//    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event) {
//        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
//    }

}
