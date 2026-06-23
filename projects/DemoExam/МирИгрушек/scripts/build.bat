@echo off
cd /d %~dp0\..
if not exist build\classes mkdir build\classes
for /r build\classes %%f in (*) do del "%%f"
dir /s /b src\*.java > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1
jar --create --file build\MirIgrushek.jar --main-class ru.mirigrushek.app.Main -C build\classes .
echo Сборка создана: build\MirIgrushek.jar
