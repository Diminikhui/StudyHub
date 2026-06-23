@echo off
cd /d %~dp0\..
call scripts\build.bat
if errorlevel 1 exit /b 1
java -cp "build\MirIgrushek.jar;lib\postgresql-42.7.7.jar" ru.mirigrushek.app.Main
