@echo off
echo === Building Full-Stack Application Package ===

rem Save current directory and navigate to project root
set PROJECT_ROOT=%CD%

echo === Building Angular Frontend ===
cd frontend
call npm install
call npm run build

echo === Preparing Backend Resources ===
if not exist "%PROJECT_ROOT%\backend\src\main\resources\webroot" mkdir "%PROJECT_ROOT%\backend\src\main\resources\webroot"
xcopy /E /Y dist\angular-vertx-app\* "%PROJECT_ROOT%\backend\src\main\resources\webroot\"

echo === Building Backend Fat JAR ===
cd "%PROJECT_ROOT%\backend"
call gradle clean shadowJar

echo === Build Complete ===
echo Executable JAR created at: %PROJECT_ROOT%\backend\build\libs\vertx-api-1.0-SNAPSHOT-fat.jar
echo.
echo Run the application with:
echo java -jar %PROJECT_ROOT%\backend\build\libs\vertx-api-1.0-SNAPSHOT-fat.jar

cd %PROJECT_ROOT% 