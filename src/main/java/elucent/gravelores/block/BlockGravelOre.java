package elucent.gravelores.block;

import java.util.List;

import elucent.gravelores.GravelOres;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BlockGravelOre extends BlockFalling {
	public Item itemBlock = null;
	public Block dropCopier = null;
	public boolean isOpaqueCube = true, isFullCube = true;
	public BlockRenderLayer layer = BlockRenderLayer.SOLID;
	public BlockGravelOre(Material material, String name, String oreKey, boolean addToTab){
		super(material);
		setUnlocalizedName(name);
		setRegistryName(GravelOres.MODID+":"+name);
		if (addToTab){
			setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		}
		GameRegistry.register(this);
        GameRegistry.register(itemBlock = (new ItemBlock(this).setRegistryName(this.getRegistryName())));
		OreDictionary.registerOre(oreKey, this);
    }
	
	public BlockGravelOre setIsOpaqueCube(boolean b){
		isOpaqueCube = b;
		return this;
	}
	
	public BlockGravelOre setInspiration(Block b){
		this.dropCopier = b;
		return this;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		if (dropCopier != null){
			return dropCopier.getDrops(world, pos, state, fortune);
		}
		else {
			return super.getDrops(world, pos, state, fortune);
		}
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return isOpaqueCube;
	}
	
	public BlockGravelOre setIsFullCube(boolean b){
		isFullCube = b;
		return this;
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return isFullCube;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state){
		return isFullCube;
	}
	
	public BlockGravelOre setHarvestProperties(String toolType, int level){
		super.setHarvestLevel(toolType, level);
		return this;
	}
	
	public BlockGravelOre setHardness(float hardness){
		super.setHardness(hardness);
		return this;
	}
	
	public void initModel(){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName().toString(),"inventory"));
	}
}
