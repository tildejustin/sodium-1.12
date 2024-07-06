package org.embeddedt.embeddium.render.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.jellysquid.mods.sodium.mixin.features.chunk_rendering.AccessorBlockFluidRenderer;
import me.jellysquid.mods.sodium.mixin.features.chunk_rendering.AccessorBlockRenderDispatcher;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class EmbeddiumFluidSpriteCache {
    // Cache the sprites array to avoid reallocating it on every call
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[3];
    private final Object2ObjectOpenHashMap<ResourceLocation, TextureAtlasSprite> spriteCache = new Object2ObjectOpenHashMap<>();
    private final TextureAtlasSprite[] waterOverride, lavaOverride;

    public EmbeddiumFluidSpriteCache() {
        AccessorBlockFluidRenderer fluidRenderer = (AccessorBlockFluidRenderer)((AccessorBlockRenderDispatcher)Minecraft.getMinecraft().getBlockRendererDispatcher()).getFluidRenderer();
        waterOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] water = fluidRenderer.getAtlasSpritesWater();
        waterOverride[0] = water[0];
        waterOverride[1] = water[1];
        waterOverride[2] = fluidRenderer.getAtlasSpriteWaterOverlay();
        // Construct array with 3 elements, vanilla only has 2
        lavaOverride = new TextureAtlasSprite[3];
        TextureAtlasSprite[] lava = fluidRenderer.getAtlasSpritesLava();
        lavaOverride[0] = lava[0];
        lavaOverride[1] = lava[1];
    }

    private TextureAtlasSprite getTexture(ResourceLocation identifier) {
        TextureAtlasSprite sprite = spriteCache.get(identifier);

        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(identifier.toString());;
            spriteCache.put(identifier, sprite);
        }

        return sprite;
    }

    public TextureAtlasSprite[] getSprites(BlockLiquid fluid) {
        if (fluid == Blocks.WATER || fluid == Blocks.FLOWING_WATER) {
            return waterOverride;
        } else if (fluid == Blocks.LAVA || fluid == Blocks.FLOWING_LAVA) {
            return lavaOverride;
        }
        throw new RuntimeException("unimplemented");
        // sprites[0] = getTexture(fluid.getStill());
        // sprites[1] = getTexture(fluid.getFlowing());
        // ResourceLocation overlay = fluid.getOverlay();
        // sprites[2] = overlay != null ? getTexture(overlay) : null;
        // return sprites;
    }
}
