package com.jujutsucraftaddon.capabilities.data;

import com.jujutsucraftaddon.Config;
import com.jujutsucraftaddon.capabilities.ModCapabilities;
import com.jujutsucraftaddon.effects.IMobEffectInstance;
import com.jujutsucraftaddon.network.PacketHandler;
import com.jujutsucraftaddon.network.packet.SyncJujutsuData;
import com.jujutsucraftaddon.skill.DashSkill;
import com.jujutsucraftaddon.utility.AdvancementUtil;
import com.jujutsucraftaddon.utility.ValueUtil;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class JujutsuData {
    public Player player;
    public JujutsucraftModVariables.PlayerVariables data;
    public Levels levels = new Levels(this);
    public Cooldowns cooldowns = new Cooldowns(this);

    public float blackFlashChance = -1, blackFlashDamageMulti = 4;
    public boolean landedFirstBlackFlash, canHealOthers, infinityOn;
    public int toHealID = -1;

    public DashSkill currentDash;

    public JujutsuData() {
    }

    public JujutsuData(Player player) {
        this.player = player;
        data = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(null);
    }

    public void tick() {
        cooldowns.tick();

        if (currentDash != null)
            currentDash.tick(this);

        if (player.tickCount % 10 == 0)
            JujutsuData.get(player).syncTracking();
    }

    public Tag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("levels", levels.writeNBT());
        nbt.put("cooldowns", cooldowns.writeNBT());

        nbt.putFloat("blackFlashChance", blackFlashChance);
        nbt.putFloat("blackFlashMulti", blackFlashDamageMulti);
        nbt.putBoolean("landedFirstBlackFlash", landedFirstBlackFlash);

        if (!player.level().isClientSide()) {
            canHealOthers = AdvancementUtil.isDone((ServerPlayer) player, "jujutsucraftaddon:rct_others");
            infinityOn = player.getPersistentData().getBoolean("infinity");
        }
        nbt.putBoolean("canHealOthers", canHealOthers);
        nbt.putInt("toHealID", toHealID);
        nbt.putBoolean("infinityOn", infinityOn);


        return nbt;
    }

    public void readNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        blackFlashChance = nbt.getFloat("blackFlashChance");
        blackFlashDamageMulti = nbt.getFloat("blackFlashMulti");
        landedFirstBlackFlash = nbt.getBoolean("landedFirstBlackFlash");
        canHealOthers = nbt.getBoolean("canHealOthers");
        infinityOn = nbt.getBoolean("infinityOn");
        toHealID = nbt.getInt("toHealID");
        levels.readNBT(nbt.getCompound("levels"));
        cooldowns.readNBT(nbt.getCompound("cooldowns"));
    }

    public boolean tamedMahoraga(boolean isNotSummoned) {
        if (player instanceof ServerPlayer player)
            return data.PlayerCurseTechnique == 6 && player.getPersistentData().getDouble("TenShadowsTechnique14") > (isNotSummoned ? -1 : -2) && player.getAdvancements().getOrStartProgress(player.server.getAdvancements().getAdvancement(new ResourceLocation("jujutsucraft:skill_mahoraga"))).isDone();

        return false;
    }

    public Entity getHealingEntity() {
        if (toHealID == -1)
            return null;
        return player.level().getEntity(toHealID);
    }

    public static JujutsuData get(Player player) {
        return player.getCapability(ModCapabilities.PLAYER_JUJUTSU_DATA, (Direction) null).orElse(new JujutsuData(player));
    }

    public JujutsucraftModVariables.PlayerVariables getPlayerVariables() {
        return data = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new JujutsucraftModVariables.PlayerVariables());
    }

    public void syncTracking() {
        if (data == null)
            data = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new JujutsucraftModVariables.PlayerVariables());

        PacketHandler.sendToTracking(player, new SyncJujutsuData(this, player.getId()));
    }

    public static class JujutsuDataProvider implements ICapabilitySerializable<Tag> {

        private JujutsuData data;
        private LazyOptional<JujutsuData> instance = LazyOptional.of(() -> {
            return this.data;
        });

        public JujutsuDataProvider(Player player) {
            this.data = new JujutsuData(player);
        }

        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == ModCapabilities.PLAYER_JUJUTSU_DATA ? this.instance.cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return data.writeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            data.readNBT(nbt);
        }
    }

    public static class Cooldowns {
        public JujutsuData parent;
        public int DASH;

        public Cooldowns(JujutsuData parent) {
            this.parent = parent;
        }

        public void tick() {
            if (DASH > 0)
                DASH--;
        }

        public Tag writeNBT() {
            CompoundTag levels = new CompoundTag();
            levels.putInt("dash", DASH);
            return levels;
        }

        public void readNBT(Tag tag) {
            CompoundTag nbt = (CompoundTag) tag;
            DASH = nbt.getInt("dash");
        }
    }

    public static class Levels {
        public JujutsuData parent;
        private float RCT, DASH;

        public Levels(JujutsuData parent) {
            this.parent = parent;
        }

        public float getFatigue() {
            float fatigue = 0;

            MobEffectInstance effect = parent.player.getEffect(JujutsucraftModMobEffects.FATIGUE.get());
            if (effect != null) {
                fatigue = effect.getDuration();
            }

            return 1 + Math.min(Math.max(fatigue, 0) / 600, 10) * 0.4f;
        }

        public void incrementDashLevel(float amount) {
            DASH = (float) ValueUtil.clamp(DASH + amount, 0, Config.DASH_MAX_LEVEL.get());
        }

        public void incrementRCTLevel(float amount) {
            RCT = (float) ValueUtil.clamp(RCT + amount, 0, Config.MAX_RCT_LEVEL.get());
            //Every 10 levels, execute once
            if (Math.round(RCT) % 10 == 0) {
                float chance = RCT < 0.5f ? 0.2f : RCT;
                if (Math.random() <= Math.min(chance, 0.5f)) {
                    AdvancementUtil.grantAdvancement((ServerPlayer) parent.player, "jujutsucraftaddon:rct_others");
                    parent.canHealOthers = true;
                }
                RCT += 0.5f;
            }
        }

        public void incrementFatigue(int ticks) {
            MobEffectInstance fatigue = parent.player.getEffect(JujutsucraftModMobEffects.FATIGUE.get());
            if (fatigue != null)
                ((IMobEffectInstance) fatigue).setDuration(Math.min(fatigue.getDuration() + ticks, 6000)).updateClient(parent.player);
            else
                parent.player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.FATIGUE.get(), ticks, 0, false, false));
        }

        public int getRCTAmplifier() {
            int zoneAmp = 0;
            MobEffectInstance zone = parent.player.getEffect(JujutsucraftModMobEffects.ZONE.get());
            if (zone != null)
                zoneAmp = (int) (zone.getAmplifier() * 1.5f);

            return (int) ValueUtil.lerp(0, Config.MAX_RCT_LEVEL_AMPLIFIER.get(), RCT / Config.MAX_RCT_LEVEL.get()) + zoneAmp;
        }

        public float getRCTLevel() {
            return RCT;
        }

        public float getDashLevel() {
            return DASH;
        }

        public Tag writeNBT() {
            CompoundTag levels = new CompoundTag();
            levels.putFloat("rct", RCT);
            levels.putFloat("dash", DASH);
            return levels;
        }

        public void readNBT(Tag tag) {
            CompoundTag nbt = (CompoundTag) tag;
            RCT = nbt.getFloat("rct");
            DASH = nbt.getFloat("dash");
        }
    }
}
