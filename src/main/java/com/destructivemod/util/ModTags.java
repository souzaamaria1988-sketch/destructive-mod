package com.destructivemod.util;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import com.destructivemod.DestructiveMod;
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> DESTRUCTIBLE = createTag("destructible");
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(DestructiveMod.MOD_ID, name));
        }
    }
}