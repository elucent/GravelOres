import logging
from time import perf_counter
from typing import Any, Dict, List, Union, Tuple
from .cache import CachedOutput

DATA_ROOT = "data"
TAG_ROOT = "tags"

class TagGenerator:
    """
    Handles loot table generation.
    Doing mostly simple tables, anything complex we just do manually.
    """
    
    cache: CachedOutput
    """Cache instance for saving files"""
    tags: Dict[Tuple[str, str], List[Union[str, Dict[str, Any]]]]
    
    def __init__(self, cache: CachedOutput):
        self.cache = cache
        self.tags = {}
        self.time = 0
    
    def __enter__(self) -> "ModelGenerator":
        logging.info("Starting tag generation")
        self.time = perf_counter()
        return self
    
    def __exit__(self, exc_type, exc_value, traceback) -> bool:
        for (domain, name), values in self.tags.items():
            # registry name is baked into the tag name
            self.cache.saveJson({"values": values}, DATA_ROOT, domain, "tags", name)
        
        self.time = perf_counter() - self.time
        logging.info(f"Generated {len(self.tags)} tags in {self.time} seconds")
        self.tags = {}
        return False
    
    def _enforceTagExists(self, registry: str, domain: str, name: str) -> Tuple[str, str]:
        # combine registry and tag name
        key = (domain, f"{registry}/{name}")
        if not key in self.tags:
            self.tags[key] = []
        return key
    
    
    # Methods for datagen root to call

    def add(self, registry: str, domain: str, name: str, *values: str) -> None:
        """Appends the given values to the given tag. Prefix a name with a # to append a tag."""
        key = self._enforceTagExists(registry, domain, name)
        self.tags[key].extend(values)
    
    def addOptional(self, registry: str, domain: str, name: str, *values: str) -> None:
        """Appends the given values as optional tag entries."""
        key = self._enforceTagExists(registry, domain, name)
        self.tags[key].extend([{"id": value, "required": False} for value in values])
        