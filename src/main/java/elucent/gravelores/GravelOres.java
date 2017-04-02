package elucent.gravelores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import elucent.gravelores.block.BlockGravelOre;
import elucent.gravelores.proxy.CommonProxy;
import elucent.gravelores.world.WorldGenGravelOres;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = GravelOres.MODID, version = GravelOres.VERSION, dependencies = GravelOres.DEPENDENCIES)
public class GravelOres
{
    public static final String MODID = "gravelores";
    public static final String VERSION = "1.0";
    public static final String DEPENDENCIES = "after:*";
    
    public static List<BlockGravelOre> blocks = new ArrayList<BlockGravelOre>();
    
    public static List<BlockGravelOre> spawns = new ArrayList<BlockGravelOre>();
    
    public static BlockGravelOre iron_gravel_ore, coal_gravel_ore, redstone_gravel_ore, gold_gravel_ore, emerald_gravel_ore, 
    lapis_gravel_ore, diamond_gravel_ore, copper_gravel_ore, lead_gravel_ore, tin_gravel_ore, silver_gravel_ore,
    nickel_gravel_ore;
    
    public static Material GRAVEL_ORE;
    
    @SidedProxy(clientSide = "elucent.gravelores.proxy.ClientProxy",serverSide = "elucent.gravelores.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	MinecraftForge.EVENT_BUS.register(new ConfigManager());
        ConfigManager.init(event.getSuggestedConfigurationFile());
    	proxy.preInit(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	proxy.postInit(event);
    }
    
    public static void addSpawn(BlockGravelOre block, int weight){
    	for (int i = 0; i < weight; i ++){
    		spawns.add(block);
    	}
    }
    
    public static void registerAll(){
    	GRAVEL_ORE = new Material(MapColor.GRAY){
    		@Override
    		public boolean isToolNotRequired(){
    			return false;
    		}
    	};
    	if (!ConfigManager.blacklist.contains("oreCoal")){
    		blocks.add(coal_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "coal_gravel_ore", "oreCoal", true).setInspiration(Blocks.COAL_ORE).setHarvestProperties("shovel", 0).setHardness(1.6f));
    		addSpawn(coal_gravel_ore,ConfigManager.weights.get("oreCoal"));
    	}
    	if (!ConfigManager.blacklist.contains("oreIron")){
    		blocks.add(iron_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "iron_gravel_ore", "oreIron", true).setHarvestProperties("shovel", 1).setHardness(2.2f));
    		addSpawn(iron_gravel_ore,ConfigManager.weights.get("oreIron"));
        }
    	if (!ConfigManager.blacklist.contains("oreLapis")){
    		blocks.add(lapis_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "lapis_gravel_ore", "oreLapis", true).setInspiration(Blocks.LAPIS_ORE).setHarvestProperties("shovel", 1).setHardness(2.0f));
    		addSpawn(lapis_gravel_ore,ConfigManager.weights.get("oreLapis"));
        }
    	if (!ConfigManager.blacklist.contains("oreGold")){
    		blocks.add(gold_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "gold_gravel_ore", "oreGold", true).setHarvestProperties("shovel", 2).setHardness(2.4f));
    		addSpawn(gold_gravel_ore,ConfigManager.weights.get("oreGold"));
        }
    	if (!ConfigManager.blacklist.contains("oreRedstone")){
    		blocks.add(redstone_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "redstone_gravel_ore", "oreRedstone", true).setInspiration(Blocks.REDSTONE_ORE).setHarvestProperties("shovel", 2).setHardness(2.8f));
    		addSpawn(redstone_gravel_ore,ConfigManager.weights.get("oreRedstone"));
        }
    	if (!ConfigManager.blacklist.contains("oreDiamond")){
    		blocks.add(diamond_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "diamond_gravel_ore", "oreDiamond", true).setInspiration(Blocks.DIAMOND_ORE).setHarvestProperties("shovel", 2).setHardness(3.0f));
    		addSpawn(diamond_gravel_ore,ConfigManager.weights.get("oreDiamond"));
        }
    	if (!ConfigManager.blacklist.contains("oreEmerald")){
    		blocks.add(emerald_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "emerald_gravel_ore", "oreEmerald", true).setInspiration(Blocks.EMERALD_ORE).setHarvestProperties("shovel", 2).setHardness(2.8f));
    		addSpawn(emerald_gravel_ore,ConfigManager.weights.get("oreEmerald"));
        }
    	if (!ConfigManager.blacklist.contains("oreTin")){
    		if (OreDictionary.getOres("oreTin").size() > 0){
    			addSpawn(tin_gravel_ore,ConfigManager.weights.get("oreTin"));
    		}
            blocks.add(tin_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "tin_gravel_ore", "oreTin", true).setHarvestProperties("shovel", 0).setHardness(1.9f));
    	}
    	if (!ConfigManager.blacklist.contains("oreNickel")){
    		if (OreDictionary.getOres("oreNickel").size() > 0){
    			addSpawn(nickel_gravel_ore,ConfigManager.weights.get("oreNickel"));
    		}
    		blocks.add(nickel_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "nickel_gravel_ore", "oreNickel", true).setHarvestProperties("shovel", 2).setHardness(2.5f));
    	}
    	if (!ConfigManager.blacklist.contains("oreSilver")){
    		if (OreDictionary.getOres("oreSilver").size() > 0){
    			addSpawn(silver_gravel_ore,ConfigManager.weights.get("oreSilver"));
    		}
            blocks.add(silver_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "silver_gravel_ore", "oreSilver", true).setHarvestProperties("shovel", 2).setHardness(2.6f));
    	}
    	if (!ConfigManager.blacklist.contains("oreLead")){
    		if (OreDictionary.getOres("oreLead").size() > 0){
    			addSpawn(lead_gravel_ore,ConfigManager.weights.get("oreLead"));
    		}
    		blocks.add(lead_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "lead_gravel_ore", "oreLead", true).setHarvestProperties("shovel", 2).setHardness(2.6f));
        }
    	if (OreDictionary.getOres("oreCopper").size() > 0 && !ConfigManager.blacklist.contains("oreCopper")){
    		if (OreDictionary.getOres("oreCopper").size() > 0){
    			addSpawn(copper_gravel_ore,ConfigManager.weights.get("oreCopper"));
    		}
    		blocks.add(copper_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "copper_gravel_ore", "oreCopper", true).setHarvestProperties("shovel", 0).setHardness(1.9f));
    	}
    	
    	GameRegistry.registerWorldGenerator(new WorldGenGravelOres(), 100);
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRendering(){
    	for (int i = 0; i < blocks.size(); i ++){
    		blocks.get(i).initModel();
    	}
    }
    
    public static void registerRecipes(){
    	for (int i = 0; i < blocks.size(); i ++){
    		boolean doContinue = true;
    		for (int j = 0; j < FurnaceRecipes.instance().getSmeltingList().size() && doContinue; j ++){
    			Entry<ItemStack, ItemStack> entry = (Entry<ItemStack, ItemStack>) FurnaceRecipes.instance().getSmeltingList().entrySet().toArray()[j];
    			int[] oreKeys1 = OreDictionary.getOreIDs(new ItemStack(blocks.get(i)));
				ItemStack test = entry.getKey().copy();
				if (test.getItemDamage() == 32767){
					test.setItemDamage(0);
				}
    			int[] oreKeys2 = OreDictionary.getOreIDs(test);
    			for (int k = 0; k < oreKeys1.length && doContinue; k ++){
    				for (int l = 0; l < oreKeys2.length && doContinue; l ++){
    					if (oreKeys1[k] == oreKeys2[l]){
    						FurnaceRecipes.instance().addSmeltingRecipeForBlock(blocks.get(i), entry.getValue(), FurnaceRecipes.instance().getSmeltingExperience(entry.getValue()));
    						doContinue = false;
    					}
    				}
    			}
        	}
    	}
    }
}
