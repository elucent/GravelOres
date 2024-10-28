# [Simple Gravel Ores](http://minecraft.curseforge.com/projects/simple-gravel-ores)

Simple Gravel Ores is a Minecraft mod designed to add surface ores for a variety of ore blocks, including several modded blocks like tin, lead, and silver.

## Contributing

This mod is written entirely using JSON. The majority of that is done using data packs and resource packs using vanilla features such as loot tables and their recent push for JSON worldgen.

Since data packs don't currently support adding new blocks, the blocks are added via [Json Things](https://www.curseforge.com/minecraft/mc-mods/json-things).

To save effort producing this mod, many of the files are generated using a Python based datagen setup. See [src/main/python](./src/main/python) for details. Any changes that affect existing generated files or that require creating many files should go through datagen. The scripts were written using Python 3.9.6 and entirely built in Python packages.