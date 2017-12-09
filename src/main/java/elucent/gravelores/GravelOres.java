package elucent.gravelores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elucent.gravelores.block.BlockExtraGravelOre;
import elucent.gravelores.block.BlockGravelOre;
import elucent.gravelores.proxy.CommonProxy;
import elucent.gravelores.world.WorldGenGravelOres;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = GravelOres.MODID, version = GravelOres.VERSION, dependencies = GravelOres.DEPENDENCIES)
public class GravelOres {
	public static final String MODID = "gravelores";
	public static final String VERSION = "${version}";
	public static final String DEPENDENCIES = "after:*";

	public static List<BlockGravelOre> blocks = new ArrayList<>();
	public static List<BlockGravelOre> extraBlocks = new ArrayList<>();
	public static List<Pair<BlockGravelOre, String>> extraBlocksInspirations = new ArrayList<>();

	public static List<BlockGravelOre> spawns = new ArrayList<BlockGravelOre>();

	public static BlockGravelOre iron_gravel_ore, coal_gravel_ore, redstone_gravel_ore, gold_gravel_ore, emerald_gravel_ore, lapis_gravel_ore,
			diamond_gravel_ore, copper_gravel_ore, lead_gravel_ore, tin_gravel_ore, silver_gravel_ore, nickel_gravel_ore, aluminum_gravel_ore;

	public static Material GRAVEL_ORE;

	@SidedProxy(clientSide = "elucent.gravelores.proxy.ClientProxy", serverSide = "elucent.gravelores.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final Logger log = LogManager.getLogger(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ConfigManager());
		MinecraftForge.EVENT_BUS.register(this);
		ConfigManager.init(event.getSuggestedConfigurationFile());

		registerAll();

		proxy.preInit(event);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		for (Block b : blocks) {
			event.getRegistry().register(b);
		}
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for (BlockGravelOre b : blocks) {
			event.getRegistry().register(b.itemBlock);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		addReferenceProperties();

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		integrateOres();

		proxy.postInit(event);
	}

	/**
	 * Populates the list of blocks and creates the instances
	 */
	private static void registerAll() {
		// custom material to ensure a tool is required, only vanilla one that does this is snow
		GRAVEL_ORE = new Material(MapColor.GRAY) {
			@Override
			public boolean isToolNotRequired() {
				return false;
			}
		};

		blocks.add(coal_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "coal_gravel_ore", "oreCoal", true)
				.setInspiration(Blocks.COAL_ORE).setHarvestProperties("shovel", 0).setHardness(1.6f));

		blocks.add(iron_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "iron_gravel_ore", "oreIron", true)
				.setHarvestProperties("shovel", 1).setHardness(2.2f));

