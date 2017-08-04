package elucent.gravelores.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import elucent.gravelores.ConfigManager;
import elucent.gravelores.GravelOres;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenGravelOres implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (!GravelOres.spawns.isEmpty() && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			if (random.nextInt(ConfigManager.orePileChance) == 0) {
				Block b = GravelOres.spawns.get(random.nextInt(GravelOres.spawns.size()));
				if (b != null) {
					IBlockState ore = b.getDefaultState();
					int xx = chunkX * 16 + 8 + random.nextInt(16);
					int zz = chunkZ * 16 + 8 + random.nextInt(16);
					BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(xx, 64, zz));
					// relative position and the one below
					IBlockState state = world.getBlockState(top);
					IBlockState below = world.getBlockState(top.down());
					if (state.getBlock() != Blocks.WATER && below.isOpaqueCube() && state.getBlock().isReplaceable(world, top)) {
						int tries = ConfigManager.orePileMinSize + random.nextInt(ConfigManager.orePileMaxSize - ConfigManager.orePileMinSize);
						List<BlockPos> blocks = new ArrayList<BlockPos>();
						blocks.add(top);

						// loop through the vein size
						for (int i = 0; i < tries; i++) {
							// start from a random ore and random side
							BlockPos start = blocks.get(random.nextInt(blocks.size()));
							EnumFacing face = EnumFacing.getFront(random.nextInt(6));
							// actual position
							BlockPos pos = start.offset(face);
							IBlockState current = world.getBlockState(pos);
							if (current.getBlock().isReplaceable(world, start.offset(face))) {
								blocks.add(pos);
							}
						}
						// place the blocks
						for (int i = 0; i < blocks.size(); i++) {
							world.setBlockState(blocks.get(i), ore);
						}
					}
				}
			}
		}
	}

}
