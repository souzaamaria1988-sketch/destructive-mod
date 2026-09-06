package com.destructivemod;
import net.fabricmc.api.ModInitializer;
import com.destructivemod.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestructiveMod implements ModInitializer {
    public static final String MOD_ID = "destructivemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Destructive Mod");
        ModItems.registerItems();
        LOGGER.info("Destructive Mod initialized!");
    }
}