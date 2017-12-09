package elucent.gravelores.proxy;

import elucent.gravelores.GravelOres;
import elucent.gravelores.block.BlockExtraGravelOre;
import elucent.gravelores.block.BlockGravelOre;
import elucent.gravelores.util.CustomStateMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void registerRendering(ModelRegistryEvent event) {
		for (BlockGravelOre block : GravelOres.blocks) {
			block.initModel();
		}
		IStateMapper mapper = new CustomStateMap(BlockExtraGravelOre.BASE_NAME);
		for (BlockGravelOre block : GravelOres.extraBlocks) {
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

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
