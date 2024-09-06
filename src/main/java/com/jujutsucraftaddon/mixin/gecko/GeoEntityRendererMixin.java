package com.jujutsucraftaddon.mixin.gecko;

import com.jujutsucraftaddon.effects.ModEffects;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(value = GeoEntityRenderer.class)
public class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> extends EntityRenderer<T> {

    protected GeoEntityRendererMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Redirect(method = "actuallyRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;speed(F)F"))
    private float removeBaseModBrainDamageHeal1(WalkAnimationState instance, float partialTick, @Local(name = "livingEntity") LocalRef<LivingEntity> livingEntity) {
        LivingEntity entity = livingEntity.get();
        if (entity.hasEffect(ModEffects.KNOCKOUT_EFFECT.get()))
            return 0;

        return instance.speed(partialTick);
    }

    @Shadow
    public ResourceLocation getTextureLocation(T p_114482_) {
        return null;
    }
}