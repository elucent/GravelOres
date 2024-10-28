from gravelores.cache import CachedOutput
from gravelores.logger import setupLogging


ROOT_PATH = "../../generated/resources"
"""Target folder for datagen"""

LOG_PATH = "../../../run/logs"
"""Path to create log files for datagen, set to None to skip"""

MOD_ID = "gravelores"
"""Mod ID for all resources generated"""

if __name__ == "__main__":
    # setup datagen, run everything below these lines
    setupLogging(LOG_PATH, debug=True)
    cache = CachedOutput(ROOT_PATH)
    # end setup, start of mod specific code
    
    # end of datagen, save the cache file
    cache.finalize()