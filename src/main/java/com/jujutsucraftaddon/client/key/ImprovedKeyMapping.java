package com.jujutsucraftaddon.client.key;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class ImprovedKeyMapping extends KeyMapping {
    public static final Map<String, ImprovedKeyMapping> ALL = Maps.newHashMap();
    public boolean isDown;

    public static Minecraft mc = Minecraft.getInstance();

    public ImprovedKeyMapping(String p_90825_, InputConstants.Type p_90826_, int p_90827_, String p_90828_) {
        super(p_90825_, p_90826_, p_90827_, p_90828_);
        ALL.put(p_90825_, this);
    }

    public ImprovedKeyMapping(String p_90821_, int p_90822_, String p_90823_) {
        this(p_90821_, InputConstants.Type.KEYSYM, p_90822_, p_90823_);
    }

    public void setDown(boolean p_90846_) {
        if (p_90846_ && !isDown)
            onAction(GLFW.GLFW_PRESS);
        if (!p_90846_ && isDown)
            onAction(GLFW.GLFW_RELEASE);

        this.isDown = p_90846_;
    }

    public boolean isDown() {
        return this.isDown && isConflictContextAndModifierActive();
    }

    public void onAction(int action) {
        if (action == GLFW.GLFW_PRESS)
            onPress();

        if (action == GLFW.GLFW_REPEAT)
            onHeld();

        if (action == GLFW.GLFW_RELEASE)
            onRelease();
    }

    public void onHeld() {
    }

    public void onTick() {
    }

    public void onPress() {
    }

    public void onRelease() {
    }

    public static void tick() {
        for (ImprovedKeyMapping keymapping : ALL.values()) {
            if (keymapping.isDown())
                keymapping.onAction(GLFW.GLFW_REPEAT);
            
            keymapping.onTick();
        }
    }
}
