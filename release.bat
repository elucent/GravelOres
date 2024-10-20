@echo off

IF EXIST build RMDIR /q /s build
IF EXIST "Gravel-Ores-#.#.#.jar" DEL "Gravel-Ores-#.#.#.jar"
MKDIR build

REM Copy required files into build directory
XCOPY src\generated\resources build /s /i /q /exclude:pack.mcmeta
XCOPY src\main\resources build /s /i /q

REM Zipping contents
cd build
REM TODO: locate version number from mods.toml
jar --create --file ../Gravel-Ores-#.#.#.jar *
cd ..

REM Removing build directory
RMDIR /q /s build
