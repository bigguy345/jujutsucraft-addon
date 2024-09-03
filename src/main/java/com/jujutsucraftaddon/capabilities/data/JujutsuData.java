package com.jujutsucraftaddon.capabilities.data;

import com.jujutsucraftaddon.capabilities.ModCapabilities;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.SyncJujutsuData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class JujutsuData {
    public Player player;
    public double blackFlashChance = -1;

    public JujutsuData() {
    }

    public JujutsuData(Player player) {
        this.player = player;
    }

    public Tag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("blackFlashChance", blackFlashChance);
        return nbt;
    }

    public void readNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        blackFlashChance = nbt.getDouble("blackFlashChance");
    }

    public static JujutsuData get(Player player) {
        return player.getCapability(ModCapabilities.PLAYER_JUJUTSU_DATA, (Direction) null).orElse(new JujutsuData(player));
    }

    public void syncTracking() {
        PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncJujutsuData(this));
    }

    public static class JujutsuDataProvider implements ICapabilitySerializable<Tag> {

        private JujutsuData data;
        private LazyOptional<JujutsuData> instance = LazyOptional.of(() -> {
            return this.data;
        });

        public JujutsuDataProvider(Player player) {
            this.data = new JujutsuData(player);
        }

        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == ModCapabilities.PLAYER_JUJUTSU_DATA ? this.instance.cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return data.writeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            data.readNBT(nbt);
        }
    }
}
