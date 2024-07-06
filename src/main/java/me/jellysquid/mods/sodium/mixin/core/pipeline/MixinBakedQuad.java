package me.jellysquid.mods.sodium.mixin.core.pipeline;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import me.jellysquid.mods.sodium.client.render.vertex.VertexFormatDescription;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedQuad.class)
public class MixinBakedQuad implements ModelQuadView {

    @Shadow
    @Final
    protected TextureAtlasSprite sprite;

    @Shadow
    @Final
    protected int tintIndex;

    @Shadow public int[] getVertexData() {
        throw new AssertionError();
    }

    protected int cachedFlags;

    private VertexFormatDescription formatDescription;

    // unsure what to do about this
    protected final VertexFormat format = DefaultVertexFormats.ITEM;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(int[] vertexData, int colorIndex, EnumFacing face, TextureAtlasSprite sprite, CallbackInfo ci) {
        this.formatDescription = VertexFormatDescription.get(format);
        this.cachedFlags = ModelQuadFlags.getQuadFlags((BakedQuad) (Object) this);
    }

    private int vertexOffset(int idx) {
        return idx * this.format.getIntegerSize();
    }

    @Override
    public float getX(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex]);
    }

    @Override
    public float getY(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex + 1]);
    }

    @Override
    public float getZ(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex + 2]);
    }

    @Override
    public int getColor(int idx) {
        int colorIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.COLOR);
        if (colorIndex == -1) {
            return 0;
        }
        return this.getVertexData()[vertexOffset(idx) + colorIndex];
    }

    @Override
    public TextureAtlasSprite rubidium$getSprite() {
        return this.sprite;
    }

    @Override
    public float getTexU(int idx) {
        int textureIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.TEXTURE);
        if (textureIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + textureIndex]);
    }

    @Override
    public float getTexV(int idx) {
        int textureIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.TEXTURE);
        if (textureIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + textureIndex + 1]);
    }

    @Override
    public int getFlags() {
        return this.cachedFlags;
    }

    @Override
    public int getNormal(int idx) {
        int normalIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.NORMAL);
        if (normalIndex == -1) {
            return 0;
        }
        return this.getVertexData()[vertexOffset(idx) + normalIndex];
    }

    @Override
    public int getColorIndex() {
        return this.tintIndex;
    }
}
