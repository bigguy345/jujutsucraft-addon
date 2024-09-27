package com.jujutsucraftaddon.Items;

import com.jujutsucraftaddon.effects.ModEffects;
import net.mcreator.jujutsucraft.entity.*;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DomainExpansionStickItem extends Item {
    public DomainExpansionStickItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide())
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        HitResult raycast = ProjectileUtil.getHitResultOnViewVector(player, (p_281111_) -> !p_281111_.isSpectator() && p_281111_.isPickable(), 100);
     //   if (player.isShiftKeyDown())
          //  player.addEffect(new MobEffectInstance(ModEffects.KNOCKOUT_EFFECT.get(), 20*3, 99, false, false));
        
        if (raycast instanceof EntityHitResult entityray) {
            Entity entity = entityray.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                ((LivingEntity) entity).setHealth(10);
                double skill = 0;
                if (entity instanceof SukunaEntity || entity instanceof SukunaPerfectEntity || entity instanceof SukunaFushiguroEntity)
                    skill = 120;
                else if (entity instanceof GojoSatoruEntity)
                    skill = 220;
                else if (entity instanceof FushiguroMegumiEntity || entity instanceof FushiguroMegumiShibuyaEntity)
                    skill = 620;
                else if (entity instanceof JogoEntity)
                    skill = 420;
                else if (entity instanceof Dagon1Entity || entity instanceof Dagon2Entity)
                    skill = 820;
                else if (entity instanceof HanamiEntity)
                    skill = 1420;
                else if (entity instanceof HigurumaHiromiEntity)
                    skill = 2720;
                else if (entity instanceof HakariKinjiEntity)
                    skill = 2920;

                if (!player.isShiftKeyDown())
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.KNOCKOUT_EFFECT.get(), 850, 99, false, false));

                if (!((LivingEntity) entity).hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())) {
                  //  if (entity instanceof Mob mob)
                     //   mob.setTarget(player);
                   // livingEntity.getPersistentData().putDouble("skill", skill);
                   // livingEntity.getPersistentData().putBoolean("FORCE_DOMAIN", true);
                   // livingEntity.getPersistentData().putInt("DOMAIN_TIME", 20 * 1000);
                   // livingEntity.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get(), Integer.MAX_VALUE, 0, false, false));
                } else
                    livingEntity.removeEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());

                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }
}
 