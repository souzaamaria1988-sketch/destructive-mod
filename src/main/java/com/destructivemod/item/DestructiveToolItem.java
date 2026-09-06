package com.destructivemod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import java.util.Queue;
import java.util.LinkedList;

public class DestructiveToolItem extends Item {
    private final int damage;
    private final int radius;
    private final int cooldown;
    private final String particleType;
    private static final Queue<BlockPos> destructionQueue = new LinkedList<>();
    private static int ticksSinceLastDestruction = 0;

    public DestructiveToolItem(Settings settings, int damage, int radius, int cooldown, String particleType) {
        super(settings);
        this.damage = damage;
        this.radius = radius;
        this.cooldown = cooldown;
        this.particleType = particleType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (!world.isClient() && player != null) {
            if (player.getItemCooldownManager().isCoolingDown(this)) return ActionResult.PASS;
            ServerWorld serverWorld = (ServerWorld) world;
            addToDestructionQueue(serverWorld, pos, radius);
            player.getItemCooldownManager().set(this, cooldown);
            serverWorld.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            spawnParticles(serverWorld, pos);
        }
        return ActionResult.SUCCESS;
    }

    private void addToDestructionQueue(ServerWorld world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + y*y + z*z <= radius*radius) destructionQueue.add(center.add(x, y, z));
                }
            }
        }
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 50; i++) {
            ParticleTypes pt = particleType.equals("flame") ? ParticleTypes.FLAME : particleType.equals("portal") ? ParticleTypes.PORTAL : particleType.equals("electric") ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.CLOUD;
            world.spawnParticles(pt, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.5, 0.5, 0.5, 0.1);
        }
    }

    public static void tickDestructionQueue(ServerWorld world) {
        ticksSinceLastDestruction++;
        if (ticksSinceLastDestruction >= 1 && !destructionQueue.isEmpty()) {
            int blocksToDestroy = Math.min(10, destructionQueue.size());
            for (int i = 0; i < blocksToDestroy; i++) {
                BlockPos pos = destructionQueue.poll();
                if (pos != null) world.breakBlock(pos, true);
            }
            ticksSinceLastDestruction = 0;
        }
    }
}