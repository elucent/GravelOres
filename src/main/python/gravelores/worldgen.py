import logging
from time import perf_counter
from typing import Dict, List, Optional, Set, Tuple
from .cache import CachedOutput

DATA_ROOT = "data"
CONFIGURED_ROOT = "worldgen/configured_feature"
PLACED_ROOT = "worldgen/placed_feature"

    
def _makeModCondition(mod: str) -> Dict:
    return { "type": "forge:mod_loaded", "modid": mod }

def _addConditions(data: Dict, mods: Optional[List[str]] = None) -> None:
    """Adds the mod conditions to the given resource"""
    if mods is not None:
        assert len(mods) > 0
        if len(mods) == 1:
            data["forge:conditions"] = [_makeModCondition(mods[0])]
        else:
            data["forge:conditions"] = [{
                "type": "forge:or",
                "values": [ _makeModCondition(mod) for mod in mods ]
            }]
            
class WorldgenGenerator:
    """
    Handles loot table generation.
    Doing mostly simple tables, anything complex we just do manually.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
    configured: Set[Tuple[str, str]]
    """Set of features already added to skip adding them twice, assumes they would be the same each time."""
    
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.configured = set()
        self.placed = 0
        self.time = 0
    
    def __enter__(self) -> "ModelGenerator":
        logging.info("Starting worldgen generation")
        self.time = perf_counter()
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        self.time = perf_counter() - self.time
        logging.info(f"Generated {len(self.configured)} configured features and {self.placed} placed features in {self.time} seconds")
        self.configured = set()
        return False
    
    
    # Methods for datagen root to call
            
    
    def configurePile(self, domain: str, name: str, block: str, mods: Optional[List[str]] = None) -> None:
        """Adds the configured feature for a pile, if not present already"""
        # skip if the feature was already configured
        # lets us reuse the piles between dimensions
        key = (domain, name)
        if key in self.configured:
            return
        self.configured.add(key)
        
        # add the actual feature
        data = {
            "type": "minecraft:block_pile",
            "config": {
                "state_provider": {
                    "type": "minecraft:simple_state_provider",
                    "state": { "Name": block }
                }
            }
        }
        _addConditions(data, mods)
        self.cache.saveJson(data, DATA_ROOT, domain, CONFIGURED_ROOT, name, sortKeys=False)
    
    def addPile(self, domain: str, name: str, block: str, chance: int, mods: Optional[List[str]] = None) -> None:
        """Adds JSON for a gravel ore pile, both configured and placed features."""
        self.configurePile(domain, name, block, mods)
        data = {
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
        _addConditions(data, mods)
        self.cache.saveJson(data, DATA_ROOT, domain, PLACED_ROOT, name, sortKeys=False)
        self.placed += 1
    
    def addPileNether(self, domain: str, name: str, block: str, chance: int, mods: Optional[List[str]] = None) -> None:
        """Adds JSON for a gravel ore pile, both configured and placed features."""
        self.configurePile(domain, name, block, mods)
        data = {
            "feature": f"{domain}:{name}",
            "placement": [
                {
                    "type": "minecraft:count_on_every_layer",
                    "count": 1
                },
                {
                    "type": "minecraft:rarity_filter",
                    "chance": chance
                },
                { "type": "minecraft:biome" }
            ]
        }
        _addConditions(data, mods)
        self.cache.saveJson(data, DATA_ROOT, domain, PLACED_ROOT, name + "_nether", sortKeys=False)
        self.placed += 1