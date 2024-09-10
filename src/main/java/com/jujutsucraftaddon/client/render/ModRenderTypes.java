package com.jujutsucraftaddon.client.render;

import com.jujutsucraftaddon.client.shader.ModShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.client.resources.model.ModelBakery.BREAKING_LOCATIONS;

public class ModRenderTypes extends RenderStateShard {
    
    public ModRenderTypes(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        super(p_110161_, p_110162_, p_110163_);
    }

    private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize((p_286174_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_286174_, false, false);
        return RenderType.create("crumbling", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(ModShaders.RENDERTYPE_CRUMBLING_SHADER).setTextureState(renderstateshard$texturestateshard).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
    });
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(ModRenderTypes::crumbling).collect(Collectors.toList());

    public static RenderType crumbling(ResourceLocation p_110495_) {
        return CRUMBLING.apply(p_110495_); 
    }
}



