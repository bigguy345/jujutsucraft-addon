package com.jujutsucraftaddon.utility;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

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
}
