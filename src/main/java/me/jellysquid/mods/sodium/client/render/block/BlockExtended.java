package me.jellysquid.mods.sodium.client.render.block;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockExtended {
    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     *
     * @param base_state The base state, getActualState should be called first
     * @param world The current world
     * @param pos Block position in world
     * @param side The side to check
     * @return True if the block is solid on the specified side.
     */
    @Deprecated //Use IBlockState.getBlockFaceShape
    public static boolean isSideSolid(Block block, IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (base_state.isTopSolid() && side == EnumFacing.UP) // Short circuit to vanilla function if its true
            return true;

        if (block instanceof BlockSlab)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            return base_state.isFullBlock()
                    || (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP    && side == EnumFacing.UP  )
                    || (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM && side == EnumFacing.DOWN);
        }
        else if (block instanceof BlockFarmland)
        {
            return (side != EnumFacing.DOWN && side != EnumFacing.UP);
        }
        else if (block instanceof BlockStairs)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            boolean flipped = state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
            BlockStairs.EnumShape shape = (BlockStairs.EnumShape)state.getValue(BlockStairs.SHAPE);
            EnumFacing facing = (EnumFacing)state.getValue(BlockStairs.FACING);
            if (side == EnumFacing.UP) return flipped;
            if (side == EnumFacing.DOWN) return !flipped;
            if (facing == side) return true;
            if (flipped)
            {
                if (shape == BlockStairs.EnumShape.INNER_LEFT ) return side == facing.rotateYCCW();
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) return side == facing.rotateY();
            }
            else
            {
                if (shape == BlockStairs.EnumShape.INNER_LEFT ) return side == facing.rotateY();
                if (shape == BlockStairs.EnumShape.INNER_RIGHT) return side == facing.rotateYCCW();
            }
            return false;
        }
        else if (block instanceof BlockSnow)
        {
            IBlockState state = block.getActualState(base_state, world, pos);
            return ((Integer)state.getValue(BlockSnow.LAYERS)) >= 8;
        }
        else if (block instanceof BlockHopper && side == EnumFacing.UP)
        {
            return true;
        }
        else if (block instanceof BlockCompressedPowered)
        {
            return true;
        }
        return block.isNormalCube(base_state);
    }

}
