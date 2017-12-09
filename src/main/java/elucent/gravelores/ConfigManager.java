package elucent.gravelores;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {
	public static Configuration config;

	//MOBS
	public static Set<String> blacklist = new HashSet<>();
	public static Map<String, Integer> weights = new HashMap<String, Integer>();
	public static boolean hasBiomeWhitelist = false;
	public static boolean hasBiomeBlacklist = false;
	public static Set<String> biomeWhitelist = new HashSet<>();
	public static Set<String> biomeBlacklist = new HashSet<>();

	public static int orePileChance = 20;

	public static int orePileMinSize = 10;
	public static int orePileMaxSize = 40;

	public static boolean oreFallingDropItems = false;

	//STRUCTURES

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			load();
		}
	}

	public static void load() {
		config.addCustomCategoryComment("ores", "Settings related to gravel ore generation.");

		String[] blist = config.getStringList("blacklist", "ores", new String[] { "oreDiamond", "oreLapis", "oreRedstone", "oreEmerald" },
				"A list of blacklisted ore dictionary keys. Ores with these keys will not generate.");
		for (String s : blist) {
			blacklist.add(s);
		}

		String[] biomeBlackarray = config.getStringList("biomeTypeBlacklist", "ores", new String[] {"OCEAN", "RIVER"},
				"A list of biome types (for example, OCEAN or PLAINS) to not generate gravel ores in. Ignored if empty. See the Forge BiomeDictionary for valid names");
		for (String s : biomeBlackarray) {
			biomeBlacklist.add(s.toUpperCase());
		}
		hasBiomeBlacklist = !biomeBlacklist.isEmpty();
		String[] biomeWhitearray = config.getStringList("biomeTypeWhitelist", "ores", new String[0],
				"A list of biome types (for example, OCEAN or PLAINS) to require to generate ores. Ignored if empty. See the Forge BiomeDictionary for valid names");
		for (String s : biomeWhitearray) {
			biomeWhitelist.add(s.toUpperCase());
		}
		hasBiomeWhitelist = !biomeWhitelist.isEmpty();

		String[] spawnWeights = config.getStringList("spawnWeights", "ores", new String[] {
				"oreCoal:16",
				"oreIron:24",
				"oreRedstone:4",
				"oreGold:4",
				"oreLapis:2",
				"oreDiamond:1",
				"oreEmerald:1",
				"oreCopper:14",
				"oreTin:14",
				"oreLead:10",
				"oreSilver:8",
				"oreNickel:10",
				"oreAluminum:12"
		}, "The generation weight of each ore type. Ores like copper that may not exist in your instance, or blacklisted ores, will not be considered if not added by another mod. Follows the format \"<ore dictionary key>:<weight>\". If an ore is missing, it will default to 10.");

		for (int i = 0; i < spawnWeights.length; i++) {
			String[] parts = spawnWeights[i].split(":");
			String oreKey = parts[0];
			int weight = Integer.valueOf(parts[1]);
			weights.put(oreKey, weight);
		}

		orePileChance = config.getInt("orePileChance", "ores", 20, 0, 32767,
				"Chance per chunk that an ore pile will spawn. Set this to zero to disable generation.");

		orePileMinSize = config.getInt("orePileMinSize", "ores", 10, 0, 100,
				"Minimum size value for generated ore piles. This is a number of block spawning attempts, not blocks -- piles can be smaller than this minimum value.");
		orePileMaxSize = config.getInt("orePileMaxSize", "ores", 40, 0, 100,
				"Maximum size value for generated ore piles. This is a number of block spawning attempts, not blocks -- piles will not always be between this and the minimum size in block count.");

		oreFallingDropItems = config.getBoolean("oreFallingDropItems", "ores", false,
				"If true, falling gravel ores drop items if they are unable to land (e.g. falls on a torch). Defaults to false which prevents working around harvest levels.");

		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(GravelOres.MODID)) {
			load();
		}
	}
}
