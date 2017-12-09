package elucent.gravelores.block;

import elucent.gravelores.ConfigManager;
import elucent.gravelores.GravelOres;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;

public class BlockGravelOre extends BlockFalling {
	public Item itemBlock = null;
	private IBlockState dropCopier = null;
	public String oreKey = "";
	private boolean hidden;

	public BlockGravelOre(Material material, String name, String oreKey, boolean addToTab) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(GravelOres.MODID, name);


		if (addToTab) {
			setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		}

		setSoundType(SoundType.GROUND);
		itemBlock = (new ItemBlock(this).setRegistryName(this.getRegistryName()));
		this.oreKey = oreKey;
	}

	public BlockGravelOre setInspiration(Block b) {
		this.dropCopier = b.getDefaultState();
		return this;
	}

	public BlockGravelOre setInspiration(IBlockState b) {
		this.dropCopier = b;
		return this;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (dropCopier != null) {
			dropCopier.getBlock().getDrops(drops, world, pos, dropCopier, fortune);
		}
		else {
			super.getDrops(drops, world, pos, state, fortune);
		}
	}

	public void setHidden() {
		this.hidden = true;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
		// ores are hidden if unused
		if (!hidden) {
			tab.add(new ItemStack(this));
		}
	}

	@Override
	protected void onStartFalling(EntityFallingBlock fallingBlock) {
		// only drop the item if enabled in the config
		fallingBlock.shouldDropItem = ConfigManager.oreFallingDropItems;
	}

	/* we want to return BlockGravelOre instead of block */
	public BlockGravelOre setHarvestProperties(String toolType, int level) {
		super.setHarvestLevel(toolType, level);
		return this;
	}

	@Override
	public BlockGravelOre setHardness(float hardness) {
		super.setHardness(hardness);
		return this;
	}

	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName().toString(), "inventory"));
	}
}
