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

public class JujutsuData {
    public Player player;
    public float blackFlashChance = -1;

    public JujutsuData() {
    }

    public JujutsuData(Player player) {
        this.player = player;
    }

    public Tag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("blackFlashChance", blackFlashChance);
        return nbt;
    }

    public void readNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        blackFlashChance = nbt.getFloat("blackFlashChance");
    }

    public static JujutsuData get(Player player) {
        return player.getCapability(ModCapabilities.PLAYER_JUJUTSU_DATA, (Direction) null).orElse(new JujutsuData(player));
    }

    public void syncTracking() {
        PacketHandler.sendToTracking(player, new SyncJujutsuData(this,player.getId()));
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
