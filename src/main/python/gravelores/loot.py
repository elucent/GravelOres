import logging
from time import perf_counter
from typing import Optional, Tuple
from .cache import CachedOutput

DATA_ROOT = "data"
BLOCK_TABLE_PATH = "loot_tables/blocks"

class LootTableGenerator:
    """
    Handles loot table generation.
    Doing mostly simple tables, anything complex we just do manually.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
    
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.blockstates = 0
        self.blocks = 0
        self.time = 0
    
    def __enter__(self) -> "ModelGenerator":
        logging.info("Starting loot table generation")
        self.time = perf_counter()
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        self.time = perf_counter() - self.time
        logging.info(f"Generated {self.blocks} block loot tables in {self.time} seconds")
        return False
    
    
    # Methods for datagen root to call
    
    def oreBlock(self, domain: str, name: str, drop: str, tag: bool = False,
                 count: Optional[Tuple[int, int]] = None, flatBonus: bool = False) -> None:
        """Adds a basic ore, with drops boosted by fortune or dropping self with silk touch"""
        data = {
            "type": "minecraft:block",
            "pools": [{
                "bonus_rolls": 0.0,
                "entries": [{
                    "type": "minecraft:alternatives",
                    "children": [
                          {
                              "type": "minecraft:item",
                              "conditions": [{
                                  "condition": "minecraft:match_tool",
                                  "predicate": {
                                      "enchantments": [{
                                          "enchantment": "minecraft:silk_touch",
                                          "levels": { "min": 1 }
                                      }]
                                  }
                              }],
                              "name": f"{domain}:{name}"
                          },
                          {
                              "type": "minecraft:item",
                              "functions": [
                                  {
                                      "enchantment": "minecraft:fortune",
                                      "formula": "minecraft:ore_drops",
                                      "function": "minecraft:apply_bonus"
                                  },
                                  { "function": "minecraft:explosion_decay" }
                              ],
                              "name": drop
                          }
                    ]
                }],
                "rolls": 1.0
            }],
            "random_sequence": f"{domain}:blocks/{name}"
        }
        # redstone ore: swaps out the fortune formula to make it additive instead of multiplicative
        dropObject = data["pools"][0]["entries"][0]["children"][1]
        if flatBonus:
            dropObject["functions"][0] = {
                "enchantment": "minecraft:fortune",
                "formula": "minecraft:uniform_bonus_count",
                "function": "minecraft:apply_bonus",
                "parameters": { "bonusMultiplier": 1 }
            }
        # redstone, lapis, copper: random drop ranges instead of base value of 1
        if count is not None:
            dropObject["functions"].insert(0, {
                "add": False,
                "count": {
                    "type": "minecraft:uniform",
                    "max": count[1],
                    "min": count[0]
                },
                "function": "minecraft:set_count"
            })
        if tag:
            # functions do not apply to expanded tags due to https://bugs.mojang.com/browse/MC-212671
            # as a result, we use a nested loot table so we can make use of applying a function to a pool
            # then fetch an entry from that table for the main drop call
            # TODO 1.21: `name` got replaced by `value` which can be an inline table.
            # dropObject["type"] = "minecraft:tag"
            # dropObject["expand"] = True
            nested = {
                "type": "minecraft:block",
                "pools": [{
                    "bonus_rolls": 0,
                    "entries": [{
                        "type": "minecraft:tag",
                        "name": drop,
                        "expand": True
                    }],
                    "functions": dropObject["functions"],
                    "rolls": 1
                }],
                "random_sequence": f"{domain}:blocks/tag_element/{name}"
            }
            self.cache.saveJson(nested, DATA_ROOT, domain, BLOCK_TABLE_PATH, "tag_element", name, sortKeys=False)
            self.blocks += 1
            
            
            data["pools"][0]["entries"][0]["children"][1] = {
                "type": "minecraft:loot_table",
                "name": f"{domain}:blocks/tag_element/{name}"
            }
        self.cache.saveJson(data, DATA_ROOT, domain, BLOCK_TABLE_PATH, name, sortKeys=False)
        self.blocks += 1