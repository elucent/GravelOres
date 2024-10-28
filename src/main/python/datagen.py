from gravelores.cache import CachedOutput
from gravelores.logger import setupLogging
from gravelores.models import ModelGenerator


ROOT_PATH = "../../generated/resources"
"""Target folder for datagen"""

LOG_PATH = "../../../run/logs"
"""Path to create log files for datagen, set to None to skip"""

MOD_ID = "gravelores"
"""Mod ID for all resources generated"""


ORES = {
    # metals
    "copper": {},
    "iron": {},
    "gold": {},
    # gems
    "diamond": {},
    "emerald": {},
    "quartz": {},
    # other
    "coal": {},
    "redstone": {},
    "lapis": {}
}
"""General config for all datagen to run, puts it in one nice spot"""

def blockName(variant: str) -> str:
    """Maps a variant name to a block ID, saves writing `_gravel_ore` everywhere."""
    return variant + "_gravel_ore"

if __name__ == "__main__":
    # setup datagen, run everything below these lines
    setupLogging(LOG_PATH, debug=False)
    cache = CachedOutput(ROOT_PATH)
    # end setup, start of mod specific code
    
    with ModelGenerator(cache) as gen:
        for variant in ORES.keys():
            gen.blockCubeAll(MOD_ID, blockName(variant))
    
    # end of datagen, save the cache file
    cache.finalize()