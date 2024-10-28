import logging
from .cache import CachedOutput

ASSET_ROOT = "assets"
BLOCKSTATE_PATH = "blockstates"
MODEL_ROOT = "models"
BLOCK_PATH = "block"
ITEM_PATH = "item"

class ModelGenerator:
    """
    Handles model generation.
    Currently, just no variant block states, item models using a block model, and cube all blocks.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
    
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.blockstates = 0
        self.items = 0
        self.blocks = 0
    
    def __enter__(self) -> "ModelGenerator":
        logging.info("Starting model generation")
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        logging.info(f"Generated {self.blockstates} blockstates, {self.blocks} block models, and {self.items} item models")
        return False
    
    
    # Methods for datagen root to call
    
    def blockstateNoVariants(self, domain: str, name: str) -> None:
        """Creates a block state file with no variants that directs to a block model with the same name"""
        data = { "variants": { "": {
            "model": f"{domain}:{BLOCK_PATH}/{name}"
        }}}
        self.cache.saveJson(data, ASSET_ROOT, domain, BLOCKSTATE_PATH, name)
        self.blockstates += 1
    
    def itemBlockRedirect(self, domain: str, name: str) -> None:
        """Redirects the item model for the given name to the block model for the given name"""
        data = { "parent": f"{domain}:{BLOCK_PATH}/{name}" }
        self.cache.saveJson(data, ASSET_ROOT, domain, MODEL_ROOT, ITEM_PATH, name)
        self.items += 1
    
    def blockCubeAll(self, domain: str, name: str) -> None:
        """
        Creates a block with the same texture on all sides. Uses the block name as the texture name.
        Automatically creates a blockstate and item model.
        """
        data = {
            "parent": "block/cube_all",
            "textures": {
                "all": f"{domain}:{BLOCK_PATH}/{name}"
            }
        }
        self.cache.saveJson(data, ASSET_ROOT, domain, MODEL_ROOT, BLOCK_PATH, name)
        self.blockstateNoVariants(domain, name)
        self.itemBlockRedirect(domain, name)
        self.blocks += 1
        