package com.jujutsucraftaddon.network.packet.animation;

import com.jujutsucraftaddon.network.PacketHandler;
import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AnimationPackets {
    public enum Type {
        PLAY_ANIMATION, REPLACE_ANIMATION, SET_SPEED
    }

    public static CompoundTag generatePlayData(ResourceLocation animation) {
        CompoundTag data = new CompoundTag();
        data.putString("name", animation == null ? "" : animation.toString());

        return data;
    }

    public static CompoundTag generateReplaceData(ResourceLocation animation, int fadeTicks, Ease ease) {
        CompoundTag data = new CompoundTag();
        data.putString("name", animation == null ? "" : animation.toString());
        data.putInt("fadeTicks", fadeTicks);
        data.putByte("ease", ease.getId());

        return data;
    }

    public static CompoundTag generateSpeedData(ResourceLocation animation, float speed) {
        CompoundTag data = new CompoundTag();
        data.putString("name", animation == null ? "" : animation.toString());
        data.putFloat("speed", speed);

        return data;
    }
}
