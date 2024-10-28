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
        "overworld_chance": 450  # 50 + 4 (buried) + 1 (buried)
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
        "drop": "minecraft:quartz"
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
                gen.addPile(MOD_ID, data["pile"], data["id"], data["overworld_chance"])
    
    with TagGenerator(cache) as gen:
        gen.add("blocks", "minecraft", "mineable/shovel", *[data["id"] for data in ORES.values()])
        for data in ORES.values():
            if "tier" in data:
                gen.add("blocks", "minecraft", f"needs_{data['tier']}_tool", data["id"])
            if "overworld_chance" in data and not "custom_biomes" in data:
                gen.add("worldgen/placed_feature", "gravelores", "overworld_piles", MOD_ID + ":" + data["pile"])
    
    # end of datagen, save the cache file
    cache.finalize()
    logging.info(f"Finished running datagen in {perf_counter() - startTime} seconds")