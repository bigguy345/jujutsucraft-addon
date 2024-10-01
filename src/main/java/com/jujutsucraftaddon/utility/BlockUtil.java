package com.jujutsucraftaddon.utility;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUtil {

    public static void doSphericalExplosion(LivingEntity entity, BlockPos hitBlock, int radius) {
        Level world = entity.level();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = hitBlock.offset(x, y, z);
                    BlockState state = world.getBlockState(targetPos);
                    double distanceSquared = x * x + y * y + z * z;
                    if (Math.sqrt(distanceSquared) <= radius && canBreak(world, state, targetPos)) {
                        double influence = (1.0 / (distanceSquared / (radius * radius) + 0.0001));
                        if (influence >= 1.5f)
                            entity.level().removeBlock(targetPos, true);
                    }
                }
            }
        }
    }

    public static boolean canBreak(Level world, BlockState state, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block != Blocks.BEDROCK && state.getDestroySpeed(world, pos) != -1;
    }

    public static void doMeleeExplosion(LivingEntity entity, BlockPos hitBlock, int radius) {
        Level world = entity.level();
        Vec3 lookVec = entity.getLookAngle().normalize();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = hitBlock.offset(x, y, z);
                    BlockState state = world.getBlockState(targetPos);
                    double distanceSquared = x * x + y * y + z * z;
                    if (Math.sqrt(distanceSquared) <= radius && canBreak(world, state, targetPos)) {
                        Vec3 blockVec = new Vec3(hitBlock.getX() + x, hitBlock.getY() + y, hitBlock.getZ() + z);
                        Vec3 toPlayer = blockVec.subtract(entity.position());

                        boolean breakGround = lookVec.y > -0.6 && entity.onGround() ? blockVec.y > entity.getBlockY() - 1 : true;

                        if (lookVec.dot(toPlayer.normalize()) > 0 && breakGround) {
                            double influence = (1.0 / (distanceSquared / (radius * radius) + 0.0001));
                            if (influence >= 1.5f)
                                entity.level().removeBlock(targetPos, true);
                        }
                    }
                }
            }
        }
    }

    public static void doMeleeExplosionDomainBarrier(LivingEntity entity, BlockPos hitBlock, int radius) {
        Level world = entity.level();
        Vec3 lookVec = entity.getLookAngle().normalize();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = hitBlock.offset(x, y, z);
                    BlockState state = world.getBlockState(targetPos);

                    double distanceSquared = x * x + y * y + z * z;
                    if (Math.sqrt(distanceSquared) <= radius && canBreak(world, state, targetPos)) {
                        Vec3 blockVec = new Vec3(hitBlock.getX() + x, hitBlock.getY() + y, hitBlock.getZ() + z);
                        Vec3 toPlayer = blockVec.subtract(entity.position());

                        boolean breakGround = lookVec.y > -0.6 && entity.onGround() ? blockVec.y > entity.getBlockY() - 1 : true;

                        if (lookVec.dot(toPlayer.normalize()) > 0 && breakGround) {
                            double influence = (1.0 / (distanceSquared / (radius * radius) + 0.0001));
                            if (influence >= 1.5f) {
                                BlockEntity barrierEntity = world.getBlockEntity(targetPos);
                                if (barrierEntity != null) {
                                    String oldBlock = barrierEntity.getPersistentData().getString("old_block");
                                    if (!oldBlock.isEmpty()) {
                                        world.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()), Vec2.ZERO, (ServerLevel) world, 4, "", Component.literal(""), world.getServer(), null).withSuppressedOutput(), "setblock ~ ~ ~ " + oldBlock);
                                        if (JujuUtil.isBarrier(world, targetPos))
                                            entity.level().removeBlock(targetPos, true);
                                    } else
                                        entity.level().removeBlock(targetPos, true);
                                } else
                                    entity.level().removeBlock(targetPos, true);
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<LivingEntity> getEntitiesWithinSphere(Level level, Vec3 center, double radius) {
        List<LivingEntity> entitiesInBoundingBox = level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius));
        return entitiesInBoundingBox.stream().filter(entity -> isEntityWithinSphere(entity, center, radius)).sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(center))).collect(Collectors.toList());
    }

    public static boolean isEntityWithinSphere(Entity entity, Vec3 center, double radius) {
        return entity.position().distanceToSqr(center) <= radius * radius;
    }
}
