@echo off
rem Чистая сборка SkinTotem / Clean build of SkinTotem
rem
rem   tools\clean_build.bat            -> собирает Fabric
rem   tools\clean_build.bat neoforge   -> собирает NeoForge
rem
rem Скрипт останавливает демон Gradle и удаляет кэши Fabric Loom: именно их loom иногда не может
rem пересоздать сам и падает с "Incompatible Gradle cache. Can't delete it, requires manual deleting".

setlocal
set LOADER=%1
if "%LOADER%"=="" set LOADER=fabric
if not "%LOADER%"=="fabric" if not "%LOADER%"=="neoforge" (
	echo Usage: %~nx0 [fabric^|neoforge]
	exit /b 2
)

pushd "%~dp0.."

echo ==^> Stopping the Gradle daemon
call gradlew.bat --stop >nul 2>&1

echo ==^> Removing Fabric Loom caches
rmdir /s /q "%USERPROFILE%\.gradle\caches\fabric-loom" 2>nul
rmdir /s /q ".gradle\loom-cache" 2>nul
rmdir /s /q "build\loom-cache" 2>nul
for /d %%D in (versions\*) do rmdir /s /q "%%D\build\loom-cache" 2>nul

echo ==^> Building %LOADER%
call gradlew.bat "buildAndCollect+%LOADER%+All" -Pci_loader=%LOADER%
set STATUS=%ERRORLEVEL%

if not "%STATUS%"=="0" (
	echo.
	echo Сборка упала. Если в логе есть "Incompatible Gradle cache", удалите весь кэш Gradle и повторите:
	echo     gradlew.bat --stop ^&^& rmdir /s /q "%USERPROFILE%\.gradle\caches" ^&^& tools\clean_build.bat %LOADER%
	popd
	exit /b %STATUS%
)

echo.
echo ==^> Done. Jars:
dir /b /s "versions\*\build\libs\SkinTotem-*.jar" 2>nul
popd
endlocal