		blocks.add(lapis_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "lapis_gravel_ore", "oreLapis", true)
				.setInspiration(Blocks.LAPIS_ORE).setHarvestProperties("shovel", 1).setHardness(2.0f));

		blocks.add(gold_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "gold_gravel_ore", "oreGold", true)
				.setHarvestProperties("shovel", 2).setHardness(2.4f));

		blocks.add(redstone_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "redstone_gravel_ore", "oreRedstone", true)
				.setInspiration(Blocks.REDSTONE_ORE).setHarvestProperties("shovel", 2).setHardness(2.8f));

		blocks.add(diamond_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "diamond_gravel_ore", "oreDiamond", true)
				.setInspiration(Blocks.DIAMOND_ORE).setHarvestProperties("shovel", 2).setHardness(3.0f));

		blocks.add(emerald_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "emerald_gravel_ore", "oreEmerald", true)
				.setInspiration(Blocks.EMERALD_ORE).setHarvestProperties("shovel", 2).setHardness(2.8f));

		blocks.add(tin_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "tin_gravel_ore", "oreTin", true)
				.setHarvestProperties("shovel", 0).setHardness(1.9f));

		blocks.add(nickel_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "nickel_gravel_ore", "oreNickel", true)
				.setHarvestProperties("shovel", 2).setHardness(2.5f));

		blocks.add(silver_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "silver_gravel_ore", "oreSilver", true)
				.setHarvestProperties("shovel", 2).setHardness(2.6f));

		blocks.add(lead_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "lead_gravel_ore", "oreLead", true)
				.setHarvestProperties("shovel", 2).setHardness(2.6f));

		blocks.add(copper_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "copper_gravel_ore", "oreCopper", true)
				.setHarvestProperties("shovel", 0).setHardness(1.9f));

		blocks.add(aluminum_gravel_ore = new BlockGravelOre(GRAVEL_ORE, "aluminum_gravel_ore", "oreAluminum", true)
				.setHarvestProperties("shovel", 0).setHardness(1.9f));

		// extra ores
		Set<String> seen = new HashSet<>();
		for(String s : ConfigManager.extraBlocks) {
			String[] parts = s.split(":");
			// ensure length
			if(parts.length != 4 && parts.length != 6 && parts.length != 7) {
				log.error("Invalid extra ore entry '{}': should be in the format 'name:color:harvestLevel:resistance' or 'name:color:harvestLevel:resistance:modid:baseore:meta'", s);
				continue;
			} else if(!parts[0].matches("^[A-Za-z0-9]+$")) {
				log.error("Invalid extra ore entry '{}': name can only contain letters and numbers", s);
				continue;
			} else if(seen.contains(parts[0].toLowerCase())) {
				log.error("Invalid extra ore entry '{}': duplicate name {}", s, parts[0]);
				continue;
			}

			// store the last part of the string for later
			String dropString = null;
			if(parts.length > 5) {
				dropString = parts[4] + ":" + parts[5];
				if(parts.length == 7) {
					dropString += ":" + parts[6];
				}
			}

			try {
				// ensure all numbers are valid
				int color = Integer.parseInt(parts[1], 16);
				int harvestLevel = Integer.parseInt(parts[2]);
				float hardness = Float.parseFloat(parts[3]);

				// capitalize first letter
				String name = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);
				BlockGravelOre block = new BlockExtraGravelOre(GRAVEL_ORE, name, color).setHarvestProperties("shovel", harvestLevel).setHardness(hardness);
				blocks.add(block);
				extraBlocks.add(block);
				extraBlocksInspirations.add(Pair.of(block, dropString));
				seen.add(name.toLowerCase());
			} catch(NumberFormatException e) {
				// invalid number
				log.error("Invalid extra ore entry '{}': color must be a six digit hex, harvest level an integer, and hardness a float", s, parts[1]);
			}
		}

		GameRegistry.registerWorldGenerator(new WorldGenGravelOres(), 88);
	}

	// run in init so the blocks are registered
	@SuppressWarnings("deprecation")
	private static void addReferenceProperties() {
		for(Pair<BlockGravelOre, String> pair : extraBlocksInspirations) {
			if(pair.getRight() == null) {
				continue;
			}
			String[] parts = pair.getRight().split(":");

			BlockGravelOre ore = pair.getLeft();
			Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(parts[0], parts[1]));
			if(block == null) {
				log.warn("Failed to add drops for {}: failed to find block '{}'", ore.getRegistryName(), pair.getRight());
				continue;
			}

			int meta = 0;
			if(parts.length > 2) {
				try {
					meta = Integer.parseInt(parts[2]);
				} catch(NumberFormatException e) {
					// ensure number
					log.error("Failed to add drops for {}: invalid metadata {}", ore.getRegistryName(), parts[2]);
					continue;
				}
			}

			// set the harvest level and mining level from the reference
			ore.setInspiration(block.getStateFromMeta(meta));
		}
		extraBlocksInspirations = null;
	}

	/**
	 * Called to register anything that is dependent on other mods loading
	 *
	 * Notably, oredict to determine if we use the ore, and furnace recipes to determine how to smelt them
	 */
	private static void integrateOres() {
		log.info("Started populating oregen list");

		// a map seemed to be the fastest as its a direct int to block lookup rather than iterative
		// array won't work for lookup as ints are not all next to each other
		Map<Integer, BlockGravelOre> furnaceOres = new HashMap<>(blocks.size());
		for (BlockGravelOre block : blocks) {
			String oreKey = block.oreKey;
			// ensure the ore exists, but always add the extra ores
			boolean hasOreDict = !OreDictionary.getOres(oreKey, false).isEmpty();
			if(extraBlocks.contains(block) || hasOreDict) {
				// we only register the oredict if someone else aready registered it
				OreDictionary.registerOre(oreKey, block);

				// add spawn if enabled
				if (!ConfigManager.blacklist.contains(oreKey)) {
					Integer weight = ConfigManager.weights.get(oreKey);
					if(weight == null) {
						weight = 10;
					}
					addSpawn(block, weight);
				}

				// we only need to find if we have an ore with the specific ID, so create a temporary hash map
				if(hasOreDict) {
					furnaceOres.put(OreDictionary.getOreID(oreKey), block);
				}
			}
			else {
				// if not, hide it from Creative
				block.setHidden();
			}
		}

		log.info("Started generating ore recipes...");
		long time = System.nanoTime();
		List<Pair<BlockGravelOre, ItemStack>> furnaceRecipes = new ArrayList<>(blocks.size());

		// only loop through all recipes once
		for (Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
			// ensure damage is valid
			ItemStack test = entry.getKey().copy();
			// avoid invalid recipes
			if (test.isEmpty()) {
				continue;
			}
			if (test.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				test.setItemDamage(0);
			}

			int[] testOreIDs = OreDictionary.getOreIDs(test);
			// try each remaining block to match this entry
			for (int testOreID : testOreIDs) {
				// check if we have a block matching that ore ID
				BlockGravelOre block = furnaceOres.get(testOreID);
				if (block != null) {
					// if so, add the recipe, though we don't add it yet to avoid concurrent modification
					furnaceRecipes.add(Pair.of(block, entry.getValue()));

					// remove the block from our list so we don't check again
					furnaceOres.remove(testOreID);
					break;
				}

				// out of blocks? we can stop searching
				if(furnaceOres.isEmpty()) {
					break;
				}
			}
		}

		// add all the recipes we build earlier
		for (Pair<BlockGravelOre, ItemStack> recipe : furnaceRecipes) {
			GameRegistry.addSmelting(recipe.getLeft(), recipe.getRight(), FurnaceRecipes.instance().getSmeltingExperience(recipe.getRight()));
		}
		// how fast were we?
		log.info("Ore recipe generation completed in " + ((System.nanoTime() - time) / 1000000000D) + " seconds.");
	}

	private static void addSpawn(BlockGravelOre block, int weight) {
		for (int i = 0; i < weight; i++) {
			spawns.add(block);
		}
	}
}
