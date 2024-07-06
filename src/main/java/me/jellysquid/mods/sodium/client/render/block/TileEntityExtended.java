package me.jellysquid.mods.sodium.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class TileEntityExtended {
    /**
     * Sometimes default render bounding box: infinite in scope. Used to control rendering on {@link TileEntitySpecialRenderer}.
     */
    public static final net.minecraft.util.math.AxisAlignedBB INFINITE_EXTENT_AABB = new net.minecraft.util.math.AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    /**
     * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    public static net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox(Block type, BlockPos pos, World world)
    {
        net.minecraft.util.math.AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        if (type == Blocks.ENCHANTING_TABLE)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos, pos.add(1, 1, 1));
        }
        else if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST)
        {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
        }
        else if (type == Blocks.STRUCTURE_BLOCK)
        {
            bb = INFINITE_EXTENT_AABB;
        }
        else if (type != null && type != Blocks.BEACON)
        {
            net.minecraft.util.math.AxisAlignedBB cbb = null;
            try
            {
                cbb = world.getBlockState(pos).getCollisionBoundingBox(world, pos).offset(pos);
            }
            catch (Exception e)
            {
                // We have to capture any exceptions that may occur here because BUKKIT servers like to send
                // the tile entity data BEFORE the chunk data, you know, the OPPOSITE of what vanilla does!
                // So we can not GARENTEE that the world state is the real state for the block...
                // So, once again in the long line of US having to accommodate BUKKIT breaking things,
                // here it is, assume that the TE is only 1 cubic block. Problem with this is that it may
                // cause the TileEntity renderer to error further down the line! But alas, nothing we can do.
                cbb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1));
            }
            if (cbb != null) bb = cbb;
        }
        return bb;
    }

    public static boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
    }
}
