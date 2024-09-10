package com.jujutsucraftaddon.client.render;

import com.jujutsucraftaddon.network.packet.BarrierBreakProgessPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

    public static void renderBarrierBreaking(PoseStack pose, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 vec3 = camera.getPosition();
        Iterator<Map.Entry<BlockPos, Byte>> iterator = BarrierBreakProgessPacket.TRACKED_BARRIERS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Byte> entry = iterator.next();

            BlockPos pos = entry.getKey();
            byte progress = entry.getValue();
            BlockState blockstate = mc.level.getBlockState(pos);
            if (blockstate.isAir() || progress < 0 || progress > 9) {
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
