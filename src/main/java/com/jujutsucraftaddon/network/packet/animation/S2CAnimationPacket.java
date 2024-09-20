package com.jujutsucraftaddon.network.packet.animation;

import com.jujutsucraftaddon.client.animation.AnimationController;
import com.jujutsucraftaddon.client.animation.Animations;
import com.jujutsucraftaddon.network.Packet;
import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import static com.jujutsucraftaddon.network.packet.animation.AnimationPackets.Type.*;

public class S2CAnimationPacket extends Packet {
    public int entityID;
    public AnimationPackets.Type type;
    public CompoundTag data;

    public S2CAnimationPacket(int entityID, AnimationPackets.Type type, CompoundTag data) {
        this.entityID = entityID;
        this.type = type;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(type.ordinal());
        buf.writeNbt(data);
    }

    public S2CAnimationPacket(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        type = AnimationPackets.Type.values()[buf.readInt()];
        data = buf.readNbt();
    }

    public void handle(Player player, NetworkEvent.Context context) {
        ClientLevel world = Minecraft.getInstance().level;
        Entity entity = world.getEntity(entityID);

        if (entity == null || !(entity instanceof AbstractClientPlayer))
            return;

        AbstractClientPlayer toAnimate = (AbstractClientPlayer) entity;
        AnimationController controller = Animations.getController(toAnimate);
        controller.autoUpdate = false;

        String name = data.getString("name");
        ResourceLocation animation = name.isEmpty() ? null : new ResourceLocation(name);

        if (type == PLAY_ANIMATION) {
            controller.play(animation);
        } else if (type == REPLACE_ANIMATION) {
            int ticks = data.getInt("fadeTicks");
            Ease ease = Ease.getEase(data.getByte("ease"));
            controller.replace(animation, ticks, ease);
        } else if (type == SET_SPEED) {
            float speed = data.getFloat("speed");
            if (controller.isAnimation(animation))
                controller.setSpeed(speed);
        }

        controller.autoUpdate = true;
    }
}