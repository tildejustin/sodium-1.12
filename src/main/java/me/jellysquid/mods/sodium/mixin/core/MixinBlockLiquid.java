// package me.jellysquid.mods.sodium.mixin.core;
//
// import me.jellysquid.mods.sodium.client.world.VanillaFluidBlock;
// import net.minecraft.block.Block;
// import net.minecraft.block.BlockLiquid;
// import net.minecraft.block.material.Material;
// import net.minecraft.block.state.IBlockState;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.world.World;
// import org.spongepowered.asm.mixin.Mixin;
//
// @Mixin(BlockLiquid.class)
// public abstract class MixinBlockLiquid implements VanillaFluidBlock {
//     private final IFluidBlock sodium$fluidBlock = new VanillaFluidBlock.Implementation((Block)(Object)this);
//
//     @Override
//     public IFluidBlock getFakeFluidBlock() {
//         return sodium$fluidBlock;
//     }
// }