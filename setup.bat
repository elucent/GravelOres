@echo off

REM setup run directory
mkdir run
mkdir run\mods

REM create dummy jar file with just meta-inf
IF EXIST build RMDIR /q /s build
IF EXIST "run\mods\Gravel-Ores-Dummy.jar" DEL "run\mods\Gravel-Ores-Dummy.jar"
mkdir build
XCOPY src\main\resources\META-INF build\META-INF /s /i /q
copy src\main\resources\pack.mcmeta build
copy src\main\resources\pack.png build
cd build
jar --create --file ../run/mods/Gravel-Ores-Dummy.jar *
cd ..
REM Removing build directory
RMDIR /q /s build

REM create symlinks so resources live update
mkdir run\thingpacks
cd run\thingpacks
IF NOT EXIST GravelOres mklink /J GravelOres ..\..\src\main\resources
IF NOT EXIST GravelOresGenerated mklink /J GravelOresGenerated ..\..\src\generated\resources
cd ..\..