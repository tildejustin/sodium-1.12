package me.jellysquid.mods.sodium.client.render.pipeline;

import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadOrientation;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorsExtended;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import java.util.List;
import java.util.Random;

public class BlockRenderer {
    private final Random random = new XoRoShiRoRandom();

    private final BlockColorsExtended blockColors;

    private final QuadLightData cachedQuadLightData = new QuadLightData();

    private final BiomeColorBlender biomeColorBlender;
    private final LightPipelineProvider lighters;

    private final boolean useAmbientOcclusion;

    public BlockRenderer(Minecraft client, LightPipelineProvider lighters, BiomeColorBlender biomeColorBlender) {
        this.blockColors = (BlockColorsExtended) client.getBlockColors();
        this.biomeColorBlender = biomeColorBlender;

        this.lighters = lighters;

        this.useAmbientOcclusion = Minecraft.isAmbientOcclusionEnabled();
    }

    public boolean renderModel(IBlockAccess world, IBlockState state, BlockPos pos, IBakedModel model, ChunkModelBuffers buffers, boolean cull, long seed) {
        LightMode mode = this.getLightingMode(state, model, world, pos);
        LightPipeline lighter = this.lighters.getLighter(mode);
        Vec3d offset = state.getOffset(world, pos);

        boolean rendered = false;

        // Use Sodium's default render path
        
        for (EnumFacing dir : DirectionUtil.ALL_DIRECTIONS) {
            this.random.setSeed(seed);

            List<BakedQuad> sided = model.getQuads(state, dir, this.random.nextLong());

            if (sided.isEmpty()) {
                continue;
            }

            if (!cull || state.shouldSideBeRendered(world, pos, dir)) {
                this.renderQuadList(world, state, pos, lighter, offset, buffers, sided, dir);

                rendered = true;
            }
        }

        this.random.setSeed(seed);

        List<BakedQuad> all = model.getQuads(state, null, this.random.nextLong());

        if (!all.isEmpty()) {
            this.renderQuadList(world, state, pos, lighter, offset, buffers, all, null);

            rendered = true;
        }

        return rendered;
    }

    private void renderQuadList(IBlockAccess world, IBlockState state, BlockPos pos, LightPipeline lighter, Vec3d offset,
                                ChunkModelBuffers buffers, List<BakedQuad> quads, EnumFacing cullFace) {
    	ModelQuadFacing facing = cullFace == null ? ModelQuadFacing.UNASSIGNED : ModelQuadFacing.fromDirection(cullFace);
        IBlockColor colorizer = null;

        ModelVertexSink sink = buffers.getSink(facing);
        sink.ensureCapacity(quads.size() * 4);

        ChunkRenderData.Builder renderData = buffers.getRenderData();

        // This is a very hot allocation, iterate over it manually
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++) {
            BakedQuad quad = quads.get(i);

            QuadLightData light = this.cachedQuadLightData;
            // TODO: Does null mean we should treat it as non-axis-aligned?
            EnumFacing quadFace = quad.getFace();
            if (quadFace == null) {
                quadFace = EnumFacing.DOWN;
            }
            lighter.calculate((ModelQuadView) quad, pos, light, cullFace, quadFace, /* quad.shouldApplyDiffuseLighting() */ false);

            if (quad.hasTintIndex() && colorizer == null) {
                colorizer = this.blockColors.getColorProvider(state);
            }

            this.renderQuad(world, state, pos, sink, offset, colorizer, quad, light, renderData);
        }

        sink.flush();
    }

    private void renderQuad(IBlockAccess world, IBlockState state, BlockPos pos, ModelVertexSink sink, Vec3d offset,
                            IBlockColor colorProvider, BakedQuad bakedQuad, QuadLightData light, ChunkRenderData.Builder renderData) {
        ModelQuadView src = (ModelQuadView) bakedQuad;

        ModelQuadOrientation order = ModelQuadOrientation.orient(light.br);

        int[] colors = null;

        if (bakedQuad.hasTintIndex()) {
            colors = this.biomeColorBlender.getColors(colorProvider, world, state, pos, src);
        }

        for (int dstIndex = 0; dstIndex < 4; dstIndex++) {
            int srcIndex = order.getVertexIndex(dstIndex);

            float x = src.getX(srcIndex) + (float) offset.x;
            float y = src.getY(srcIndex) + (float) offset.y;
            float z = src.getZ(srcIndex) + (float) offset.z;

            int color = ColorABGR.mul(colors != null ? colors[srcIndex] : src.getColor(srcIndex), light.br[srcIndex]);

            float u = src.getTexU(srcIndex);
            float v = src.getTexV(srcIndex);

            int lm = light.lm[srcIndex];

            sink.writeQuad(x, y, z, color, u, v, lm);
        }

        TextureAtlasSprite sprite = src.rubidium$getSprite();

        if (sprite != null) {
            renderData.addSprite(sprite);
        }
    }

    private LightMode getLightingMode(IBlockState state, IBakedModel model, IBlockAccess world, BlockPos pos) {
        if (this.useAmbientOcclusion && model.isAmbientOcclusion() && state.getLightValue() == 0) {
            return LightMode.SMOOTH;
        } else {
            return LightMode.FLAT;
        }
    }
}
