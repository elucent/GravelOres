package elucent.gravelores.block;

import elucent.gravelores.util.CustomStateMap;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class BlockExtraGravelOre extends BlockGravelOre {
	private static final String BASE_NAME = "extra_gravel_ore";
	private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation("gravelores", "extra_gravel_ore");
	private static final IStateMapper STATE_MAP = new CustomStateMap(BASE_NAME);

	private int color;
	private String name;
	public BlockExtraGravelOre(Material material, String name, int color) {
		super(material, BASE_NAME + "_" + name.toLowerCase(), "ore" + name, true);
		this.name = name;
		this.color = color;
		this.setUnlocalizedName(BASE_NAME + "." + name.toLowerCase());
	}

	public int getColor() {
		return color;
	}

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    @Override
	public String getLocalizedName() {
    	String key = this.getUnlocalizedName() + ".name";
    	if(I18n.canTranslate(key)) {
            return I18n.translateToLocal(key);
    	}
        return I18n.translateToLocalFormatted("tile." + BASE_NAME + ".default.name", name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
    	return BlockRenderLayer.CUTOUT;
    }

	@Override
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, STATE_MAP);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(RESOURCE_LOCATION, "inventory"));
	}
}
