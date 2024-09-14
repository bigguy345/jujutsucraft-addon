package com.jujutsucraftaddon.utility;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Utility {

    public static void displayTitle(ServerPlayer player, String titleText, String subtitleText, int stay, int fadeIn, int fadeOut) {
        CommandSourceStack sourceStack = player.createCommandSourceStack().withSuppressedOutput();

        String titleCommand = String.format("title %s title %s", player.getName().getString(), titleText);

        Commands commands = player.getServer().getCommands();
        commands.performPrefixedCommand(sourceStack, titleCommand);
        commands.performPrefixedCommand(sourceStack, String.format("title %s times %d %d %d", player.getName().getString(), fadeIn, stay, fadeOut));

        if (!subtitleText.isEmpty())
            commands.performPrefixedCommand(sourceStack, String.format("title %s subtitle {text:%s}", player.getName().getString(), subtitleText));
    }

    public static Entity raytraceEntity(Entity traceFrom, double distance) {
        HitResult raycast = ProjectileUtil.getHitResultOnViewVector(traceFrom, (p_281111_) -> !p_281111_.isSpectator() && p_281111_.isPickable(), distance);
        if (raycast instanceof EntityHitResult entityray)
            return entityray.getEntity();
        else
            return null;
    }

    public static BlockPos rayTraceBlock(Entity traceFrom, double distance) {
        HitResult raycast = ProjectileUtil.getHitResultOnViewVector(traceFrom, (p_281111_) -> !p_281111_.isSpectator() && p_281111_.isPickable(), distance);
        if (raycast instanceof BlockHitResult blockray)
            return blockray.getBlockPos();
        else
            return null;
    }
}
