import logging
from time import perf_counter
from .cache import CachedOutput

THING_ROOT = "things"
BLOCK_ROOT = "block"

class BlockGenerator:
    """
    Handles thingpack block creation.
    Currently, just does gravel like blocks, as this is gravel ores.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
       
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.blocks = 0
        self.time = 0
    
    def __enter__(self) -> "BlockGenerator":
        logging.info("Starting block generation")
        self.time = perf_counter()
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        self.time = perf_counter() - self.time
        logging.info(f"Generated {self.blocks} blocks in {self.time} seconds")
        return False
    
    
    # Methods for datagen root to call
    
    def gravelBlock(self, domain: str, name: str,
                    destroyTime: float = 1.6, explosionResistance: int = 1, itemGroup: str = None) -> None:
        data = {
            # sets any properties not handled by JSON things, e.g. noteblock instruments
            "parent": "minecraft:gravel",
            "type": "falling",
            "dust_color": { "r": 128, "g": 124, "b": 133 }, # gravel's color
            "map_color": "stone",
            "sound_type": "gravel",
            "destroy_time": destroyTime,
            "explosion_resistance": explosionResistance,
            "requires_tool_for_drops": True,
            "item": {}
        }
        if itemGroup is not None:
            data["item"]["group"] = itemGroup
        self.cache.saveJson(data, THING_ROOT, domain, BLOCK_ROOT, name, sortKeys=False)
        self.blocks += 1
        