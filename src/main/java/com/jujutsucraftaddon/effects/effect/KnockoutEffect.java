package com.jujutsucraftaddon.effects.effect;

import com.jujutsucraftaddon.capabilities.data.JujutsuData;
import com.jujutsucraftaddon.effects.ImprovedMobEffect;
import com.jujutsucraftaddon.utility.Utility;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.ResetCounterProcedure;
import net.mcreator.jujutsucraft.procedures.SummonMahoragaProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;

public class KnockoutEffect extends ImprovedMobEffect {
    public KnockoutEffect(MobEffectCategory category, int color) {
        super(category, color);
        updateClient = true;
    }

    public void onInstancedAdded(LivingEntity entity, MobEffectInstance instance) {
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, instance.getDuration() + 20, 99, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.UNSTABLE.get(), instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.COOLDOWN_TIME.get(), instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.COOLDOWN_TIME_COMBAT.get(), instance.getDuration(), 99, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, instance.getDuration() * 2, 99, false, false));
        if (entity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()))
            entity.removeEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());

        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        boolean wearingWheel = helmet.getItem() == JujutsucraftModItems.MAHORAGA_WHEEL_HELMET.get();

        if (entity instanceof ServerPlayer player) {
            Utility.displayTitle(player, "{\"text\":\"" + Component.translatable("title.jujutsucraftaddon.knocked_out").getString() + "\",\"color\":\"dark_red\",\"bold\":true}}", "", 70, 10, 10);
            player.playNotifySound(SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0f, 1.0f);
            player.displayClientMessage(Component.translatable("message.jujutsucraftaddon.knocked_out").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)).withBold(true)), false);

            if (JujutsuData.get(player).tamedMahoraga(true) && wearingWheel) {
                SummonMahoragaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
            }
        } else if (wearingWheel && entity.getPersistentData().getDouble("TenShadowsTechnique14") > 0) {
            ResetCounterProcedure.execute(entity);
            SummonMahoragaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
        }
        super.onInstancedAdded(entity, instance);
    }

    public void applyEffectTick(LivingEntity entity, MobEffectInstance instance) {
        entity.setXRot(70);
    }

    public void onInstanceRemove(LivingEntity entity, MobEffectInstance instance) {
        if (entity instanceof Monster monster)
            monster.setNoAi(false);

        entity.setXRot(0);
        super.onInstanceRemove(entity, instance);
    }
}
