package elucent.gravelores.proxy;

import elucent.gravelores.GravelOres;
import elucent.gravelores.block.BlockExtraGravelOre;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {
	private static Minecraft mc = Minecraft.getMinecraft();
	@Override
	public void init(FMLInitializationEvent event) {
		Block[] blocks = GravelOres.extraBlocks.toArray(new Block[GravelOres.extraBlocks.size()]);
		mc.getBlockColors().registerBlockColorHandler((state, world, pos, index) -> {
			Block block = state.getBlock();
			if(index == 1 && block instanceof BlockExtraGravelOre) {
				return ((BlockExtraGravelOre) block).getColor();
			}
			return 0xFFFFFF;
		}, blocks);

		mc.getItemColors().registerItemColorHandler((stack, index) -> {
			Block block = Block.getBlockFromItem(stack.getItem());
			if(index == 1 && block instanceof BlockExtraGravelOre) {
				return ((BlockExtraGravelOre) block).getColor();
			}
			return 0xFFFFFF;
		}, blocks);
	}
}
