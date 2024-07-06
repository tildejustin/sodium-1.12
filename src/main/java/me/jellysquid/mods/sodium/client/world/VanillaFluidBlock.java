// package me.jellysquid.mods.sodium.client.world;
//
// import net.minecraft.block.Block;
// import net.minecraft.block.BlockLiquid;
// import net.minecraft.block.material.Material;
// import net.minecraft.block.state.IBlockState;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.World;
// import org.jetbrains.annotations.Nullable;
//
// public interface VanillaFluidBlock {
//     IFluidBlock getFakeFluidBlock();
//
//     class Implementation implements IFluidBlock {
//         private final Block block;
//         private Fluid sodium$forgeFluid;
//
//         public Implementation(Block block) {
//             this.block = block;
//         }
//
//         @Override
//         public Fluid getFluid() {
//             Fluid fluid = sodium$forgeFluid;
//             if(fluid == null) {
//                 sodium$forgeFluid = fluid = block.getDefaultState().getMaterial() == Material.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
//             }
//             return fluid;
//         }
//
//         @Override
//         public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace) {
//             throw new UnsupportedOperationException();
//         }
//
//         @Nullable
//         @Override
//         public FluidStack drain(World world, BlockPos pos, boolean doDrain) {
//             throw new UnsupportedOperationException();
//         }
//
//         @Override
//         public boolean canDrain(World world, BlockPos pos) {
//             throw new UnsupportedOperationException();
//         }
//
//         @Override
//         public float getFilledPercentage(World world, BlockPos pos) {
//             IBlockState blockState = world.getBlockState(pos);
//             return getFluid() == null ? 0 : 1 - BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL));
//         }
//     }
// }
