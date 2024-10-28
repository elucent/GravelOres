import hashlib
import json
import logging
import os
from posixpath import join


from typing import Dict, Set

"""
To reduce tne number of needed IO operations, we follow the same practices as Forge Java datagen.

Instead of immediately writing generated files to disk, we hash the file and only write if the hash changed (or did not exist before).

As a bonus, this allows deleting files that no longer exist.
"""

def hashString(string: str) -> str:
    """
    Hashes the given string using sha256.
    :param string:  String to hash.
    :return:  Hash of the string
    """
    hasher = hashlib.sha256()
    hasher.update(string.encode('utf-8'))
    return hasher.hexdigest()

class CachedOutput:
    """File writer that validates the file contents have changed since the last write before writing (to save on disk IO)."""
    
    rootFolder: str
    """Root path for output"""
    cacheFilePath: str
    """Location of the cache file, for saving at the end"""
    
    oldSize: int
    """Original cache size, used for logging"""
    cache: Dict[str, str]
    """Mapping of root relative paths to their last hash"""
    currentFiles: Set[str]
    """Any paths that were created in the current run. Used to identify stale files from the cache."""
    isDirty: bool
    """If true, then at least 1 file was created or modified. Saves recreating the cache file if not needed"""    

    def __init__(self, rootFolder: str):
        self.rootFolder = rootFolder
        self.cacheFilePath = join(rootFolder, 'cache.json')
        if os.path.exists(self.cacheFilePath):
            with open(self.cacheFilePath, "r") as f:
                self.cache = json.load(f)
                assert isinstance(self.cache, dict), "Cache file must contain JSON object"
                self.oldSize = len(self.cache)
                logging.info(f"Found existing cache file with {self.oldSize} entries")
        else:
            self.cache = dict()
            self.oldSize = 0
        self.currentFiles = set()
        self.isDirty = False
    
    def finalize(self) -> None:
        """
        Finalizes the cache by deleting any old files
        """
        missingPaths = [path for path in self.cache.keys() if not path in self.currentFiles]
        # delete each missing path and remove it from the cache dictionary
        for path in missingPaths:
            os.remove(join(self.rootFolder, path))
            del self.cache[path]
        # cache dictionary should be only existing files
        assert len(self.cache) == len(self.currentFiles)
        
        # save cache dictionary, assuming something changed
        if len(missingPaths) > 0 or self.isDirty:
            with open(self.cacheFilePath, "w") as f:
                json.dump(self.cache, f, indent = 2, sort_keys = True)
        logging.info(f"Finished running datagen. Updated from {self.oldSize} files to {len(self.currentFiles)} with {len(missingPaths)} deletions")
        
        # reset state in case we finalize again
        self.currentFiles = set()
        self.isDirty = False
        self.oldSize = len(self.cache)
    
    def saveText(self, contents: str, path: str, *suffix: str) -> None:
        """
        Saves a text file at the given path, assuming the file does not already exist with those contents.
        :param contents:  Text contents to save.
        :param path:      Ourput path
        :param suffix:    Additional path elements to use in os.path.join.
        """
        if len(suffix) > 0:
            path = join(path, *suffix)
        
        # ensure no duplicate files
        if path in self.currentFiles:
            raise FileExistsError(f"Path {path} has already been created during this datagen run")
        
        # mark the path as saved in this run
        self.currentFiles.add(path)
        
        # enforce trailing newlines in files
        if not contents.endswith('\n'):
            contents = contents + '\n'
        
        # if the hash changed, we need to update the file
        oldhash = self.cache.get(path)
        newhash = hashString(contents)
        if oldhash is None or oldhash != newhash:
            # update hash for later save
            self.cache[path] = newhash
            self.isDirty = True
            
            outputPath = join(self.rootFolder, path)
            os.makedirs(os.path.dirname(outputPath), exist_ok=True)
            with open(outputPath, "w") as f:
                f.write(contents)
            logging.debug(f"Create new file at {path}")
        else:
            logging.debug(f"Skipping saving {path} as the file is unchanged")
    
    def saveJson(self, contents: Dict, path: str, *suffix: str) -> None:
        """
        Saves a json object at the given path, assuming the file does not already exist with those contents.
        :param contents:  JSON object to save.
        :param path:      Ourput path
        :param suffix:    Additional path elements to use in os.path.join.
        """
        self.saveText(json.dumps(contents, indent = 2, sort_keys = True), path, *suffix)
