package com.jujutsucraftaddon.capabilities.data;

import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.BarrierBreakProgessPacket;
import com.jujutsucraftaddon.utility.JujuUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Handles the break progress of domain barriers when you hit them
 */
public class BarrierBreakProgressData {

    public static void setProgress(LivingEntity entity, BlockPos pos, byte progress) {
        CompoundTag data = entity.getPersistentData().getCompound("barrierBlockProgress");
        if (data.isEmpty())
            entity.getPersistentData().put("barrierBlockProgress", data = new CompoundTag());

        Entry entry = new Entry(pos, progress);
        entry.serialise(data);
        PacketHandler.sendToTrackingChunk(entity.level().getChunkAt(entry.pos), new BarrierBreakProgessPacket(pos, progress));
    }

    public static void setProgress(LivingEntity entity, BlockPos pos, float progress) {
        setProgress(entity, pos, bytify(progress));
    }

    /*
    bytifies 0-1 float to 0-9 byte
     */
    public static byte bytify(float progress) {
        return (byte) Math.floor(Math.min(progress, 1) * 10 - 1);
    }

    public static void setSurroundingProgress(LivingEntity entity, float shatter, float breaksAt, BlockPos center, byte progress, int radius) {
        Level world = entity.level();
        CompoundTag data = entity.getPersistentData();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos offset = center.offset(x, y, z);
                    double distanceFromCenter = Math.sqrt(offset.distSqr(center));
                    if (distanceFromCenter <= radius && JujuUtil.isBarrier(world, offset)) {
                        //   byte newOffsetProgress = (byte) (Math.max(progress - Math.pow(distanceFromCenter,2f), offsetProgress));
                        // setProgress(entity, offset, newOffsetProgress);

                        BlockEntity blockEntity = world.getBlockEntity(offset);
                        if (blockEntity != null && !offset.equals(center)) {
                            CompoundTag blockData = blockEntity.getPersistentData();
                            float prevShatter = blockData.getFloat("shatter");
                            float newShatter = (float) (prevShatter + (shatter * Math.max(1 - distanceFromCenter / radius, 0.1))); //should be based off of total shatter
                            blockData.putFloat("shatter", newShatter);
                            float shatterPercent = newShatter / breaksAt;

                            if (shatterPercent >= 0.1) {
                                byte prevProgress = getProgress(data, offset);
                                byte newProgress = bytify(shatterPercent);
                                if (prevProgress != newProgress)
                                    setProgress(entity, offset, newProgress);
                            }
                        }
                    }
                }
            }
        }
    }

    public static byte getProgress(CompoundTag entityData, BlockPos pos) {
        CompoundTag data = entityData.getCompound("barrierBlockProgress");
        if (data.isEmpty())
            return -1;

        Entry entry = new Entry(pos);
        CompoundTag entryData = data.getCompound(entry.toString());
        if (entryData.isEmpty())
            return -1;
        return entryData.getByte("progress");
    }

    public static void removeAllProgress(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData().getCompound("barrierBlockProgress");
        if (data.isEmpty())
            return;

        for (String name : data.getAllKeys()) {
            Entry entry = Entry.deserialise(data.getCompound(name));
            PacketHandler.sendToTrackingChunk(entity.level().getChunkAt(entry.pos), new BarrierBreakProgessPacket(entry.pos, (byte) -1));
        }

        entity.getPersistentData().remove("barrierBlockProgress");
    }

    public static void sendProgressToTracking(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData().getCompound("barrierBlockProgress");
        if (data.isEmpty())
            return;

        for (String name : data.getAllKeys()) {
            Entry entry = Entry.deserialise(data.getCompound(name));
            PacketHandler.sendToTrackingChunk(entity.level().getChunkAt(entry.pos), new BarrierBreakProgessPacket(entry.pos, entry.progress));
        }
    }

    public static class Entry {
        public BlockPos pos;
        public byte progress;

        public Entry(BlockPos pos) {
            this.pos = pos;
        }

        public Entry(BlockPos pos, byte progress) {
            this.pos = pos;
            this.progress = progress;
        }

        public Tag serialise(CompoundTag tag) {
            String name = toString();
            CompoundTag entryTag = tag.getCompound(name);
            if (entryTag.isEmpty())
                tag.put(name, entryTag);

            entryTag.putInt("x", pos.getX());
            entryTag.putInt("y", pos.getY());
            entryTag.putInt("z", pos.getZ());
            entryTag.putByte("progress", progress);

            return tag;
        }

        public static Entry deserialise(CompoundTag tag) {
            return new Entry(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), tag.getByte("progress"));
        }

        @Override
        public String toString() {
            return pos.getX() + "," + pos.getY() + "," + pos.getZ();
        }
    }
}
