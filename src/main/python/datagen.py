import logging
from time import perf_counter
from gravelores.cache import CachedOutput
from gravelores.logger import setupLogging

from gravelores.blocks import BlockGenerator
from gravelores.loot import LootTableGenerator
from gravelores.models import ModelGenerator
from gravelores.tags import TagGenerator
from gravelores.worldgen import WorldgenGenerator


ROOT_PATH = "../../generated/resources"
"""Target folder for datagen"""

LOG_PATH = "../../../run/logs"
"""Path to create log files for datagen, set to None to skip"""

MOD_ID = "gravelores"
"""Mod ID for all resources generated"""

ITEM_GROUP = MOD_ID+":"+MOD_ID
"""Creative tab for items"""


ORES = {
    # metals
    "copper": {
        "drop": {
            "drop": "minecraft:raw_copper",
            "count": (2, 5)
        },
        "tier": "stone",
        "overworld_chance": 250  # 16 + 16
    },
    "iron": {
        "drop": "minecraft:raw_iron",
        "tier": "stone",
        "overworld_chance": 250  # 90 + 10 + 10
    },
    "gold": {
        "drop": "minecraft:raw_gold",
        "tier": "iron",
        "overworld_chance": 450,  # 50 + 4 (buried) + 1 (buried)
        "nether_chance": 70  # 20 + 10
    },
    # gems
    "diamond": {
        "drop": "minecraft:diamond",
        "tier": "iron",
        "overworld_chance": 1500  # 9 + 7 + 4 (buried)
    },
    "emerald": {
        "drop": "minecraft:emerald",
        "tier": "iron",
        "overworld_chance": 250,  # 100
        "custom_biomes": True
    },
    "quartz": {
        "drop": "minecraft:quartz",
        "nether_chance": 50  # 32 + 16
    },
    # other
    "coal": {
        "drop": "minecraft:coal",
        "overworld_chance": 250  # 20 + 30
    },
    "redstone": {
        "drop": {
            "drop": "minecraft:redstone",
            "count": (4, 5),
            "flatBonus": True
        },
        "tier": "iron",
        "overworld_chance": 700  # 8 + 4
    },
    "lapis": {
        "drop": {
            "drop": "minecraft:lapis_lazuli",
            "count": (4, 9)
        },
        "tier": "stone",
        "overworld_chance": 1000  # 2 + 4 buried
    },
    
    # compat
    "aluminum": {
        "mods": ["immersiveengineering"],
        "tier": "stone",
        "tag_drop": "raw_materials",
        "overworld_chance": 750  # Immersive Engineering uses weird ore config so guessing
    },
    "cobalt": {
        "mods": ["tconstruct"],
        "tier": "iron",
        "tag_drop": "raw_materials",
        "nether_chance": 300  # Rarer than gold
    },
    "lead": {
        "mods": ["embers", "immersiveengineering", "thermal", "mekanism"],
        "tier": "iron",  # mekansim says stone, consensus iron
        "tag_drop": "raw_materials",
        "overworld_chance": 750  # Embers 8, Thermal 6, Mek 8
    },
    "nickel": {
        "mods": ["immersiveengineering", "thermal"],
        "tier": "iron",
        "tag_drop": "raw_materials",
        "overworld_chance": 850  # Thermal 4
    },
    "osmium": {
        "mods": ["mekanism"],
        "tier": "stone",
        "tag_drop": "raw_materials",
        "overworld_chance": 450  # Mek: 65 + 6 + 8
    },
    "silver": {
        "mods": ["embers", "immersiveengineering", "thermal"],
        "tier": "iron",
        "tag_drop": "raw_materials",
        "overworld_chance": 850  # Embers 4, Thermal 4
    },
    "tin": {
        "mods": ["thermal", "mekanism"],
        "tier": "stone",
        "tag_drop": "raw_materials",
        "overworld_chance": 650  # Mek: 14 + 12, Thermal 6
    },
    "uranium": {
        "mods": ["immersiveengineering", "mekanism"],
        "tier": "iron",  # mekanism says stone, but it already violated concensus
        "tag_drop": "raw_materials",
        "overworld_chance": 1000  # Mek: 4 + 7 (buried)
    },
    "zinc": {
        "mods": ["create"],
        "tier": "iron",
        "tag_drop": "raw_materials",
        "overworld_chance": 750  # Create: 8
    }
}
"""General config for all datagen to run, puts it in one nice spot"""

if __name__ == "__main__":
    # setup datagen, run everything below these lines
    startTime = perf_counter()
    setupLogging(LOG_PATH, debug=False)
    cache = CachedOutput(ROOT_PATH)
    # end setup, start of mod specific code
    
    # saves writing `_gravel_ore` everywhere
    for variant, data in ORES.items():
        data["name"] = variant + "_gravel_ore"
        data["pile"] = data["name"] + "_pile"
        data["id"] = MOD_ID + ":" + data["name"]
        data["pile_id"] = MOD_ID + ":" + data["pile"]
        
        # tag_drop is used later to define which tag, here it just generates drop data
        if "tag_drop" in data:
            if not "drop" in data:
                data["drop"] = {}
            data["drop"]["drop"] = f"gravelores:ore_drops/{variant}"
            data["drop"]["tag"] = True
    
    with ModelGenerator(cache) as gen:
        for data in ORES.values():
            gen.blockCubeAll(MOD_ID, data["name"])
    
    with BlockGenerator(cache) as gen:
        for data in ORES.values():
            gen.gravelBlock(MOD_ID, data["name"], itemGroup=ITEM_GROUP)
    
    with LootTableGenerator(cache) as gen:
        for data in ORES.values():
            drop = data["drop"]
            if isinstance(drop, dict):
                gen.oreBlock(MOD_ID, data["name"], **drop)
            else:
                gen.oreBlock(MOD_ID, data["name"], drop=drop)
    
    with WorldgenGenerator(cache) as gen:
        for data in ORES.values():
            if "overworld_chance" in data:
                gen.addPile(MOD_ID, data["pile"], data["id"], data["overworld_chance"], data.get("mods"))
            if "nether_chance" in data:
                gen.addPileNether(MOD_ID, data["pile"], data["id"], data["nether_chance"], data.get("mods"))
    
    with TagGenerator(cache) as gen:
        gen.add("blocks", "minecraft", "mineable/shovel", *[data["id"] for data in ORES.values()])
        for variant, data in ORES.items():
            # harvest tiers
            if "tier" in data:
                gen.add("blocks", "minecraft", f"needs_{data['tier']}_tool", data["id"])
                
            # tagging piles in the overworld tag adds them to overworld biomes
            if "overworld_chance" in data and not "custom_biomes" in data:
                gen.add("worldgen/placed_feature", "gravelores", "overworld_piles", data["pile_id"], optional = "mods" in data)
            # tagging piles in the nether tag adds them to nether biomes
            if "nether_chance" in data:
                # nether piles automatically append "_nether", so manually append for the tag
                gen.add("worldgen/placed_feature", "gravelores", "nether_piles", data["pile_id"] + "_nether", optional = "mods" in data)
            
            # redirect gravelores:ore_drops/<variant> to forge:raw_materials/<variant>
            # lets a modpack maker easily choose the drop for an ore without emptying the
            # forge tag or redefining our loot table
            if "tag_drop" in data:
                gen.add("items", MOD_ID, f"ore_drops/{variant}", f"#forge:{data['tag_drop']}/{variant}", optional = True)
                
    
    # end of datagen, save the cache file
    cache.finalize()
    logging.info(f"Finished running datagen in {perf_counter() - startTime} seconds")