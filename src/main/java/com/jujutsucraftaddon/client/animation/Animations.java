package com.jujutsucraftaddon.client.animation;

import com.jujutsucraftaddon.Main;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Animations {
    public static ResourceLocation REGISTRY = new ResourceLocation(Main.MODID, "animation_factory");

    public static ResourceLocation SUPER_DASH = new ResourceLocation(Main.MODID, "super_dash");
    public static ResourceLocation DASH_LEFT = new ResourceLocation(Main.MODID, "dash_left");
    public static ResourceLocation DASH_RIGHT = new ResourceLocation(Main.MODID, "dash_right");

    public static void setup() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(REGISTRY, 42, Animations::registerPlayerAnimation);
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new AnimationController<>(player);
    }

    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getInstance().level.getPlayerByUUID(event.getSender());

        if (player == null)
            return;

//        AnimationController animController = getController(player);
//        if (event.getMessage().getString().contains("waving"))
//            animController.play(new ResourceLocation(Main.MODID, "waving_orig"));
    }

    public static AnimationController<IAnimation> getController(AbstractClientPlayer player) {
        return (AnimationController<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(REGISTRY);
    }

    public static KeyframeAnimationPlayer createAnimationPlayer(ResourceLocation name) {
        if (name == null)
            return null;

        KeyframeAnimation animation = PlayerAnimationRegistry.getAnimation(name);
        if (animation == null)
            return null;
        
        return new KeyframeAnimationPlayer(animation);
    }

    public static AbstractFadeModifier fade(int ticks, Ease ease) {
        return AbstractFadeModifier.standardFadeIn(ticks, ease);
    }
}
