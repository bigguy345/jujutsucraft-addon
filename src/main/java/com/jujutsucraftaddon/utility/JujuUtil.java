package com.jujutsucraftaddon.utility;

import net.mcreator.jujutsucraft.entity.DomainExpansionEntityEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class JujuUtil {

    public static LivingEntity getDomainCaster(Level world, BlockPos block) {
        return getDomainCaster(world, world.getBlockEntity(block));
    }

    public static LivingEntity getDomainCaster(Level world, BlockEntity blockEntity) {
        if (blockEntity != null && blockEntity.getPersistentData().get("caster") != null) {
            Entity entity = ((ServerLevel) world).getEntity(UUID.fromString(blockEntity.getPersistentData().getString("caster")));
            return entity instanceof LivingEntity living ? living : null;
        }

        return null;
    }

    public static LivingEntity getDomainCaster(Level world, DomainExpansionEntityEntity domainEntity) {
        if (domainEntity != null && domainEntity.getPersistentData().get("caster") != null) {
            Entity entity = ((ServerLevel) world).getEntity(UUID.fromString(domainEntity.getPersistentData().getString("caster")));
            return entity instanceof LivingEntity living ? living : null;
        }

        return null;
    }

    public static void breakInfinity(LivingEntity gojo, int durationTicks) {
        gojo.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.UNSTABLE.get(), durationTicks + 10, 0, false, false));
        gojo.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.CANCEL_CURSED_TECHNIQUE.get(), durationTicks, 0, false, false));
    }

    public static DamageSource getCurseDamageSource(LivingEntity entity) {
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jujutsucraft:damage_curse"))), entity);
    }

    public static boolean isBarrier(Level world, BlockPos block) {
        return world.getBlockState(block).is(BlockTags.create(new ResourceLocation("jujutsucraft:barrier")));
    }

    public static List<LivingEntity> getEntitiesWithinDomain(LivingEntity caster) {
        MobEffectInstance domain = caster.getEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
        if (domain == null)
            return null;

        Vec3 center = new Vec3(caster.getPersistentData().getDouble("x_pos_doma"), caster.getPersistentData().getDouble("y_pos_doma"), caster.getPersistentData().getDouble("z_pos_doma"));
        double radius = JujutsucraftModVariables.MapVariables.get(caster.level()).DomainExpansionRadius * (domain.getAmplifier() > 0 ? 9 : 1) - 1;
        List entities = BlockUtil.getEntitiesWithinSphere(caster.level(), center, radius);
        return entities;
    }

    public static boolean isWithinDomain(LivingEntity caster, LivingEntity within) {
        List entites = getEntitiesWithinDomain(caster);
        return entites != null && entites.contains(within);
    }
}
