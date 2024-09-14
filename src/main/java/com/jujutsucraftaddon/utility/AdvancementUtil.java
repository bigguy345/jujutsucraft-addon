package com.jujutsucraftaddon.utility;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtil {

    public static boolean grantAdvancement(ServerPlayer player, String advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(advancementId));
        if (advancement == null)
            return false;


        AdvancementProgress playerProgress = player.getAdvancements().getOrStartProgress(advancement);
        if (!playerProgress.isDone())
            for (String criterion : playerProgress.getRemainingCriteria())
                player.getAdvancements().award(advancement, criterion);


        return playerProgress.isDone();
    }

    public static boolean revokeAdvancement(ServerPlayer player, String advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;


        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(advancementId));
        if (advancement == null)
            return false;


        AdvancementProgress playerProgress = player.getAdvancements().getOrStartProgress(advancement);
        if (!playerProgress.isDone())
            for (String criterion : playerProgress.getCompletedCriteria())
                player.getAdvancements().revoke(advancement, criterion);
        

        return playerProgress.isDone();
    }

    public static boolean isDone(ServerPlayer player, String advancementId) {
        MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(advancementId));
        if (advancement == null)
            return false;


        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        return progress.isDone();
    }
}
