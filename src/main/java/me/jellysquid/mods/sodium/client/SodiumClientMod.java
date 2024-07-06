package me.jellysquid.mods.sodium.client;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SodiumClientMod {

    public static final String MODID = "vintagium";
    public static final String MODNAME = "Vintagium";
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MODID).get().getMetadata().getVersion().toString();

    private static SodiumGameOptions CONFIG;
    public static Logger LOGGER = LogManager.getLogger(MODNAME);

    public static SodiumGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger(MODNAME);
        }

        return LOGGER;
    }

    private static SodiumGameOptions loadConfig() {
        return SodiumGameOptions.load(FabricLoader.getInstance().getConfigDir().resolve(MODID + "-options.json"));
    }

    public static String getVersion() {
        return MOD_VERSION;
    }
    
    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}
