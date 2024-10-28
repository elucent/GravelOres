import logging
from time import perf_counter
from .cache import CachedOutput

DATA_ROOT = "data"
CONFIGURED_ROOT = "worldgen/configured_feature"
PLACED_ROOT = "worldgen/placed_feature"

class WorldgenGenerator:
    """
    Handles loot table generation.
    Doing mostly simple tables, anything complex we just do manually.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
    
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.configured = 0
        self.placed = 0
        self.time = 0
    
    def __enter__(self) -> "ModelGenerator":
        logging.info("Starting worldgen generation")
        self.time = perf_counter()
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        self.time = perf_counter() - self.time
        logging.info(f"Generated {self.configured} configured features and {self.placed} placed features in {self.time} seconds")
        return False
    
    
    # Methods for datagen root to call
    
    def addPile(self, domain: str, name: str, block: str, chance: int) -> None:
        """Adds JSON for a gravel ore pile, both configured and placed features."""
        configured = {
            "type": "minecraft:block_pile",
            "config": {
                "state_provider": {
                    "type": "minecraft:simple_state_provider",
                    "state": { "Name": block }
                }
            }
        }
        self.cache.saveJson(configured, DATA_ROOT, domain, CONFIGURED_ROOT, name, sortKeys=False)
        self.configured += 1
        
        placed = {
            "feature": f"{domain}:{name}",
            "placement": [
                {
                    "type": "minecraft:rarity_filter",
                    "chance": chance
                },
                { "type": "minecraft:in_square" },
                {
                  "type": "minecraft:heightmap",
                  "heightmap": "MOTION_BLOCKING"
                },
                { "type": "minecraft:biome" }
            ]
        }
        self.cache.saveJson(placed, DATA_ROOT, domain, PLACED_ROOT, name, sortKeys=False)
        self.placed += 1