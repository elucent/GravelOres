package elucent.gravelores.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockExtraGravelOre extends BlockGravelOre {
	public static final String BASE_NAME = "extra_gravel_ore";
	private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation("gravelores", "extra_gravel_ore");

	private int color;
	public BlockExtraGravelOre(Material material, String name, int color) {
		super(material, BASE_NAME + "_" + name.toLowerCase(), "ore" + name, true);
		this.color = color;
		this.setUnlocalizedName(BASE_NAME + "." + name.toLowerCase());
	}

	public int getColor() {
		return color;
	}

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
    	return BlockRenderLayer.CUTOUT;
    }

    @SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(RESOURCE_LOCATION, "inventory"));
	}
}
