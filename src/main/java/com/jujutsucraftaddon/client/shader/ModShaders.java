package com.jujutsucraftaddon.client.shader;

import com.jujutsucraftaddon.Main;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ModShaders {

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_CRUMBLING_SHADER = new RenderStateShard.ShaderStateShard(ModShaders::getRendertypeCrumblingShader);

    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Main.MODID, "rendertype_crumbling"), DefaultVertexFormat.BLOCK), (p_172645_) -> {
            rendertypeBlockCrumbling = p_172645_;
        });
    }

    @Nullable
    public static ShaderInstance rendertypeBlockCrumbling;

    @Nullable
    public static ShaderInstance getRendertypeCrumblingShader() {
        return rendertypeBlockCrumbling;
    }
}
