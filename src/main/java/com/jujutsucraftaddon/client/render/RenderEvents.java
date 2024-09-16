package com.jujutsucraftaddon.client.render;

import com.jujutsucraftaddon.network.packet.BarrierBreakProgessPacket;
import com.jujutsucraftaddon.skill.DashSkill;
import com.jujutsucraftaddon.utility.JujuUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.Map;

public class RenderEvents {

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            renderBarrierBreaking(event.getPoseStack(), event.getCamera());
    }

    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (DashSkill.DASH_CHARGE > 0 && event.getOverlay().id().toString().equals("minecraft:experience_bar"))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (DashSkill.DASH_CHARGE > 0) {
            Minecraft mc = Minecraft.getInstance();
            GuiGraphics graphics = event.getGuiGraphics();
            ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
            float f = DashSkill.DASH_CHARGE;
            int i = 182;
            int j = (int) (f * 183.0F);
            int y = graphics.guiHeight() - 32 + 3;
            int x = graphics.guiWidth() / 2 - 91;
            graphics.blit(GUI_ICONS_LOCATION, x, y, 0, 84, 182, 5);
            // if (p_282774_.getJumpCooldown() > 0) {
            graphics.blit(GUI_ICONS_LOCATION, x, y, 0, 74, 182, 5);
            // } else if (j > 0) {
           graphics.blit(GUI_ICONS_LOCATION, x, y, 0, 89, j, 5);
            // }}


            MutableComponent energy = Component.translatable("message.dash_energy_consumed").append(Component.literal((int) DashSkill.ENERGY_NEEDED + "").withStyle(style -> style.withColor(0xFFFF00)));// Red color
            int w = mc.font.width(energy.getString());

            if (f >= 1)
                energy.append(Component.literal(" (").append(Component.translatable("message.dash_fully_charged")).append(Component.literal(")")).withStyle(style -> style.withColor(0x00FF00)));
            else if (DashSkill.OUT_OF_ENERGY)
                energy.append(Component.literal(" (").append(Component.translatable("message.dash_out_of_energy")).append(Component.literal(")")).withStyle(style -> style.withColor(0xFF0000)));

            graphics.drawString(mc.font, energy, graphics.guiWidth() / 2 - w / 2, y - 50, 0xffffff);
        }
    }

    public static void renderBarrierBreaking(PoseStack pose, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 vec3 = camera.getPosition();
        Iterator<Map.Entry<BlockPos, Byte>> iterator = BarrierBreakProgessPacket.TRACKED_BARRIERS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Byte> entry = iterator.next();

            BlockPos pos = entry.getKey();
            byte progress = entry.getValue();
            BlockState blockstate = mc.level.getBlockState(pos);
            if (blockstate.isAir() || progress < 0 || progress > 9 || !JujuUtil.isBarrier(mc.level, pos)) {
                iterator.remove();
                continue;
            }

            double interpX = (double) pos.getX() - vec3.x();
            double interpY = (double) pos.getY() - vec3.y();
            double interpZ = (double) pos.getZ() - vec3.z();
            pose.pushPose();
            pose.translate(interpX, interpY, interpZ);
            PoseStack.Pose posestack$pose1 = pose.last();
            VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(mc.renderBuffers().crumblingBufferSource().getBuffer(ModRenderTypes.DESTROY_TYPES.get(progress)), posestack$pose1.pose(), posestack$pose1.normal(), 1.0F);

            net.minecraftforge.client.model.data.ModelData modelData = mc.level.getModelDataManager().getAt(pos);
            mc.getBlockRenderer().renderBreakingTexture(blockstate, pos, mc.level, pose, vertexconsumer1, modelData == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : modelData);
            pose.popPose();
        }
    }
}
