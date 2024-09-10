package com.jujutsucraftaddon.capabilities.data;

import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.BarrierBreakProgessPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

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
