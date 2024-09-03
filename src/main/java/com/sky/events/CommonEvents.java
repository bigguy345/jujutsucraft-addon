package com.sky.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import static com.sky.Main.TOGGLE_TNT_PLACEMENT;
import static com.sky.Main.cooldownTicks;

// Events that fire on both client and server side
public class CommonEvents {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Abilities abilities = player.getAbilities();
        if (!abilities.mayfly)
            abilities.mayfly = true;

        //If this event is firing on the client side, cancel it. Ensures everything after this line fires only on server side
        if (event.side == LogicalSide.CLIENT)
            return;

        cooldownTicks++;
        Level world = event.player.level();
        BlockPos underPlayer = player.blockPosition().below(); // The block directly beneath the player
        BlockState blockState = world.getBlockState(underPlayer);
        Block block = blockState.getBlock();

        int cooldownSeconds = 5;
        if (TOGGLE_TNT_PLACEMENT && cooldownTicks > 20 * cooldownSeconds && block != Blocks.AIR && block != Blocks.WATER && block != Blocks.BEDROCK)
            world.setBlock(underPlayer, Blocks.TNT.defaultBlockState(), 3);
    }

    public void fallEvent(LivingEntity entity, float distance) {
        //If this event is firing on the client side, cancel it. Ensures everything after this line fires only on server side
        if (entity.level().isClientSide())
            return;

        // Check if the entity is a player
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Get the block the player landed on
            BlockPos blockPos = player.blockPosition().below(); // The block directly beneath the player
            Level world = player.level();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (block == Blocks.TNT) {
                TntBlock tnt = (TntBlock) block;
                tnt.onCaughtFire(blockState, world, blockPos, null, player); //detonates it
                world.removeBlock(blockPos, false);
                cooldownTicks = 0;
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        fallEvent(event.getEntity(), event.getDistance());
    }

    @SubscribeEvent
    public void onLivingFall(PlayerFlyableFallEvent event) {
        fallEvent(event.getEntity(), event.getDistance());
    }
}
