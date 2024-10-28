import logging
from time import perf_counter
from gravelores.cache import CachedOutput
from gravelores.logger import setupLogging

from gravelores.blocks import BlockGenerator
from gravelores.loot import LootTableGenerator
from gravelores.models import ModelGenerator


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
        }
    },
    "iron": {
        "drop": "minecraft:raw_iron"
    },
    "gold": {
        "drop": "minecraft:raw_gold"
    },
    # gems
    "diamond": {
        "drop": "minecraft:diamond"
    },
    "emerald": {
        "drop": "minecraft:emerald"
    },
    "quartz": {
        "drop": "minecraft:quartz"
    },
    # other
    "coal": {
        "drop": "minecraft:coal"
    },
    "redstone": {
        "drop": {
            "drop": "minecraft:redstone",
            "count": (4, 5),
            "flatBonus": True
        }
    },
    "lapis": {
        "drop": {
            "drop": "minecraft:lapis_lazuli",
            "count": (4, 9)
        }
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
    
    # end of datagen, save the cache file
    cache.finalize()
    logging.info(f"Finished running datagen in {perf_counter() - startTime} seconds")