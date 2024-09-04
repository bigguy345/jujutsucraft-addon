package com.jujutsucraftaddon.mixin.jujutsu;

import net.mcreator.jujutsucraft.client.screens.OverlayDefaultOverlay;
import net.mcreator.jujutsucraft.procedures.OCoolTimeCombatProcedure;
import net.mcreator.jujutsucraft.procedures.OCoolTimeProcedure;
import net.mcreator.jujutsucraft.procedures.OCoolTimeSelectingProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OverlayDefaultOverlay.class)
public class CooldownGuiFix {
        @Redirect(method = "eventHandler", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 3))
        private static int removeSpeed1(GuiGraphics instance, Font p_283343_, String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_, RenderGuiEvent.Pre event) {
            Player entity = Minecraft.getInstance().player;
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            String nah = "";
            if(!OCoolTimeSelectingProcedure.execute(entity).equals("")){
                int cuh = Integer.parseInt(OCoolTimeSelectingProcedure.execute(entity))/2;
                nah = String.valueOf(cuh);
            }

            return event.getGuiGraphics().drawString(Minecraft.getInstance().font, nah, w / 2 + -203, h / 2 + 95, -205, false);

        }
        @Redirect(method = "eventHandler", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 4))
        private static int removeSpeed2(GuiGraphics instance, Font p_283343_, String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_, RenderGuiEvent.Pre event) {
            Player entity = Minecraft.getInstance().player;
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            String nah = "";
            if(!OCoolTimeCombatProcedure.execute(entity).equals("")){
                int cuh = Integer.parseInt(OCoolTimeCombatProcedure.execute(entity))/2;
                nah = String.valueOf(cuh);
            }
            return event.getGuiGraphics().drawString(Minecraft.getInstance().font,nah, w / 2 + -208, h / 2 + 107, -205, false);

        }
        @Redirect(method = "eventHandler", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 5), remap = false)
        private static int removeSpeed3(GuiGraphics instance, Font p_283343_, String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_, RenderGuiEvent.Pre event) {
            Player entity = Minecraft.getInstance().player;
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            String nah = "";
            if(!OCoolTimeProcedure.execute(entity).equals("")){
                int cuh = Integer.parseInt(OCoolTimeProcedure.execute(entity))/2;
                nah = String.valueOf(cuh);
            }
            return event.getGuiGraphics().drawString(Minecraft.getInstance().font, nah, w / 2 + -194, h / 2 + 107, -205, false);
        }
    }
